package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.GameConstants
import com.example.domain.LEVEL_INFO
import com.example.domain.Player
import com.example.ui.components.BoardTrackView
import com.example.ui.components.SafetyFloatingPill
import com.example.ui.components.ScoreStrip
import com.example.ui.components.SeductiveGoldButton
import com.example.ui.components.SeductivePrimaryButton
import com.example.ui.components.SeductiveSecondaryButton
import com.example.ui.components.TopBarHeader
import com.example.ui.components.parseColor
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AppTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.LightGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyYellow
import com.example.ui.theme.SoftCharcoal
import com.example.ui.theme.VelvetWine
import com.example.ui.viewmodel.GameScreen
import com.example.ui.viewmodel.GameUiState

@Composable
fun GamePlayScreen(
    state: GameUiState,
    isDark: Boolean = true,
    onToggleTheme: (() -> Unit)? = null,
    onRollDice: () -> Unit,
    onConfirmMoveAndDraw: (chosenCategory: String?) -> Unit,
    onContinueZoneUnlock: () -> Unit,
    onDrawJackpot: () -> Unit,
    onSelectPartner: (String) -> Unit,
    onToggleConsent: (String, Boolean) -> Unit,
    onDowngradeToSolo: () -> Unit,
    onRedrawCard: () -> Unit,
    onStartTimer: () -> Unit,
    onAwardBonus: () -> Unit,
    onMarkUncomfortable: () -> Unit,
    onDrawReplacement: () -> Unit,
    onResolveCard: (outcome: String, isBonus: Boolean) -> Unit,
    onSwapPositions: (String) -> Unit,
    onAdvanceTurn: () -> Unit,
    onEndGame: () -> Unit,
    onGiveHeart: () -> Unit = {},
    onClearEmergencySafeWord: () -> Unit,
    onOpenSafetyModal: () -> Unit,
    onOpenScoreHistory: () -> Unit,
    onOpenPauseModal: () -> Unit,
    onOpenRulesModal: () -> Unit
) {
    val scrollState = rememberScrollState()
    val active = state.activePlayer ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 72.dp)
        ) {
            // Top Navigation & Scores
            TopBarHeader(
                onScoreClick = onOpenScoreHistory,
                onPauseClick = onOpenPauseModal,
                onRulesClick = onOpenRulesModal,
                isDark = isDark,
                onToggleTheme = onToggleTheme
            )

            ScoreStrip(
                players = state.players,
                currentPlayerIndex = state.currentPlayerIndex
            )

            BoardTrackView(
                players = state.players
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Main Dynamic Gameplay Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                when (state.currentScreen) {
                    GameScreen.BOARD_OVERVIEW -> {
                        BoardOverviewContent(
                            state = state,
                            onStartPlaying = onRollDice
                        )
                    }

                    GameScreen.ROLL_SCREEN -> {
                        RollPromptContent(
                            activePlayer = active,
                            onRoll = onRollDice
                        )
                    }

                    GameScreen.ROLL_RESULT -> {
                        RollResultContent(
                            state = state,
                            onConfirmMove = onConfirmMoveAndDraw
                        )
                    }

                    GameScreen.ZONE_UNLOCK -> {
                        ZoneUnlockContent(
                            zoneLevel = state.pendingZoneUnlock ?: state.highestZoneUnlocked,
                            onContinue = onContinueZoneUnlock
                        )
                    }

                    GameScreen.SWAP_SCREEN -> {
                        SwapSpaceContent(
                            activePlayer = active,
                            otherPlayers = state.players.filter { it.id != active.id },
                            onSwap = onSwapPositions
                        )
                    }

                    GameScreen.JACKPOT_INTRO -> {
                        JackpotIntroContent(
                            activePlayer = active,
                            onDrawJackpot = onDrawJackpot
                        )
                    }

                    GameScreen.PARTNER_SELECTION -> {
                        PartnerSelectionContent(
                            activePlayer = active,
                            otherPlayers = state.players.filter { it.id != active.id },
                            onSelect = onSelectPartner
                        )
                    }

                    GameScreen.CONSENT_CHECK,
                    GameScreen.CARD_REVEAL -> {
                        CardRevealContent(
                            state = state,
                            isBonusCard = false,
                            onStartTimer = onStartTimer,
                            onAwardBonus = onAwardBonus,
                            onMarkUncomfortable = onMarkUncomfortable,
                            onDrawReplacement = onDrawReplacement,
                            onDowngradeToSolo = onDowngradeToSolo,
                            onRedrawCard = onRedrawCard,
                            onSelectPartner = onSelectPartner,
                            onResolve = { outcome -> onResolveCard(outcome, false) }
                        )
                    }

                    GameScreen.DOUBLE_DARE_REVEAL -> {
                        CardRevealContent(
                            state = state,
                            isBonusCard = true,
                            onStartTimer = onStartTimer,
                            onAwardBonus = onAwardBonus,
                            onMarkUncomfortable = onMarkUncomfortable,
                            onDrawReplacement = onDrawReplacement,
                            onDowngradeToSolo = onDowngradeToSolo,
                            onRedrawCard = onRedrawCard,
                            onSelectPartner = onSelectPartner,
                            onResolve = { outcome -> onResolveCard(outcome, true) }
                        )
                    }

                    GameScreen.TURN_SUMMARY -> {
                        TurnSummaryContent(
                            state = state,
                            onContinue = onAdvanceTurn,
                            onEndGame = onEndGame,
                            onGiveHeart = onGiveHeart
                        )
                    }

                    else -> {
                        RollPromptContent(
                            activePlayer = active,
                            onRoll = onRollDice
                        )
                    }
                }
            }
        }

        // Floating Bonus Badge Animation
        AnimatedVisibility(
            visible = state.floatBadgeText != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(ChampagneGold)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = state.floatBadgeText ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack
                    )
                )
            }
        }

        // Floating Safety Pill
        SafetyFloatingPill(
            safetyLevel = state.safetyLevel,
            safeWord = state.safeWord,
            onClick = onOpenSafetyModal,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        )

        // Emergency Safe Word Full Overlay
        if (state.emergencySafeWordTriggered) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ObsidianBlack.copy(alpha = 0.95f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, CrimsonRed, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCardElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🛑 SAFE WORD TRIGGERED",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CrimsonRed,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Safe word: \"${state.safeWord}\"",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = ChampagneGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "All timers, dares, and gameplay have been halted immediately. Take all the time you need to check in. There is never any penalty or pressure.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CreamWhite,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        SeductivePrimaryButton(
                            text = "RESUME GAME WHEN READY",
                            onClick = onClearEmergencySafeWord,
                            testTag = "resume_after_safeword_btn"
                        )
                    }
                }
            }
        }
    }
}

