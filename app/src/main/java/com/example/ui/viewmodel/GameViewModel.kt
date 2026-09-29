package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DareRepository
import com.example.data.dao.TopMomentDetail
import com.example.data.model.DareCardEntity
import com.example.data.model.DareLogEntity
import com.example.data.model.GameSessionEntity
import com.example.data.model.UserEntity
import com.example.domain.CategoryAffinity
import com.example.domain.GameConstants
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameScreen {
    WELCOME,
    SAFETY_SETUP,
    PLAYER_SETUP,
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
    CUSTOM_DARES,
    PAST_SESSIONS,
    LEARNED_PATTERNS,
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
    val playerSafetyClamps: Map<String, Int> = emptyMap(), // playerId -> clamp level
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
    val activeDareLogId: Long? = null,
    val topMomentsList: List<TopMomentDetail> = emptyList(),
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
    private val applicationContext: Context
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

    private val prefs = applicationContext.getSharedPreferences("uncomfortable_cards_prefs", Context.MODE_PRIVATE)

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

    private val safetyClamp = mutableMapOf<String, Int>()

    init {
        // Pre-populate default database cards on first launch
        viewModelScope.launch {
            AppDatabase.getInstance(applicationContext).prepopulateCards()
            val allMuted = repository.getAllMutedCardIds().size
            _uiState.value = _uiState.value.copy(learnedUncomfortableCount = allMuted)
        }
        viewModelScope.launch {
            repository.allLogs.collect { logs ->
                calculateLearnedPatterns(logs)
            }
        }
    }

    // Safety Clamp per-player (Task 4)
    fun setYellowClamp(playerId: String, clampLevel: Int = 2) {
        safetyClamp[playerId] = clampLevel
        _uiState.value = _uiState.value.copy(
            safetyLevel = SafetyLevel.YELLOW,
            playerSafetyClamps = safetyClamp.toMap()
        )
    }

    fun clearYellowClamp(playerId: String) {
        safetyClamp.remove(playerId)
        val newLevel = if (safetyClamp.isEmpty()) SafetyLevel.GREEN else SafetyLevel.YELLOW
        _uiState.value = _uiState.value.copy(
            safetyLevel = newLevel,
            playerSafetyClamps = safetyClamp.toMap()
        )
    }

    fun effectiveMaxLevel(participantIds: List<String>, zoneCap: Int): Int {
        val clamps = participantIds.mapNotNull { safetyClamp[it] }
        return (clamps + zoneCap).minOrNull() ?: zoneCap
    }

    private suspend fun calculateLearnedPatterns(logs: List<DareLogEntity>) {
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

        val uncomfortableIds = repository.getAllMutedCardIds().toSet()
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
        safetyClamp.clear()
        _uiState.value = GameUiState(
            currentScreen = GameScreen.BOARD_OVERVIEW,
            players = players.map { it.copy(position = 0, points = 0) },
            currentPlayerIndex = 0,
            safeWord = safeWord.ifBlank { "Pineapple" },
            highestZoneUnlocked = 1,
            isGameInProgress = true,
            usedCardIds = emptySet(),
            playerSafetyClamps = emptyMap()
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
        val state = _uiState.value
        val used = state.usedCardIds
        val active = state.activePlayer
        val activeUserId = active?.userId ?: active?.id?.hashCode()?.toLong() ?: 0L

        val participantUserIds = mutableSetOf(activeUserId)
        state.selectedPartnerIds.forEach { pid ->
            val p = state.players.find { it.id == pid }
            val pUid = p?.userId ?: p?.id?.hashCode()?.toLong()
            if (pUid != null) participantUserIds.add(pUid)
        }

        val mutedCardIds = mutableSetOf<Long>()
        participantUserIds.forEach { uid ->
            mutedCardIds.addAll(repository.getMutedCardIds(uid))
        }

        val excluded = used + mutedCardIds + (if (excludeId != null) setOf(excludeId) else emptySet())

        // Calculate effective level based on per-player yellow safety clamps and zone cap! (Task 4)
        val participantPlayerIds = if (active != null) listOf(active.id) + state.selectedPartnerIds else emptyList()
        val effectiveLevel = effectiveMaxLevel(participantPlayerIds, level)

        val boostCustom = prefs.getBoolean("boost_custom_dares", true)

        var cards = repository.getCardsByCategoryAndLevel(category, effectiveLevel)
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
            for (l in effectiveLevel downTo 1) {
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
            for (l in (effectiveLevel + 1)..4) {
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

    private fun prepareCardSetup(cardIn: DareCardEntity?) {
        val card = cardIn ?: GameConstants.EMERGENCY_CARD
        if (cardIn == null) {
            android.util.Log.w("GameViewModel", "Emergency fallback card used — deck may be exhausted for this player")
        }
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
            partnerConsentStatus = partnerIds.associateWith { true }, // Default opt-in
            timerRemaining = card.timerSeconds ?: 0,
            isTimerRunning = false,
            isTimerFinished = false,
            uncomfortableCardPending = null,
            currentScreen = GameScreen.CARD_REVEAL
        )
    }

    fun selectPartner(partnerId: String) {
        val state = _uiState.value
        val current = state.selectedPartnerIds
        val updated = if (current.contains(partnerId)) {
            if (current.size > 1) current - partnerId else current // Keep at least one partner for paired
        } else {
            current + partnerId
        }
        _uiState.value = state.copy(
            selectedPartnerIds = updated,
            partnerConsentStatus = updated.associateWith { true }
        )
    }

    fun togglePartnerConsent(playerId: String, consent: Boolean) {
        val state = _uiState.value
        val updated = state.partnerConsentStatus.toMutableMap()
        updated[playerId] = consent
        _uiState.value = state.copy(partnerConsentStatus = updated)
    }

    fun redrawDifferentCard() {
        val current = _uiState.value.currentCard ?: return
        viewModelScope.launch {
            val card = drawCard(current.category, current.level, excludeId = current.id)
                ?: drawCard("WILDCARD", 1)
            prepareCardSetup(card)
        }
    }

    // Learning System: Mark card uncomfortable for active player (Task 3)
    fun markCurrentCardUncomfortable() {
        val state = _uiState.value
        val card = state.currentCard ?: return
        val active = state.activePlayer ?: return
        val userId = active.userId ?: active.id.hashCode().toLong()

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
            repository.muteCard(userId, card.id)
            val logId = repository.logDare(log)
            val mutedCount = repository.getMutedCardIds(userId).size

            _uiState.value = _uiState.value.copy(
                learnedUncomfortableCount = mutedCount,
                uncomfortableCardPending = card,
                activeDareLogId = logId,
                floatBadgeText = "🛡️ Learned: Won't suggest again to ${active.name}",
                recentScoreLogs = listOf(log) + state.recentScoreLogs
            )
        }

        viewModelScope.launch {
            delay(2500)
            if (_uiState.value.floatBadgeText?.startsWith("🛡️ Learned") == true) {
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

    fun downgradeToSolo() = downgradeToSoloDare()

    fun getUncomfortableCardIds(): Set<String> {
        return _learnedPatterns.value.blockedCardIds.map { it.toString() }.toSet()
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
        viewModelScope.launch {
            repository.clearAllMutedCards()
            _uiState.value = _uiState.value.copy(
                learnedUncomfortableCount = 0,
                floatBadgeText = "↺ Disliked dare memory reset"
            )
            _learnedPatterns.value = _learnedPatterns.value.copy(
                blockedCardIds = emptySet(),
                blockedCards = emptyList()
            )
            delay(2000)
            _uiState.value = _uiState.value.copy(floatBadgeText = null)
        }
    }

    fun unblockCard(cardId: Long) {
        viewModelScope.launch {
            repository.unmuteCardForAll(cardId)
            val updatedIds = repository.getAllMutedCardIds().toSet()
            val updatedCards = repository.getCardsByIds(updatedIds.toList())
            _uiState.value = _uiState.value.copy(learnedUncomfortableCount = updatedIds.size)
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
        viewModelScope.launch {
            repository.clearAllMutedCards()
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
            playerName = active?.name ?: "Game",
            cardText = "EMERGENCY SAFE WORD TRIGGERED (${state.safeWord})",
            category = "SAFETY",
            level = 1,
            outcome = "EMERGENCY_STOP",
            points = 0
        )
        viewModelScope.launch {
            repository.logDare(log)
        }
        _uiState.value = state.copy(
            isTimerRunning = false,
            safetyLevel = SafetyLevel.RED,
            emergencySafeWordTriggered = true,
            floatBadgeText = "🛑 Emergency Stop Triggered"
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
            cardText = "Double Dare Bonus Awarded",
            category = "BONUS",
            level = 2,
            outcome = "BONUS",
            points = GameConstants.BONUS_POINTS
        )

        viewModelScope.launch {
            val logId = repository.logDare(log)
            _uiState.value = _uiState.value.copy(activeDareLogId = logId)
        }

        _uiState.value = state.copy(
            players = updatedPlayers,
            floatBadgeText = "+${GameConstants.BONUS_POINTS} Bonus Points!",
            recentScoreLogs = listOf(log) + state.recentScoreLogs
        )

        viewModelScope.launch {
            delay(1600)
            if (_uiState.value.floatBadgeText?.contains("Bonus") == true) {
                _uiState.value = _uiState.value.copy(floatBadgeText = null)
            }
        }
    }

    // Hearts Reaction (Task 5)
    fun giveHeart(dareLogId: Long? = null, sessionId: Long = 0L) {
        val logId = dareLogId ?: _uiState.value.activeDareLogId ?: return
        viewModelScope.launch {
            repository.giveHeart(logId, sessionId)
            _uiState.value = _uiState.value.copy(
                floatBadgeText = "🖤 Loved this moment!"
            )
            delay(2000)
            if (_uiState.value.floatBadgeText == "🖤 Loved this moment!") {
                _uiState.value = _uiState.value.copy(floatBadgeText = null)
            }
        }
    }

    fun loadTopMoments(sessionId: Long = 0L) {
        viewModelScope.launch {
            val counts = repository.getTopMoments(sessionId)
            val allLogsList = repository.allLogs.firstOrNull() ?: emptyList()
            val details = counts.mapNotNull { count ->
                val log = allLogsList.find { it.id == count.dareLogId }
                if (log != null) {
                    TopMomentDetail(
                        dareLogId = log.id,
                        playerName = log.playerName,
                        cardText = log.cardText,
                        category = log.category,
                        heartCount = count.heartCount
                    )
                } else null
            }
            _uiState.value = _uiState.value.copy(topMomentsList = details)
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
            val logId = repository.logDare(log)
            _uiState.value = _uiState.value.copy(activeDareLogId = logId)
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
            level = 2,
            outcome = "SWAP",
            points = 0
        )

        viewModelScope.launch {
            val logId = repository.logDare(log)
            _uiState.value = _uiState.value.copy(activeDareLogId = logId)
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
        val nextIndex = (state.currentPlayerIndex + 1) % state.players.size
        _uiState.value = state.copy(
            currentPlayerIndex = nextIndex,
            rollOutcome = null,
            currentCard = null,
            bonusCard = null,
            specialSpace = null,
            uncomfortableCardPending = null,
            activeDareLogId = null,
            currentScreen = GameScreen.ROLL_SCREEN
        )
    }

    fun endGame() {
        loadTopMoments(0L)
        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreen.GAME_OVER,
            isGameInProgress = false
        )
    }

    fun addCustomDare(category: String, level: Int, text: String, scope: String, timer: Int?) {
        val newCard = DareCardEntity(
            category = category,
            level = level,
            text = text,
            scope = scope,
            timerSeconds = if (timer != null && timer > 0) timer else null,
            isCustom = true
        )
        viewModelScope.launch {
            repository.addCustomCard(newCard)
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
            val db = AppDatabase.getInstance(context.applicationContext)
            val repo = DareRepository(db)
            return GameViewModel(repo, context.applicationContext) as T
        }
    }
}
