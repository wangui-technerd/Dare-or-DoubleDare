package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DareLogEntity
import com.example.domain.LEVEL_INFO
import com.example.domain.SafetyLevel
import com.example.ui.theme.AppTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.LightGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyYellow
import com.example.ui.theme.SoftCharcoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyModalSheet(
    currentLevel: SafetyLevel,
    safeWord: String,
    learnedUncomfortableCount: Int = 0,
    onSelectLevel: (SafetyLevel) -> Unit,
    onResetUncomfortableCards: (() -> Unit)? = null,
    onEmergencySafeWord: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.cardBackground,
        contentColor = AppTheme.colors.textPrimary,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SAFETY & COMFORT",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Traffic-Light Check",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Consent always outranks score. Anyone can adjust intensity or stop play immediately.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SoftCharcoal,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Safe Word Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ObsidianCardElevated)
                    .border(1.5.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ROOM SAFE WORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            color = SoftCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = safeWord.uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = LightGold,
                            letterSpacing = 2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Saying this immediately halts the current dare with no questions asked.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = SoftCharcoal,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3 Traffic lights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SafetyLevel.values().forEach { level ->
                    val isSelected = level == currentLevel
                    val color = parseColor(level.colorHex)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) color else ObsidianBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectLevel(level) }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = if (isSelected) 0.9f else 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = level.icon, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CreamWhite else SoftCharcoal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentLevel.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = parseColor(currentLevel.colorHex),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            )

            if (learnedUncomfortableCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCardElevated)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🛡️ LEARNING MEMORY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "$learnedUncomfortableCount dare(s) permanently muted based on comfort feedback.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = SoftCharcoal
                                )
                            )
                        }
                        if (onResetUncomfortableCards != null) {
                            OutlinedButton(
                                onClick = onResetUncomfortableCards,
                                modifier = Modifier.padding(start = 8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = LightGold),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Reset ↺", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (onEmergencySafeWord != null) {
                OutlinedButton(
                    onClick = {
                        onEmergencySafeWord()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("emergency_safeword_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🚨 EMERGENCY SAFE WORD (HALT PLAY)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            SeductivePrimaryButton(
                text = "RESUME GAME",
                onClick = onDismiss,
                testTag = "safety_resume_btn"
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreHistoryModalSheet(
    recentLogs: List<DareLogEntity>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.cardBackground,
        contentColor = AppTheme.colors.textPrimary,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DARE & SCORE LOG",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = LightGold
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftCharcoal)
                }
            }

            if (recentLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No dares played yet in this session.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SoftCharcoal)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentLogs) { log ->
                        val outcomeColor = when (log.outcome) {
                            "COMPLETED" -> SafetyGreen
                            "BONUS" -> ChampagneGold
                            "SKIPPED" -> SoftCharcoal
                            "NOT_COMFORTABLE" -> SafetyYellow
                            else -> CrimsonRed
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ObsidianCardElevated)
                                .border(0.8.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = log.playerName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = CreamWhite
                                            )
                                        )
                                        Text(
                                            text = "• ${log.category} (L${log.level})",
                                            style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = log.cardText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SoftCharcoal,
                                            fontSize = 12.sp
                                        ),
                                        maxLines = 2
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (log.points > 0) "+${log.points} pts" else "0 pts",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = outcomeColor
                                        )
                                    )
                                    Text(
                                        text = log.outcome,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            color = outcomeColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            SeductiveSecondaryButton(
                text = "BACK TO GAME",
                onClick = onDismiss
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PauseModalSheet(
    onResume: () -> Unit,
    onSafetyCheck: () -> Unit,
    onViewScores: () -> Unit,
    onViewPatterns: (() -> Unit)? = null,
    onEndGame: () -> Unit,
    onExitToMenu: () -> Unit,
    isDark: Boolean = true,
    onToggleTheme: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.cardBackground,
        contentColor = AppTheme.colors.textPrimary,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PAUSED",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    color = AppTheme.colors.gold,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Take Your Time",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "The board isn't going anywhere. Step away, refresh drinks, or check in.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            SeductivePrimaryButton(
                text = "RESUME GAME",
                onClick = onResume,
                testTag = "pause_resume_btn"
            )
            Spacer(modifier = Modifier.height(10.dp))
            SeductiveSecondaryButton(
                text = "SAFETY CHECK (TRAFFIC LIGHT)",
                onClick = onSafetyCheck,
                testTag = "pause_safety_btn"
            )
            Spacer(modifier = Modifier.height(10.dp))
            SeductiveSecondaryButton(
                text = "VIEW SCORES & DARE LOG",
                onClick = onViewScores,
                testTag = "pause_scores_btn"
            )
            if (onViewPatterns != null) {
                Spacer(modifier = Modifier.height(10.dp))
                SeductiveSecondaryButton(
                    text = "PREFERENCES & LEARNED PATTERNS 🧠",
                    onClick = onViewPatterns,
                    testTag = "pause_patterns_btn"
                )
            }
            if (onToggleTheme != null) {
                Spacer(modifier = Modifier.height(10.dp))
                SeductiveSecondaryButton(
                    text = if (isDark) "SWITCH TO LIGHT MODE ☀️" else "SWITCH TO DARK MODE 🌙",
                    onClick = onToggleTheme,
                    testTag = "pause_toggle_theme_btn"
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SeductiveSecondaryButton(
                text = "FINISH & SEE FINAL PODIUM",
                onClick = onEndGame,
                testTag = "pause_end_game_btn"
            )
            Spacer(modifier = Modifier.height(10.dp))
            SeductiveSecondaryButton(
                text = "EXIT TO MAIN MENU",
                onClick = onExitToMenu,
                testTag = "pause_exit_menu_btn"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesModalSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AppTheme.colors.cardBackground,
        contentColor = AppTheme.colors.textPrimary,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HOW IT WORKS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = LightGold
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftCharcoal)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Two dice. One board. Everyone plays.",
                        style = MaterialTheme.typography.titleSmall.copy(color = CreamWhite, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Roll two dice each turn: Die 1 picks your Category (Power, Sensation, Roleplay, Praise, Wildcard, or Wild Pick). Die 2 picks your Intensity Level (1 to 4), capped by your current board zone. Their sum advances your token across 40 spaces!",
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, lineHeight = 18.sp)
                    )
                }

                item {
                    Text(
                        text = "The 4 Intensity Zones",
                        style = MaterialTheme.typography.titleSmall.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LEVEL_INFO.values.forEach { lvl ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(lvl.emoji, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "${lvl.name} (Zone ${lvl.level})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = parseColor(lvl.colorHex)
                                    )
                                )
                                Text(
                                    text = lvl.tagline,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = SoftCharcoal)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Special Spaces",
                        style = MaterialTheme.typography.titleSmall.copy(color = ChampagneGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Space 8 & 32 (Double Dare 🃏🃏): Draw two dares! Complete both for double points.\n• Space 16 (Swap 🔀): Trade board positions with any player.\n• Space 24 (Confess 🤫): Forced spicy wildcard confession.\n• Space 40 (Jackpot 🏆): Final showdown dare to close the game!",
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, lineHeight = 18.sp)
                    )
                }

                item {
                    Text(
                        text = "Consent & Opt-In Rules",
                        style = MaterialTheme.typography.titleSmall.copy(color = LightGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Whenever a paired or group dare is drawn, every named partner gets to explicitly opt in or out before the dare counts. Anyone can skip or opt out without explanation or penalty. Points reward boldness, but comfort and consent always outrank score.",
                        style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal, lineHeight = 18.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            SeductivePrimaryButton(
                text = "GOT IT",
                onClick = onDismiss
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