// ---------------- Sub-Screens / Components ----------------

@Composable
private fun BoardOverviewContent(
    state: GameUiState,
    onStartPlaying: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "INTIMATE BOARD READY",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp,
                color = ChampagneGold,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "40 Spaces • 4 Intensity Zones",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Roll two dice each turn: one picks category, one picks intensity capped by your zone.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = SoftCharcoal,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        SeductivePrimaryButton(
            text = "START FIRST ROLL 🎲",
            onClick = onStartPlaying,
            testTag = "start_first_roll_btn"
        )
    }
}

@Composable
private fun RollPromptContent(
    activePlayer: Player,
    onRoll: () -> Unit
) {
    val cap = GameConstants.zoneCapFor(activePlayer.position)
    val capMeta = LEVEL_INFO[cap] ?: LEVEL_INFO[1]!!

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${activePlayer.name.uppercase()}'S TURN",
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 2.sp,
                color = ChampagneGold,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Roll the Dice",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Current Board Zone: ${capMeta.emoji} ${capMeta.name} Ceiling (Space ${activePlayer.position})",
            style = MaterialTheme.typography.bodySmall.copy(
                color = parseColor(capMeta.colorHex),
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Tactile 3D Dice Display
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DieBox("?", "CATEGORY", ChampagneGold)
            DieBox("?", "INTENSITY", CrimsonRed)
        }

        Spacer(modifier = Modifier.height(34.dp))

        SeductivePrimaryButton(
            text = "🎲 ROLL THE DICE",
            onClick = onRoll,
            testTag = "roll_dice_btn"
        )
    }
}

