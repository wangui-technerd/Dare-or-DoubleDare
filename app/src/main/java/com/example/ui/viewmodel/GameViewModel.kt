package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DareRepository
import com.example.data.model.DareCardEntity
import com.example.data.model.DareLogEntity
import com.example.data.model.GameSessionEntity
import com.example.data.model.UserEntity
import com.example.domain.CategoryAffinity
import com.example.domain.GameConstants
import com.example.domain.LEVEL_INFO
import com.example.domain.LearnedPatterns
import com.example.domain.Player
import com.example.domain.RollOutcome
import com.example.domain.SafetyLevel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameScreen {
    WELCOME,
    AUTH,
    PROFILE,
    PLAYERS_SETUP,
    SAFETY_SETUP,
    BOARD_OVERVIEW,
    ROLL_SCREEN,
    ROLL_RESULT,
    ZONE_UNLOCK,
    SWAP_SCREEN,
    JACKPOT_INTRO,
    PARTNER_SELECTION,
    CONSENT_CHECK,
    CARD_REVEAL,
    DOUBLE_DARE_REVEAL,
    TURN_SUMMARY,
    GAME_OVER,
    PAST_GAMES,
    CUSTOM_DARES,
    RULES
}

data class GameUiState(
    val currentScreen: GameScreen = GameScreen.WELCOME,
    val players: List<Player> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val rollOutcome: RollOutcome? = null,
    val currentCard: DareCardEntity? = null,
    val bonusCard: DareCardEntity? = null,
    val cardScope: String = "solo",
    val selectedPartnerIds: List<String> = emptyList(),
    val partnerConsentStatus: Map<String, Boolean> = emptyMap(), // true = in, false = out
    val safetyLevel: SafetyLevel = SafetyLevel.GREEN,
    val safeWord: String = "Pineapple",
    val sharePointsWithPartners: Boolean = false,
    val highestZoneUnlocked: Int = 1,
    val pendingZoneUnlock: Int? = null,
    val pendingCategory: String? = null,
    val specialSpace: String? = null, // SWAP, DOUBLE_DARE, CONFESS, JACKPOT
    val lastResolution: String? = null, // completed, skipped, notcomfortable, partial, swap
    val timerRemaining: Int = 0,
    val isTimerRunning: Boolean = false,
    val isTimerFinished: Boolean = false,
    val uncomfortableCardPending: DareCardEntity? = null,
    val learnedUncomfortableCount: Int = 0,
    val emergencySafeWordTriggered: Boolean = false,
    val floatBadgeText: String? = null,
    val showSafetyModal: Boolean = false,
    val showScoreHistoryModal: Boolean = false,
    val showPauseModal: Boolean = false,
    val showBonusModal: Boolean = false,
    val recentScoreLogs: List<DareLogEntity> = emptyList(),
    val usedCardIds: Set<Long> = emptySet(),
    val isGameInProgress: Boolean = false
) {
    val activePlayer: Player?
        get() = players.getOrNull(currentPlayerIndex)
}

