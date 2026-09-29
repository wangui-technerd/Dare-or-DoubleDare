package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.components.PauseModalSheet
import com.example.ui.components.RulesModalSheet
import com.example.ui.components.SafetyModalSheet
import com.example.ui.components.ScoreHistoryModalSheet
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CustomDaresScreen
import com.example.ui.screens.GameOverScreen
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.LearnedPatternsScreen
import com.example.ui.screens.PastGamesScreen
import com.example.ui.screens.PlayerSetupScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.GameScreen
import com.example.ui.viewmodel.GameViewModel

enum class AppDestination {
    WELCOME,
    AUTH,
    PLAYER_SETUP,
    GAME_PLAY,
    GAME_OVER,
    CUSTOM_DARES,
    PAST_GAMES,
    LEARNED_PATTERNS
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = ViewModelProvider(this, AuthViewModel.Factory(this))[AuthViewModel::class.java]
        val gameViewModel = ViewModelProvider(this, GameViewModel.Factory(this))[GameViewModel::class.java]

        setContent {
            val isDark by gameViewModel.isDarkMode.collectAsState()
            MyApplicationTheme(isDark = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colors.background
                ) {
                    DareAppRoot(
                        authViewModel = authViewModel,
                        gameViewModel = gameViewModel,
                        isDark = isDark,
                        onToggleTheme = { gameViewModel.toggleTheme() }
                    )
                }
            }
        }
    }
}