@Composable
private fun DieBox(value: String, label: String, accentColor: Color) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ObsidianCardElevated,
                        ObsidianBlack
                    )
                )
            )
            .border(2.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    color = LightGold
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = SoftCharcoal,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
private fun RollResultContent(
    state: GameUiState,
    onConfirmMove: (chosenCategory: String?) -> Unit
) {
    val roll = state.rollOutcome ?: return
    val active = state.activePlayer ?: return
    val lvlMeta = LEVEL_INFO[roll.cappedLevel] ?: LEVEL_INFO[1]!!

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ROLL REVEALED",
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 2.sp,
                color = ChampagneGold,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dice Result Display
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DieBox("${roll.catDie}", roll.category, ChampagneGold)
            DieBox("${roll.intDie}", "LEVEL ${roll.cappedLevel}", parseColor(lvlMeta.colorHex))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(ObsidianCard)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${lvlMeta.emoji} ${roll.category} • LEVEL ${roll.cappedLevel} (${lvlMeta.name})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = parseColor(lvlMeta.colorHex)
                    )
                )
                if (roll.wasCapped) {
                    Text(
                        text = "(Intensity rolled ${roll.rolledLevel}, capped at Level ${roll.cappedLevel} by your board zone)",
                        style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal, fontSize = 11.sp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                val targetSpace = minOf(GameConstants.BOARD_LENGTH, active.position + roll.movement)
                Text(
                    text = "Move ${roll.movement} spaces: Space ${active.position} ➔ Space $targetSpace",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = LightGold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (roll.isWildPick) {
            Text(
                text = "🃏 You rolled Wild Pick! Choose your category:",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LightGold
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GameConstants.CATEGORIES.forEach { cat ->
                    SeductiveSecondaryButton(
                        text = "${cat.emoji} ${cat.label.uppercase()}",
                        onClick = { onConfirmMove(cat.key) },
                        testTag = "wildpick_${cat.key}"
                    )
                }
            }
        } else {
            SeductivePrimaryButton(
                text = "ADVANCE & DRAW CARD ➔",
                onClick = { onConfirmMove(null) },
                testTag = "move_and_draw_btn"
            )
        }
    }
}

@Composable
private fun ZoneUnlockContent(
    zoneLevel: Int,
    onContinue: () -> Unit
) {
    val meta = LEVEL_INFO[zoneLevel] ?: LEVEL_INFO[1]!!
    val color = parseColor(meta.colorHex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.5f),
                        ObsidianBlack
                    )
                )
            )
            .border(2.dp, ChampagneGold, RoundedCornerShape(24.dp))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ZONE UNLOCKED",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 3.sp,
                    color = LightGold,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = meta.emoji, fontSize = 54.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = meta.name,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    color = CreamWhite,
                    letterSpacing = 2.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = meta.tagline,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = LightGold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            SeductiveGoldButton(
                text = "ENTER ZONE ➔",
                onClick = onContinue,
                testTag = "zone_continue_btn"
            )
        }
    }
}