class GameViewModel(
    private val repository: DareRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val allCards: StateFlow<List<DareCardEntity>> = repository.allCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customCards: StateFlow<List<DareCardEntity>> = repository.customCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCards: StateFlow<List<DareCardEntity>> = repository.favoriteCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions: StateFlow<List<GameSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var timerJob: Job? = null

    private val prefs = context.getSharedPreferences("uncomfortable_cards_prefs", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(
        prefs.getBoolean("is_dark_mode", true)
    )
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        prefs.edit().putBoolean("is_dark_mode", next).apply()
    }

    private val _learnedPatterns = MutableStateFlow(LearnedPatterns())
    val learnedPatterns: StateFlow<LearnedPatterns> = _learnedPatterns.asStateFlow()

    init {
        // Pre-populate default database cards on first launch
        viewModelScope.launch {
            AppDatabase.getInstance(context).prepopulateCards()
        }
        _uiState.value = _uiState.value.copy(
            learnedUncomfortableCount = getLearnedUncomfortableIds().size
        )
        viewModelScope.launch {
            repository.allLogs.collect { logs ->
                calculateLearnedPatterns(logs)
            }
        }
    }

    private fun calculateLearnedPatterns(logs: List<DareLogEntity>) {
        val total = logs.size
        val completed = logs.count { it.outcome == "COMPLETED" || it.outcome == "BONUS" }
        val uncomfortable = logs.count { it.outcome == "UNCOMFORTABLE" }
        val skipped = logs.count { it.outcome == "SKIPPED" }
        val overallRate = if (total > 0) (completed * 100 / total) else 0

        val categoryAffinities = GameConstants.CATEGORIES.map { cat ->
            val catLogs = logs.filter {
                it.category.equals(cat.key, ignoreCase = true) ||
                it.category.replace("_", " ").equals(cat.key, ignoreCase = true)
            }
            val presented = catLogs.size
            val comp = catLogs.count { it.outcome == "COMPLETED" || it.outcome == "BONUS" }
            val affinity = if (presented > 0) (comp * 100 / presented) else 100
            val status = when {
                presented == 0 -> "Untested"
                affinity >= 85 -> "Favorite 🔥"
                affinity >= 60 -> "High Comfort ✨"
                affinity >= 40 -> "Exploring 🧪"
                else -> "Cautious ⚠️"
            }
            CategoryAffinity(
                categoryKey = cat.key,
                categoryLabel = cat.label,
                emoji = cat.emoji,
                totalPresented = presented,
                completedCount = comp,
                affinityPercent = affinity,
                status = status
            )
        }

        val topCategories = categoryAffinities
            .filter { it.totalPresented > 0 }
            .sortedByDescending { it.affinityPercent * 1000 + it.completedCount }
            .take(3)

        val levelMap = (1..4).associateWith { lvl ->
            val lvlLogs = logs.filter { it.level == lvl }
            val lvlTotal = lvlLogs.size
            val lvlComp = lvlLogs.count { it.outcome == "COMPLETED" || it.outcome == "BONUS" }
            if (lvlTotal > 0) (lvlComp * 100 / lvlTotal) else 100
        }

        val sweetSpot = levelMap.maxByOrNull { it.value }?.key ?: 2

        val (archetype, desc) = when {
            total < 3 -> "Sensual Explorer" to "Discovering mutual chemistry, boundaries, and favorite game rhythms"
            topCategories.any { it.categoryKey in listOf("TENSION", "TROUBLE") } ->
                "Magnetic Alchemist" to "Excited by intense eye contact, electric proximity, and teasing anticipation"
            topCategories.any { it.categoryKey == "POWER" } ->
                "Command & Surrender" to "Enjoys playful dominance, trust dynamics, and seductive obedience"
            topCategories.any { it.categoryKey == "AFTER DARK" } ->
                "Midnight Provocateur" to "Drawn to unfiltered spice, secret confessions, and late-night intimacy"
            topCategories.any { it.categoryKey in listOf("CONFESSIONS", "ADMITTING") } ->
                "Deep Confessor" to "Thrives on radical honesty, unspoken desires, and emotional vulnerability"
            topCategories.any { it.categoryKey in listOf("SENSATION", "ROLEPLAY") } ->
                "Sensory Artisan" to "Captivated by touch, temperature contrasts, textures, and playful immersion"
            topCategories.any { it.categoryKey in listOf("ICEBREAKER", "PRAISE") } ->
                "Affectionate Charmer" to "Builds connection through sweet praise, playful giggles, and charming warmth"
            else -> "Intimate All-Rounder" to "Adventurous, versatile, and eager to explore all facets of the game"
        }

        val uncomfortableIds = getLearnedUncomfortableIds()

        viewModelScope.launch {
            val blockedCards = repository.getCardsByIds(uncomfortableIds.toList())
            val adaptiveOn = prefs.getBoolean("adaptive_engine_enabled", true)
            val boostCustomOn = prefs.getBoolean("boost_custom_dares", true)

            _learnedPatterns.value = LearnedPatterns(
                totalDaresPlayed = total,
                totalCompleted = completed,
                totalUncomfortable = uncomfortable,
                totalSkipped = skipped,
                overallCompletionRate = overallRate,
                categoryAffinities = categoryAffinities,
                topCategories = topCategories,
                intensityComfortPercents = levelMap,
                intensitySweetSpot = sweetSpot,
                playstyleArchetype = archetype,
                archetypeDescription = desc,
                blockedCardIds = uncomfortableIds,
                blockedCards = blockedCards,
                adaptiveEngineEnabled = adaptiveOn,
                boostCustomDares = boostCustomOn
            )
        }
    }

    fun getLearnedUncomfortableIds(): Set<Long> {
        val raw = prefs.getStringSet("blocked_card_ids", emptySet()) ?: emptySet()
        return raw.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun navigateTo(screen: GameScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun openSafetyModal() {
        _uiState.value = _uiState.value.copy(showSafetyModal = true)
    }

    fun closeSafetyModal() {
        _uiState.value = _uiState.value.copy(showSafetyModal = false)
    }

    fun setSafetyLevel(level: SafetyLevel) {
        _uiState.value = _uiState.value.copy(safetyLevel = level)
        if (level == SafetyLevel.RED) {
            _uiState.value = _uiState.value.copy(showSafetyModal = true)
        }
    }

    fun setSafeWord(word: String) {
        _uiState.value = _uiState.value.copy(safeWord = word.ifBlank { "Pineapple" })
    }

    fun toggleSharePoints() {
        _uiState.value = _uiState.value.copy(sharePointsWithPartners = !_uiState.value.sharePointsWithPartners)
    }

    fun toggleScoreHistory(show: Boolean) {
        _uiState.value = _uiState.value.copy(showScoreHistoryModal = show)
    }

    fun togglePauseModal(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPauseModal = show)
    }

    fun toggleBonusModal(show: Boolean) {
        _uiState.value = _uiState.value.copy(showBonusModal = show)
    }

    fun setupGame(players: List<Player>, safeWord: String) {
        _uiState.value = GameUiState(
            currentScreen = GameScreen.BOARD_OVERVIEW,
            players = players.map { it.copy(position = 0, points = 0) },
            currentPlayerIndex = 0,
            safeWord = safeWord.ifBlank { "Pineapple" },
            highestZoneUnlocked = 1,
            isGameInProgress = true,
            usedCardIds = emptySet()
        )
    }

    fun startPlaying() {
        _uiState.value = _uiState.value.copy(currentScreen = GameScreen.ROLL_SCREEN)
    }

    fun rollDice() {
        val active = _uiState.value.activePlayer ?: return
        val catDie = Random.nextInt(1, 13) // 1..12: 1..11 are all categories, 12 is Wild Pick!
        val intDie = Random.nextInt(1, 7) // 1..6

        val rolledCategory = GameConstants.categoryDieMapping(catDie)
        val rolledLevel = when {
            intDie <= 2 -> 1
            intDie <= 4 -> 2
            intDie == 5 -> 3
            else -> 4
        }

        val currentCap = GameConstants.zoneCapFor(active.position)
        val cappedLevel = minOf(rolledLevel, currentCap)
        val movement = (((catDie - 1) % 6) + 1) + intDie // 2..12 movement spaces for balanced 40-space board

        val outcome = RollOutcome(
            catDie = catDie,
            intDie = intDie,
            category = rolledCategory,
            rolledLevel = rolledLevel,
            cappedLevel = cappedLevel,
            movement = movement,
            isWildPick = rolledCategory == "WILDPICK",
            wasCapped = rolledLevel > cappedLevel
        )

        _uiState.value = _uiState.value.copy(
            rollOutcome = outcome,
            currentScreen = GameScreen.ROLL_RESULT
        )
    }

    fun confirmMoveAndDraw(chosenCategory: String? = null) {
        val state = _uiState.value
        val roll = state.rollOutcome ?: return
        val active = state.activePlayer ?: return
        val category = chosenCategory ?: roll.category

        val prevZone = GameConstants.zoneOf(active.position)
        val newPos = minOf(GameConstants.BOARD_LENGTH, active.position + roll.movement)
        val newZone = GameConstants.zoneOf(newPos)

        val updatedPlayers = state.players.mapIndexed { idx, p ->
            if (idx == state.currentPlayerIndex) p.copy(position = newPos) else p
        }

        _uiState.value = state.copy(players = updatedPlayers)

        // Check if a new higher zone is unlocked for the first time
        if (newZone > state.highestZoneUnlocked && newZone <= 4) {
            _uiState.value = _uiState.value.copy(
                highestZoneUnlocked = newZone,
                pendingZoneUnlock = newZone,
                pendingCategory = category,
                currentScreen = GameScreen.ZONE_UNLOCK
            )
            return
        }

        landOnSpace(category)
    }

    fun continueAfterZoneUnlock() {
        val state = _uiState.value
        val cat = state.pendingCategory ?: state.rollOutcome?.category ?: "WILDCARD"
        _uiState.value = state.copy(pendingZoneUnlock = null, pendingCategory = null)
        landOnSpace(if (cat == "WILDPICK") "WILDCARD" else cat)
    }

    private fun landOnSpace(category: String) {
        val state = _uiState.value
        val active = state.activePlayer ?: return
        val special = GameConstants.SPECIAL_SPACES[active.position]

        if (special != null) {
            _uiState.value = _uiState.value.copy(specialSpace = special)
            when (special) {
                "JACKPOT" -> {
                    _uiState.value = _uiState.value.copy(currentScreen = GameScreen.JACKPOT_INTRO)
                    return
                }
                "SWAP" -> {
                    _uiState.value = _uiState.value.copy(currentScreen = GameScreen.SWAP_SCREEN)
                    return
                }
                "CONFESS" -> {
                    viewModelScope.launch {
                        val card = drawCard("CONFESSIONS", 1) ?: drawCard("ADMITTING", 1) ?: drawCard("WILDCARD", 1)
                        prepareCardSetup(card)
                    }
                    return
                }
                "DOUBLE_DARE" -> {
                    viewModelScope.launch {
                        val level = state.rollOutcome?.cappedLevel ?: 1
                        val card1 = drawCard(category, level)
                        val card2 = drawCard(category, level, excludeId = card1?.id)
                        _uiState.value = _uiState.value.copy(bonusCard = card2)
                        prepareCardSetup(card1)
                    }
                    return
                }
            }
        }

        val level = state.rollOutcome?.cappedLevel ?: 1
        viewModelScope.launch {
            val card = drawCard(category, level)
            prepareCardSetup(card)
        }
    }

    fun drawJackpot() {
        viewModelScope.launch {
            val card = drawCard("WILDCARD", 4) ?: drawCard("WILDCARD", 3)
            prepareCardSetup(card)
        }
    }

    private suspend fun drawCard(
        category: String,
        level: Int,
        excludeId: Long? = null,
        scopePreference: String? = null
    ): DareCardEntity? {
        val used = _uiState.value.usedCardIds
        val uncomfortable = getLearnedUncomfortableIds()
        val excluded = used + uncomfortable + (if (excludeId != null) setOf(excludeId) else emptySet())

        val boostCustom = prefs.getBoolean("boost_custom_dares", true)

        var cards = repository.getCardsByCategoryAndLevel(category, level)
            .filter { !excluded.contains(it.id) }

        if (boostCustom) {
            val customCandidates = cards.filter { it.isCustom }
            if (customCandidates.isNotEmpty() && Random.nextFloat() < 0.65f) {
                val picked = customCandidates.random()
                _uiState.value = _uiState.value.copy(usedCardIds = _uiState.value.usedCardIds + picked.id)
                return picked
            }
        }

        if (scopePreference != null) {
            val scoped = cards.filter { it.scope.equals(scopePreference, ignoreCase = true) }
            if (scoped.isNotEmpty()) cards = scoped
        }

        if (cards.isEmpty()) {
            // Fallback to nearby levels downward
            for (l in level downTo 1) {
                cards = repository.getCardsByCategoryAndLevel(category, l)
                    .filter { !excluded.contains(it.id) }
                if (scopePreference != null) {
                    val scoped = cards.filter { it.scope.equals(scopePreference, ignoreCase = true) }
                    if (scoped.isNotEmpty()) cards = scoped
                }
                if (cards.isNotEmpty()) break
            }
        }

        if (cards.isEmpty()) {
            // Fallback to nearby levels upward within the category
            for (l in (level + 1)..4) {
                cards = repository.getCardsByCategoryAndLevel(category, l)
                    .filter { !excluded.contains(it.id) }
                if (scopePreference != null) {
                    val scoped = cards.filter { it.scope.equals(scopePreference, ignoreCase = true) }
                    if (scoped.isNotEmpty()) cards = scoped
                }
                if (cards.isNotEmpty()) break
            }
        }

        if (cards.isEmpty()) {
            // Category-wide fallback
            cards = repository.getCardsByCategory(category)
                .filter { !excluded.contains(it.id) }
            if (scopePreference != null) {
                val scoped = cards.filter { it.scope.equals(scopePreference, ignoreCase = true) }
                if (scoped.isNotEmpty()) cards = scoped
            }
        }

        if (cards.isEmpty()) {
            // Ultimate fallback to any available card not excluded
            cards = allCards.value.filter { !excluded.contains(it.id) }
            if (scopePreference != null) {
                val scoped = cards.filter { it.scope.equals(scopePreference, ignoreCase = true) }
                if (scoped.isNotEmpty()) cards = scoped
            }
        }

        // Safety fallback if all available cards are marked uncomfortable
        if (cards.isEmpty()) {
            cards = allCards.value.filter { it.id != excludeId }
        }

        val card = cards.randomOrNull()
        if (card != null) {
            _uiState.value = _uiState.value.copy(usedCardIds = _uiState.value.usedCardIds + card.id)
        }
        return card
    }

    private fun prepareCardSetup(card: DareCardEntity?) {
        if (card == null) return
        val state = _uiState.value
        val active = state.activePlayer ?: return
        val others = state.players.filter { it.id != active.id }

        val scope = card.scope.lowercase()
        val partnerIds: List<String> = when {
            scope == "solo" -> emptyList()
            scope == "group" -> others.map { it.id }
            others.size == 1 -> listOf(others.first().id) // 2-player game: always the other player!
            state.selectedPartnerIds.isNotEmpty() -> state.selectedPartnerIds
            others.isNotEmpty() -> listOf(others.first().id)
            else -> emptyList()
        }

        // Direct reveal! Both parties see the dare on screen together.
        _uiState.value = state.copy(
            currentCard = card,
            cardScope = scope,
            selectedPartnerIds = partnerIds,
            partnerConsentStatus = partnerIds.associateWith { true },
            uncomfortableCardPending = null,
            timerRemaining = card.timerSeconds ?: 0,
            isTimerRunning = false,
            isTimerFinished = false,
            currentScreen = GameScreen.CARD_REVEAL
        )
    }

    fun selectPartner(partnerId: String) {
        _uiState.value = _uiState.value.copy(
            selectedPartnerIds = listOf(partnerId),
            partnerConsentStatus = mapOf(partnerId to true),
            currentScreen = GameScreen.CARD_REVEAL
        )
    }

    fun togglePartnerConsent(partnerId: String, isIn: Boolean) {
        val current = _uiState.value.partnerConsentStatus.toMutableMap()
        current[partnerId] = isIn
        _uiState.value = _uiState.value.copy(partnerConsentStatus = current)
    }

    fun downgradeToSolo() {
        _uiState.value = _uiState.value.copy(
            cardScope = "solo",
            selectedPartnerIds = emptyList(),
            partnerConsentStatus = emptyMap(),
            uncomfortableCardPending = null,
            currentScreen = GameScreen.CARD_REVEAL
        )
    }

    fun redrawDifferentCard() {
        val current = _uiState.value.currentCard ?: return
        viewModelScope.launch {
            val card = drawCard(current.category, current.level, excludeId = current.id)
                ?: drawCard("WILDCARD", 1)
            prepareCardSetup(card)
        }
    }

    // Learning System: Mark card uncomfortable, mute it permanently for players, and offer replacement/solo
    fun markCurrentCardUncomfortable() {
        val state = _uiState.value
        val card = state.currentCard ?: return
        val active = state.activePlayer ?: return

        val currentSet = prefs.getStringSet("blocked_card_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(card.id.toString())
        prefs.edit().putStringSet("blocked_card_ids", currentSet).apply()

        val log = DareLogEntity(
            gameSessionId = 0,
            playerName = active.name,
            cardText = card.text,
            category = card.category,
            level = card.level,
            outcome = "UNCOMFORTABLE",
            points = 0
        )

        viewModelScope.launch {
            repository.logDare(log)
        }

        _uiState.value = state.copy(
            learnedUncomfortableCount = currentSet.size,
            uncomfortableCardPending = card,
            floatBadgeText = "🛡️ Learned: Won't suggest again",
            recentScoreLogs = listOf(log) + state.recentScoreLogs
        )

        viewModelScope.launch {
            delay(2500)
            if (_uiState.value.floatBadgeText == "🛡️ Learned: Won't suggest again") {
                _uiState.value = _uiState.value.copy(floatBadgeText = null)
            }
        }
    }

    fun drawReplacementDare() {
        val state = _uiState.value
        val current = state.currentCard ?: return
        viewModelScope.launch {
            val card = drawCard(current.category, current.level, excludeId = current.id)
                ?: drawCard("WILDCARD", maxOf(1, current.level - 1), excludeId = current.id)
                ?: drawCard("WILDCARD", 1)
            prepareCardSetup(card)
        }
    }

    fun drawReplacementCard() = drawReplacementDare()

    fun getUncomfortableCardIds(): Set<String> {
        return prefs.getStringSet("blocked_card_ids", emptySet()) ?: emptySet()
    }

    fun downgradeToSoloDare() {
        val state = _uiState.value
        val current = state.currentCard
        val category = current?.category ?: "WILDCARD"
        val level = current?.level ?: 1
        viewModelScope.launch {
            val soloCard = drawCard(category, level, scopePreference = "solo")
                ?: drawCard("WILDCARD", 1, scopePreference = "solo")
            if (soloCard != null) {
                prepareCardSetup(soloCard)
            } else {
                _uiState.value = state.copy(
                    cardScope = "solo",
                    selectedPartnerIds = emptyList(),
                    uncomfortableCardPending = null,
                    currentScreen = GameScreen.CARD_REVEAL
                )
            }
        }
    }

    fun passUncomfortableTurn() {
        resolveCard("notcomfortable", isBonusCard = false)
    }

    fun resetLearnedUncomfortableCards() {
        prefs.edit().remove("blocked_card_ids").apply()
        _uiState.value = _uiState.value.copy(
            learnedUncomfortableCount = 0,
            floatBadgeText = "↺ Disliked dare memory reset"
        )
        _learnedPatterns.value = _learnedPatterns.value.copy(
            blockedCardIds = emptySet(),
            blockedCards = emptyList()
        )
        viewModelScope.launch {
            delay(2000)
            _uiState.value = _uiState.value.copy(floatBadgeText = null)
        }
    }

    fun unblockCard(cardId: Long) {
        val current = prefs.getStringSet("blocked_card_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        current.remove(cardId.toString())
        prefs.edit().putStringSet("blocked_card_ids", current).apply()
        val updatedIds = current.mapNotNull { it.toLongOrNull() }.toSet()
        _uiState.value = _uiState.value.copy(learnedUncomfortableCount = updatedIds.size)
        viewModelScope.launch {
            val updatedCards = repository.getCardsByIds(updatedIds.toList())
            _learnedPatterns.value = _learnedPatterns.value.copy(
                blockedCardIds = updatedIds,
                blockedCards = updatedCards
            )
        }
    }

    fun toggleAdaptiveEngine(enabled: Boolean) {
        prefs.edit().putBoolean("adaptive_engine_enabled", enabled).apply()
        _learnedPatterns.value = _learnedPatterns.value.copy(adaptiveEngineEnabled = enabled)
    }

    fun toggleBoostCustomDares(enabled: Boolean) {
        prefs.edit().putBoolean("boost_custom_dares", enabled).apply()
        _learnedPatterns.value = _learnedPatterns.value.copy(boostCustomDares = enabled)
    }

    fun clearLearnedPatterns() {
        prefs.edit().remove("blocked_card_ids").apply()
        viewModelScope.launch {
            repository.clearAllLogs()
            _uiState.value = _uiState.value.copy(learnedUncomfortableCount = 0)
            _learnedPatterns.value = LearnedPatterns()
        }
    }

    fun triggerEmergencySafeWord() {
        timerJob?.cancel()
        val state = _uiState.value
        val active = state.activePlayer
        val log = DareLogEntity(
            gameSessionId = 0,
            playerName = active?.name ?: "Player",
            cardText = "SAFE WORD TRIGGERED (${state.safeWord})",
            category = "SAFETY",
            level = 0,
            outcome = "SAFEWORD",
            points = 0
        )
        viewModelScope.launch {
            repository.logDare(log)
        }
        _uiState.value = state.copy(
            safetyLevel = SafetyLevel.RED,
            emergencySafeWordTriggered = true,
            isTimerRunning = false,
            timerRemaining = 0,
            currentCard = null,
            bonusCard = null,
            recentScoreLogs = listOf(log) + state.recentScoreLogs
        )
    }

    fun clearEmergencySafeWord() {
        _uiState.value = _uiState.value.copy(
            emergencySafeWordTriggered = false,
            safetyLevel = SafetyLevel.GREEN,
            currentScreen = GameScreen.ROLL_SCREEN
        )
    }

    fun startTimer() {
        val seconds = _uiState.value.currentCard?.timerSeconds ?: return
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            timerRemaining = seconds,
            isTimerRunning = true,
            isTimerFinished = false
        )
        timerJob = viewModelScope.launch {
            for (sec in seconds downTo 0) {
                _uiState.value = _uiState.value.copy(timerRemaining = sec)
                if (sec == 0) {
                    _uiState.value = _uiState.value.copy(
                        isTimerRunning = false,
                        isTimerFinished = true
                    )
                    break
                }
                delay(1000)
            }
        }
    }

    fun awardBonus() {
        val state = _uiState.value
        val active = state.activePlayer ?: return
        val updatedPlayers = state.players.mapIndexed { idx, p ->
            if (idx == state.currentPlayerIndex) p.copy(points = p.points + GameConstants.BONUS_POINTS) else p
        }

        val log = DareLogEntity(
            gameSessionId = 0,
            playerName = active.name,
            cardText = state.currentCard?.text ?: "Bonus action",
            category = state.currentCard?.category ?: "SPECIAL",
            level = state.currentCard?.level ?: 1,
            outcome = "BONUS",
            points = GameConstants.BONUS_POINTS
        )

        _uiState.value = state.copy(
            players = updatedPlayers,
            floatBadgeText = "+${GameConstants.BONUS_POINTS} BONUS ✨",
            recentScoreLogs = listOf(log) + state.recentScoreLogs
        )

        viewModelScope.launch {
            repository.logDare(log)
            if (active.userId != null && active.userId > 0) {
                repository.updateUserStats(active.userId, 0, 0, GameConstants.BONUS_POINTS)
            }
            delay(1600)
            _uiState.value = _uiState.value.copy(floatBadgeText = null)
        }
    }

    fun resolveCard(outcome: String, isBonusCard: Boolean = false) {
        val state = _uiState.value
        val card = if (isBonusCard) state.bonusCard else state.currentCard ?: return
        if (card == null) return
        val active = state.activePlayer ?: return

        var pts = 0
        var updatedPlayers = state.players

        if (outcome == "completed") {
            pts = GameConstants.computePoints(card.level, card.scope)
            if (state.sharePointsWithPartners && state.selectedPartnerIds.isNotEmpty()) {
                val partnerCount = state.selectedPartnerIds.size
                val share = maxOf(1, pts / (partnerCount + 1))
                updatedPlayers = state.players.map { p ->
                    if (p.id == active.id || state.selectedPartnerIds.contains(p.id)) {
                        p.copy(points = p.points + share)
                    } else p
                }
            } else {
                updatedPlayers = state.players.mapIndexed { idx, p ->
                    if (idx == state.currentPlayerIndex) p.copy(points = p.points + pts) else p
                }
            }
        }

        val log = DareLogEntity(
            gameSessionId = 0,
            playerName = active.name,
            cardText = card.text,
            category = card.category,
            level = card.level,
            outcome = outcome.uppercase(),
            points = pts
        )

        viewModelScope.launch {
            repository.logDare(log)
            if (outcome == "completed" && active.userId != null && active.userId > 0) {
                repository.updateUserStats(active.userId, 0, 1, pts)
            }
        }

        val updatedScoreLogs = listOf(log) + state.recentScoreLogs

        // Check if there is a double dare bonus card waiting
        if (!isBonusCard && state.bonusCard != null) {
            val nextCard = state.bonusCard
            val others = state.players.filter { it.id != active.id }
            val nextPartners = if (others.size == 1) listOf(others.first().id) else state.selectedPartnerIds

            _uiState.value = state.copy(
                players = updatedPlayers,
                lastResolution = outcome,
                recentScoreLogs = updatedScoreLogs,
                currentCard = nextCard,
                bonusCard = null,
                cardScope = nextCard.scope.lowercase(),
                selectedPartnerIds = nextPartners,
                timerRemaining = nextCard.timerSeconds ?: 0,
                isTimerRunning = false,
                isTimerFinished = false,
                uncomfortableCardPending = null,
                currentScreen = GameScreen.DOUBLE_DARE_REVEAL
            )
            return
        }

        _uiState.value = state.copy(
            players = updatedPlayers,
            lastResolution = outcome,
            recentScoreLogs = updatedScoreLogs,
            bonusCard = null,
            uncomfortableCardPending = null,
            currentScreen = GameScreen.TURN_SUMMARY
        )
    }

    fun swapPositions(targetPlayerId: String) {
        val state = _uiState.value
        val active = state.activePlayer ?: return
        val target = state.players.find { it.id == targetPlayerId } ?: return

        val updatedPlayers = state.players.map { p ->
            when (p.id) {
                active.id -> p.copy(position = target.position)
                target.id -> p.copy(position = active.position)
                else -> p
            }
        }

        val log = DareLogEntity(
            gameSessionId = 0,
            playerName = active.name,
            cardText = "Swapped board positions with ${target.name} (Space ${target.position})",
            category = "SPECIAL",
            level = 0,
            outcome = "SWAP",
            points = 0
        )

        viewModelScope.launch {
            repository.logDare(log)
        }

        _uiState.value = state.copy(
            players = updatedPlayers,
            lastResolution = "swap",
            recentScoreLogs = listOf(log) + state.recentScoreLogs,
            currentScreen = GameScreen.TURN_SUMMARY
        )
    }

    fun advanceToNextTurn() {
        val state = _uiState.value
        val active = state.activePlayer ?: return

        if (active.position >= GameConstants.BOARD_LENGTH) {
            endGame()
            return
        }

        val nextIndex = (state.currentPlayerIndex + 1) % state.players.size
        _uiState.value = state.copy(
            currentPlayerIndex = nextIndex,
            currentCard = null,
            bonusCard = null,
            rollOutcome = null,
            selectedPartnerIds = emptyList(),
            partnerConsentStatus = emptyMap(),
            specialSpace = null,
            timerRemaining = 0,
            isTimerRunning = false,
            currentScreen = GameScreen.ROLL_SCREEN
        )
    }

    fun endGame() {
        val state = _uiState.value
        val sorted = state.players.sortedByDescending { it.points }
        val winner = sorted.firstOrNull()

        val playersSummary = state.players.joinToString(", ") { "${it.name}: ${it.points}pts" }

        val session = GameSessionEntity(
            winnerName = winner?.name ?: "No one",
            winnerPoints = winner?.points ?: 0,
            playersJson = playersSummary,
            roundsCount = state.recentScoreLogs.size,
            daresCompletedCount = state.recentScoreLogs.count { it.outcome == "COMPLETED" || it.outcome == "BONUS" },
            safeWordUsed = state.safetyLevel == SafetyLevel.RED
        )

        viewModelScope.launch {
            repository.saveGameSession(session)
            // Update stats for all registered users in the game
            state.players.forEach { p ->
                if (p.userId != null && p.userId > 0) {
                    repository.updateUserStats(p.userId, 1, 0, 0)
                }
            }
        }

        _uiState.value = state.copy(
            isGameInProgress = false,
            currentScreen = GameScreen.GAME_OVER
        )
    }

    // Custom Dares & Favorites
    fun addCustomDare(category: String, level: Int, text: String, scope: String, timer: Int?) {
        viewModelScope.launch {
            repository.addCustomCard(
                DareCardEntity(
                    category = category.uppercase(),
                    level = level.coerceIn(1, 4),
                    text = text.trim(),
                    scope = scope.lowercase(),
                    timerSeconds = if (timer != null && timer > 0) timer else null,
                    isCustom = true
                )
            )
        }
    }

    fun toggleFavoriteCard(cardId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(cardId, isFavorite)
        }
    }

    fun deleteCustomCard(cardId: Long) {
        viewModelScope.launch {
            repository.deleteCustomCard(cardId)
        }
    }

    fun deletePastSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(context)
            val repo = DareRepository(
                db.userDao(),
                db.gameSessionDao(),
                db.dareCardDao(),
                db.dareLogDao()
            )
            return GameViewModel(repo, context) as T
        }
    }
}
