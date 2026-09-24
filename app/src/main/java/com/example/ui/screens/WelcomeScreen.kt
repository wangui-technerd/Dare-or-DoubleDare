package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.SeductiveGoldButton
import com.example.ui.components.SeductivePrimaryButton
import com.example.ui.components.SeductiveSecondaryButton
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
import com.example.ui.theme.SoftCharcoal
import com.example.ui.theme.VelvetWine

@Composable
fun WelcomeScreen(
    currentUser: UserEntity?,
    isDark: Boolean = true,
    onToggleTheme: (() -> Unit)? = null,
    onStartGame: () -> Unit,
    onOpenAuth: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenCustomDares: () -> Unit,
    onOpenPastGames: () -> Unit,
    onOpenLearnedPatterns: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.background,
                        if (isDark) DeepBurgundy.copy(alpha = 0.35f) else Color(0xFFFFE6EB),
                        AppTheme.colors.background
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // User Authentication Bar & Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppTheme.colors.surface)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp))
                        .clickable { onOpenAuth() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("user_profile_bar")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentUser != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(currentUser.avatarColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(currentUser.avatarEmoji, fontSize = 18.sp)
                                }
                                Column {
                                    Text(
                                        text = currentUser.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = "Safe: ${currentUser.safeWord} • ${currentUser.totalPoints} pts",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AppTheme.colors.gold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Sign in",
                                    tint = AppTheme.colors.gold
                                )
                                Column {
                                    Text(
                                        text = "Host Profile & Vault",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = "Sign in / Set Profile",
                                        style = MaterialTheme.typography.labelSmall.copy(color = AppTheme.colors.textSecondary)
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Profile",
                            tint = AppTheme.colors.textSecondary
                        )
                    }
                }

                if (onToggleTheme != null) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppTheme.colors.surface)
                            .border(1.dp, AppTheme.colors.border, RoundedCornerShape(14.dp))
                            .clickable { onToggleTheme() }
                            .testTag("theme_toggle_welcome_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDark) "☀️" else "🌙",
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title & Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "FOR CONSENTING ADULTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 2.5.sp,
                        color = AppTheme.colors.gold,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "DARE OR\nDOUBLE DARE",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.goldLight,
                        letterSpacing = 2.sp,
                        lineHeight = 44.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ROLL • REVEAL • GO FURTHER",
                    style = MaterialTheme.typography.bodySmall.copy(
                        letterSpacing = 2.sp,
                        color = AppTheme.colors.textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Zone preview pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ZoneChip("🌸 TEASE", Color(0xFFE598A8))
                    ZoneChip("🍷 TEMPT", Color(0xFF8B2247))
                    ZoneChip("🔥 HEAT", Color(0xFFC92A45))
                    ZoneChip("🖤 DARK", Color(0xFF221326))
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Navigation Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeductivePrimaryButton(
                    text = "START NEW GAME",
                    onClick = onStartGame,
                    testTag = "start_game_btn"
                )

                SeductiveSecondaryButton(
                    text = "HOW IT WORKS & RULES",
                    onClick = onOpenRules,
                    testTag = "rules_btn"
                )

                SeductiveSecondaryButton(
                    text = "DARE DECK & CATEGORIES (250 CARDS)",
                    onClick = onOpenCustomDares,
                    testTag = "custom_dares_btn"
                )

                SeductiveSecondaryButton(
                    text = "PREFERENCES & PATTERNS (ADAPTIVE LEARNING)",
                    onClick = onOpenLearnedPatterns,
                    testTag = "learned_patterns_btn"
                )

                SeductiveSecondaryButton(
                    text = "PAST GAMES & RECORDS",
                    onClick = onOpenPastGames,
                    testTag = "past_games_btn"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Stored privately on your device. Zero external data sharing.",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = SoftCharcoal.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun ZoneChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.25f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CreamWhite
            )
        )
    }
}