@Composable
private fun SwapSpaceContent(
    activePlayer: Player,
    otherPlayers: List<Player>,
    onSwap: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔀 CHANCE SPACE", style = MaterialTheme.typography.labelMedium.copy(color = ChampagneGold, letterSpacing = 2.sp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Swap Positions!",
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif, color = CreamWhite)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${activePlayer.name}, choose a player to trade board locations with.",
            style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            otherPlayers.forEach { other ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianCardElevated)
                        .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { onSwap(other.id) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Trade with ${other.name}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CreamWhite)
                        )
                        Text(
                            text = "Space ${other.position}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = LightGold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JackpotIntroContent(
    activePlayer: Player,
    onDrawJackpot: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏆 FINAL SPACE 40", style = MaterialTheme.typography.labelMedium.copy(color = ChampagneGold, letterSpacing = 2.sp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "JACKPOT SHOWDOWN", style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Serif, color = LightGold, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${activePlayer.name} has reached the end of the board! One final maximum intensity dare to determine the crown.",
            style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(24.dp))
        SeductiveGoldButton(
            text = "DRAW JACKPOT DARE 🏆",
            onClick = onDrawJackpot,
            testTag = "draw_jackpot_btn"
        )
    }
}

@Composable
private fun PartnerSelectionContent(
    activePlayer: Player,
    otherPlayers: List<Player>,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CHOOSE A PARTNER", style = MaterialTheme.typography.labelMedium.copy(color = ChampagneGold, letterSpacing = 2.sp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${activePlayer.name}, who is this dare with?",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Serif, color = CreamWhite)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Select who you would like to invite into this dare.",
            style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            otherPlayers.forEach { partner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianCardElevated)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                        .clickable { onSelect(partner.id) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(parseColor(partner.colorHex))
                            )
                            Text(
                                text = partner.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CreamWhite)
                            )
                        }
                        Text("SELECT ➔", style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold))
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsentCheckContent(
    activePlayer: Player,
    partnerIds: List<String>,
    partnerConsentStatus: Map<String, Boolean>,
    allPlayers: List<Player>,
    scope: String,
    onToggleConsent: (String, Boolean) -> Unit,
    onDowngradeToSolo: () -> Unit,
    onRedraw: () -> Unit,
    onConfirmed: () -> Unit
) {
    val partners = allPlayers.filter { partnerIds.contains(it.id) }
    val anyOptedOut = partnerConsentStatus.values.any { it == false }
    val allOptedIn = partners.isNotEmpty() && partners.all { partnerConsentStatus[it.id] == true }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CONSENT CHECK",
            style = MaterialTheme.typography.labelMedium.copy(color = ChampagneGold, letterSpacing = 2.sp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (scope == "group") "Is everyone in?" else "${partners.firstOrNull()?.name ?: "Partner"}, you in?",
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif, color = CreamWhite)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${activePlayer.name} drew a dare that includes you. Confirm your consent before it's revealed.",
            style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Opt-in cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            partners.forEach { partner ->
                val isIn = partnerConsentStatus[partner.id] == true
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isIn) VelvetWine.copy(alpha = 0.4f) else ObsidianCard)
                        .border(
                            1.5.dp,
                            if (isIn) ChampagneGold else ObsidianBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = partner.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = CreamWhite)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isIn) SafetyGreen else ObsidianCardElevated)
                                    .clickable { onToggleConsent(partner.id, true) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("I'm in ✓", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CreamWhite))
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (!isIn && partnerConsentStatus[partner.id] == false) SafetyRed else ObsidianCardElevated)
                                    .clickable { onToggleConsent(partner.id, false) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Opt out ✕", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CreamWhite))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (anyOptedOut) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianCardElevated)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Someone opted out — completely fine! Consent comes first.",
                    style = MaterialTheme.typography.bodySmall.copy(color = LightGold, textAlign = TextAlign.Center)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            SeductiveSecondaryButton(
                text = "MAKE IT A SOLO DARE INSTEAD",
                onClick = onDowngradeToSolo
            )
            Spacer(modifier = Modifier.height(8.dp))
            SeductiveSecondaryButton(
                text = "DRAW A DIFFERENT CARD",
                onClick = onRedraw
            )
        } else {
            SeductivePrimaryButton(
                text = "EVERYONE'S IN — REVEAL DARE ➔",
                onClick = onConfirmed,
                enabled = allOptedIn,
                testTag = "reveal_card_btn"
            )
        }
    }
}