@Composable
fun DareAppRoot(
    authViewModel: AuthViewModel,
    gameViewModel: GameViewModel,
    isDark: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val authState by authViewModel.uiState.collectAsState()
    val authUsers by authViewModel.allUsers.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()

    val allCards by gameViewModel.allCards.collectAsState()
    val allSessions by gameViewModel.allSessions.collectAsState()
    val learnedPatterns by gameViewModel.learnedPatterns.collectAsState()

    var destination by remember { mutableStateOf(AppDestination.WELCOME) }

    // Modal bottom sheet visibility states
    var showSafetyModal by remember { mutableStateOf(false) }
    var showScoreHistoryModal by remember { mutableStateOf(false) }
    var showPauseModal by remember { mutableStateOf(false) }
    var showRulesModal by remember { mutableStateOf(false) }

    // Sync game over state
    LaunchedEffect(gameState.currentScreen) {
        if (gameState.currentScreen == GameScreen.GAME_OVER) {
            destination = AppDestination.GAME_OVER
        }
    }

    // Handle System Back button gracefully
    BackHandler(enabled = destination != AppDestination.WELCOME) {
        when (destination) {
            AppDestination.GAME_PLAY -> showPauseModal = true
            AppDestination.GAME_OVER -> destination = AppDestination.WELCOME
            else -> destination = AppDestination.WELCOME
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (destination) {
            AppDestination.WELCOME -> {
                WelcomeScreen(
                    currentUser = authState.currentUser,
                    isDark = isDark,
                    onToggleTheme = onToggleTheme,
                    onStartGame = { destination = AppDestination.PLAYER_SETUP },
                    onOpenAuth = { destination = AppDestination.AUTH },
                    onOpenRules = { showRulesModal = true },
                    onOpenCustomDares = { destination = AppDestination.CUSTOM_DARES },
                    onOpenPastGames = { destination = AppDestination.PAST_GAMES },
                    onOpenLearnedPatterns = { destination = AppDestination.LEARNED_PATTERNS }
                )
            }

            AppDestination.AUTH -> {
                AuthScreen(
                    currentUser = authState.currentUser,
                    allUsers = authUsers,
                    errorMessage = authState.errorMessage,
                    successMessage = authState.successMessage,
                    onRegister = { username, displayName, avatarEmoji, avatarColorHex, safeWord, intensity ->
                        authViewModel.register(
                            username, displayName, avatarEmoji, avatarColorHex, safeWord, intensity
                        )
                    },
                    onSelectUser = { user ->
                        authViewModel.selectUser(user)
                        destination = AppDestination.WELCOME
                    },
                    onContinueAsGuest = {
                        authViewModel.continueAsGuest()
                        destination = AppDestination.WELCOME
                    },
                    onLogout = {
                        authViewModel.logout()
                    },
                    onBack = {
                        authViewModel.clearMessages()
                        destination = AppDestination.WELCOME
                    }
                )
            }

            AppDestination.PLAYER_SETUP -> {
                PlayerSetupScreen(
                    currentUser = authState.currentUser,
                    allSavedUsers = authUsers,
                    onStartGame = { players, safeWord, sharePoints ->
                        gameViewModel.setupGame(players, safeWord)
                        if (sharePoints != gameState.sharePointsWithPartners) {
                            gameViewModel.toggleSharePoints()
                        }
                        destination = AppDestination.GAME_PLAY
                    },
                    onBack = { destination = AppDestination.WELCOME }
                )
            }

            AppDestination.GAME_PLAY -> {
                GamePlayScreen(
                    state = gameState,
                    isDark = isDark,
                    onToggleTheme = onToggleTheme,
                    onRollDice = {
                        if (gameState.currentScreen == GameScreen.BOARD_OVERVIEW) {
                            gameViewModel.startPlaying()
                        }
                        gameViewModel.rollDice()
                    },
                    onConfirmMoveAndDraw = { chosenCategory ->
                        gameViewModel.confirmMoveAndDraw(chosenCategory)
                    },
                    onContinueZoneUnlock = { gameViewModel.continueAfterZoneUnlock() },
                    onDrawJackpot = { gameViewModel.drawJackpot() },
                    onSelectPartner = { partnerId -> gameViewModel.selectPartner(partnerId) },
                    onToggleConsent = { playerId, consent ->
                        gameViewModel.togglePartnerConsent(playerId, consent)
                    },
                    onDowngradeToSolo = { gameViewModel.downgradeToSolo() },
                    onRedrawCard = { gameViewModel.redrawDifferentCard() },
                    onStartTimer = { gameViewModel.startTimer() },
                    onAwardBonus = { gameViewModel.awardBonus() },
                    onMarkUncomfortable = { gameViewModel.markCurrentCardUncomfortable() },
                    onDrawReplacement = { gameViewModel.drawReplacementCard() },
                    onResolveCard = { outcome, isBonus ->
                        gameViewModel.resolveCard(outcome, isBonus)
                    },
                    onSwapPositions = { targetPlayerId ->
                        gameViewModel.swapPositions(targetPlayerId)
                    },
                    onAdvanceTurn = { gameViewModel.advanceToNextTurn() },
                    onEndGame = { gameViewModel.endGame() },
                    onGiveHeart = { gameViewModel.giveHeart() },
                    onClearEmergencySafeWord = { gameViewModel.clearEmergencySafeWord() },
                    onOpenSafetyModal = { showSafetyModal = true },
                    onOpenScoreHistory = { showScoreHistoryModal = true },
                    onOpenPauseModal = { showPauseModal = true },
                    onOpenRulesModal = { showRulesModal = true }
                )
            }

            AppDestination.GAME_OVER -> {
                GameOverScreen(
                    players = gameState.players,
                    topMoments = gameState.topMomentsList,
                    onPlayAgain = {
                        gameViewModel.setupGame(
                            gameState.players.map { it.copy(position = 0, points = 0) },
                            gameState.safeWord
                        )
                        destination = AppDestination.GAME_PLAY
                    },
                    onNewGame = {
                        destination = AppDestination.WELCOME
                    },
                    onViewPastGames = {
                        destination = AppDestination.PAST_GAMES
                    }
                )
            }

            AppDestination.CUSTOM_DARES -> {
                CustomDaresScreen(
                    allCards = allCards,
                    isDark = isDark,
                    onToggleTheme = onToggleTheme,
                    onAddCustomCard = { category, level, scope, text, timerSeconds ->
                        gameViewModel.addCustomDare(category, level, text, scope, timerSeconds)
                    },
                    onToggleFavorite = { card ->
                        gameViewModel.toggleFavoriteCard(card.id, !card.isFavorite)
                    },
                    onDeleteCard = { card ->
                        gameViewModel.deleteCustomCard(card.id)
                    },
                    onBack = { destination = AppDestination.WELCOME }
                )
            }

            AppDestination.PAST_GAMES -> {
                PastGamesScreen(
                    sessions = allSessions,
                    onDeleteSession = { sessionId ->
                        gameViewModel.deletePastSession(sessionId)
                    },
                    onBack = { destination = AppDestination.WELCOME }
                )
            }

            AppDestination.LEARNED_PATTERNS -> {
                LearnedPatternsScreen(
                    patterns = learnedPatterns,
                    isDark = isDark,
                    onToggleTheme = onToggleTheme,
                    onToggleAdaptiveEngine = { gameViewModel.toggleAdaptiveEngine(it) },
                    onToggleBoostCustomDares = { gameViewModel.toggleBoostCustomDares(it) },
                    onUnblockCard = { gameViewModel.unblockCard(it) },
                    onResetLearnedPatterns = { gameViewModel.clearLearnedPatterns() },
                    onBack = { destination = AppDestination.WELCOME }
                )
            }
        }

        // Global Bottom Sheets
        if (showSafetyModal) {
            SafetyModalSheet(
                currentLevel = gameState.safetyLevel,
                safeWord = gameState.safeWord,
                learnedUncomfortableCount = gameViewModel.getUncomfortableCardIds().size,
                onSelectLevel = { level ->
                    gameViewModel.setSafetyLevel(level)
                },
                onResetUncomfortableCards = {
                    gameViewModel.resetLearnedUncomfortableCards()
                },
                onEmergencySafeWord = {
                    gameViewModel.triggerEmergencySafeWord()
                },
                onDismiss = { showSafetyModal = false }
            )
        }

        if (showScoreHistoryModal) {
            ScoreHistoryModalSheet(
                recentLogs = gameState.recentScoreLogs,
                onDismiss = { showScoreHistoryModal = false }
            )
        }

        if (showPauseModal) {
            PauseModalSheet(
                onResume = { showPauseModal = false },
                onSafetyCheck = {
                    showPauseModal = false
                    showSafetyModal = true
                },
                onViewScores = {
                    showPauseModal = false
                    showScoreHistoryModal = true
                },
                onViewPatterns = {
                    showPauseModal = false
                    destination = AppDestination.LEARNED_PATTERNS
                },
                onEndGame = {
                    showPauseModal = false
                    gameViewModel.endGame()
                },
                onExitToMenu = {
                    showPauseModal = false
                    destination = AppDestination.WELCOME
                },
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                onDismiss = { showPauseModal = false }
            )
        }

        if (showRulesModal) {
            RulesModalSheet(
                onDismiss = { showRulesModal = false }
            )
        }
    }
}