@Composable
private fun CardRevealContent(
    state: GameUiState,
    isBonusCard: Boolean,
    onStartTimer: () -> Unit,
    onAwardBonus: () -> Unit,
    onMarkUncomfortable: () -> Unit,
    onDrawReplacement: () -> Unit,
    onDowngradeToSolo: () -> Unit,
    onRedrawCard: () -> Unit,
    onSelectPartner: (String) -> Unit,
    onResolve: (String) -> Unit
) {
    val card = if (isBonusCard) state.bonusCard else state.currentCard ?: return
    if (card == null) return
    val active = state.activePlayer ?: return

    val levelMeta = LEVEL_INFO[card.level] ?: LEVEL_INFO[1]!!
    val levelColor = parseColor(levelMeta.colorHex)

    // Personalize prompt text
    val otherPlayers = state.players.filter { it.id != active.id }
    val partnerNames = state.players
        .filter { state.selectedPartnerIds.contains(it.id) }
        .map { it.name }
    val partnerText = if (partnerNames.isNotEmpty()) {
        partnerNames.joinToString(" & ")
    } else if (otherPlayers.size == 1) {
        otherPlayers.first().name
    } else {
        "your partner"
    }
    val personalizedText = card.text.replace("{p}", partnerText)
    val points = GameConstants.computePoints(card.level, state.cardScope)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isBonusCard) {
            Text(
                text = "🃏 DOUBLE DARE BONUS CARD",
                style = MaterialTheme.typography.labelMedium.copy(color = ChampagneGold, letterSpacing = 2.sp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Luxury Seductive Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            levelColor,
                            ObsidianBlack
                        )
                    )
                )
                .border(1.5.dp, ChampagneGold.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${card.category} • LEVEL ${card.level}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LightGold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "+$points PTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = LightGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${levelMeta.emoji} ${levelMeta.name}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        color = CreamWhite.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = personalizedText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        color = CreamWhite,
                        lineHeight = 32.sp
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCOPE: ${state.cardScope.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal, fontSize = 10.sp)
                    )

                    if (card.timerSeconds != null) {
                        Text(
                            text = "⏱ ${card.timerSeconds}s DURATION",
                            style = MaterialTheme.typography.labelSmall.copy(color = ChampagneGold, fontSize = 10.sp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "${active.name}'s Dare",
            style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, fontWeight = FontWeight.SemiBold)
        )

        // Partner selection chips if 3+ players and paired dare
        if (state.cardScope == "paired" && otherPlayers.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Partner: ",
                    style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal, fontSize = 11.sp)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(otherPlayers) { p ->
                        val isSelected = state.selectedPartnerIds.contains(p.id)
                        val pColor = parseColor(p.colorHex)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) pColor.copy(alpha = 0.35f) else ObsidianCard)
                                .border(
                                    1.dp,
                                    if (isSelected) pColor else ObsidianBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectPartner(p.id) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = p.name + if (isSelected) " ✓" else "",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CreamWhite else SoftCharcoal,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Timer controls if card has timer
        if (card.timerSeconds != null) {
            Spacer(modifier = Modifier.height(10.dp))
            if (state.isTimerRunning) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CrimsonRed)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "⏱ ${state.timerRemaining}s REMAINING",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CreamWhite
                        )
                    )
                }
            } else if (state.isTimerFinished) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ChampagneGold)
                        .border(1.5.dp, LightGold, RoundedCornerShape(20.dp))
                        .padding(horizontal = 22.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "⏰ TIME'S UP!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = ObsidianBlack,
                            letterSpacing = 1.2.sp
                        )
                    )
                }
            } else {
                SeductiveSecondaryButton(
                    text = "START ${card.timerSeconds}s TIMER ⏱",
                    onClick = onStartTimer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comfort Assistant or Outcome actions
        if (state.uncomfortableCardPending != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ObsidianCardElevated)
                    .border(1.5.dp, ChampagneGold.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🛡️ DARE MUTED & REMOVED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Recorded in learning memory. This dare will never be suggested to these players again. Choose how to continue:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CreamWhite,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SeductiveGoldButton(
                        text = "🔄 DRAW REPLACEMENT DARE",
                        onClick = onDrawReplacement,
                        testTag = "draw_replacement_btn"
                    )
                    if (state.cardScope != "solo") {
                        Spacer(modifier = Modifier.height(8.dp))
                        SeductiveSecondaryButton(
                            text = "👤 SWITCH TO SOLO DARE",
                            onClick = onDowngradeToSolo,
                            testTag = "downgrade_solo_btn"
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onResolve("notcomfortable") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("pass_turn_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftCharcoal),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "➔ PASS TURN (ZERO PENALTY)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SoftCharcoal
                            )
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SeductivePrimaryButton(
                        text = "✓ COMPLETED",
                        onClick = { onResolve("completed") },
                        testTag = "completed_dare_btn"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SeductiveGoldButton(
                        text = "✨ BONUS (+2)",
                        onClick = {
                            onAwardBonus()
                            onResolve("completed")
                        },
                        testTag = "bonus_dare_btn"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SeductiveSecondaryButton(
                        text = "🛑 UNCOMFORTABLE",
                        onClick = onMarkUncomfortable,
                        testTag = "uncomfortable_btn"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    SeductiveSecondaryButton(
                        text = "↻ REDRAW",
                        onClick = onRedrawCard,
                        testTag = "skip_dare_btn"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No one ever owes an explanation. Comfort and safety always come first.",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = SoftCharcoal.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun TurnSummaryContent(
    state: GameUiState,
    onContinue: () -> Unit,
    onEndGame: () -> Unit,
    onGiveHeart: () -> Unit = {}
) {
    val active = state.activePlayer ?: return
    val nextIndex = (state.currentPlayerIndex + 1) % state.players.size
    val nextPlayer = state.players.getOrNull(nextIndex)
    val hasReachedEnd = active.position >= GameConstants.BOARD_LENGTH

    val headline = when (state.lastResolution) {
        "completed" -> "Nice 😏"
        "notcomfortable" -> "Comfort first."
        "skipped" -> "Skipped. No worries."
        "swap" -> "Positions Swapped!"
        else -> "Round Complete."
    }

    val subtitle = when (state.lastResolution) {
        "completed" -> "${active.name} earned points for boldness!"
        "notcomfortable" -> "Consent always outranks score. Zero penalty."
        "skipped" -> "Onward to the next challenge."
        "swap" -> "Board positions have been updated."
        else -> "Keep the energy going."
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = LightGold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SoftCharcoal,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Heart reaction button for non-active players (Task 5)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(ObsidianCardElevated)
                .border(1.dp, ChampagneGold.copy(alpha = 0.6f), RoundedCornerShape(30.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onGiveHeart() }
                    )
                }
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🖤", fontSize = 22.sp)
                Text(
                    text = "Long press to heart this moment",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = SoftCharcoal
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (hasReachedEnd) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ObsidianCardElevated)
                    .border(1.dp, ChampagneGold, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆 ${active.name} reached the final space 40!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LightGold
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            SeductiveGoldButton(
                text = "SEE FINAL PODIUM & SCORES 🏆",
                onClick = onEndGame,
                testTag = "see_final_podium_btn"
            )
        } else {
            if (nextPlayer != null) {
                Text(
                    text = "Next Roller: ${nextPlayer.name}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SeductivePrimaryButton(
                text = "PASS PHONE & CONTINUE ➔",
                onClick = onContinue,
                testTag = "advance_turn_btn"
            )
        }
    }
}
