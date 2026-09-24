package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.GameConstants
import com.example.domain.LEVEL_INFO
import com.example.domain.Player
import com.example.domain.SafetyLevel
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.AppTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.LightGold
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyYellow
import com.example.ui.theme.SoftCharcoal
import com.example.ui.theme.VelvetWine

@Composable
fun TopBarHeader(
    title: String = "DARE OR DOUBLE DARE",
    onScoreClick: (() -> Unit)? = null,
    onPauseClick: (() -> Unit)? = null,
    onRulesClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    isDark: Boolean = true,
    onToggleTheme: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    color = AppTheme.colors.goldLight
                )
            )
            Text(
                text = "INTIMATE PARTY BOARD",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    color = AppTheme.colors.textSecondary
                )
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (onToggleTheme != null) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.surfaceElevated)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                        .testTag("theme_toggle_header_btn")
                ) {
                    Text(
                        text = if (isDark) "☀️" else "🌙",
                        fontSize = 16.sp
                    )
                }
            }
            if (onRulesClick != null) {
                IconButton(
                    onClick = onRulesClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.surfaceElevated)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                        .testTag("rules_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Rules",
                        tint = AppTheme.colors.gold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (onScoreClick != null) {
                IconButton(
                    onClick = onScoreClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.surfaceElevated)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                        .testTag("scores_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Score History",
                        tint = AppTheme.colors.gold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (onPauseClick != null) {
                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.surfaceElevated)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                        .testTag("pause_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = AppTheme.colors.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (onProfileClick != null) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.surfaceElevated)
                        .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                        .testTag("profile_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Profile",
                        tint = AppTheme.colors.gold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreStrip(
    players: List<Player>,
    currentPlayerIndex: Int,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        players.forEachIndexed { index, player ->
            val isActive = index == currentPlayerIndex
            val playerColor = parseColor(player.colorHex)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isActive) AppTheme.colors.surfaceElevated else AppTheme.colors.surface)
                    .border(
                        width = if (isActive) 1.5.dp else 1.dp,
                        color = if (isActive) AppTheme.colors.gold else AppTheme.colors.border,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(playerColor)
                    )
                    Column {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isActive) AppTheme.colors.textPrimary else AppTheme.colors.textSecondary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${player.points} pts",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) AppTheme.colors.goldLight else AppTheme.colors.gold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoardTrackView(
    players: List<Player>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Auto-scroll to furthest token
    val leadPosition = players.maxOfOrNull { it.position } ?: 0
    LaunchedEffect(leadPosition) {
        val targetPixel = (leadPosition * 26).coerceAtLeast(0)
        scrollState.animateScrollTo(targetPixel)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Zone labels header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🌸 TEASE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = AppTheme.colors.textSecondary))
            Text("🍷 TEMPT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = AppTheme.colors.textSecondary))
            Text("🔥 HEAT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = AppTheme.colors.textSecondary))
            Text("🖤 AFTER DARK", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = AppTheme.colors.textSecondary))
        }

        // Horizontal board spaces
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.colors.surfaceElevated)
                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(12.dp))
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            for (space in 1..GameConstants.BOARD_LENGTH) {
                val zone = GameConstants.zoneOf(space)
                val special = GameConstants.SPECIAL_SPACES[space]
                val tokensHere = players.filter { it.position == space }

                val zoneColor = when (zone) {
                    1 -> Color(0xFF381F28)
                    2 -> Color(0xFF3B1525)
                    3 -> Color(0xFF451020)
                    else -> Color(0xFF221326)
                }

                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 38.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(zoneColor)
                        .border(
                            width = if (special != null) 1.5.dp else 0.5.dp,
                            color = if (special != null) ChampagneGold else ObsidianBorder,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Special space marker icon or space number
                    if (special != null && tokensHere.isEmpty()) {
                        val iconText = when (special) {
                            "DOUBLE_DARE" -> "🃏"
                            "SWAP" -> "🔀"
                            "CONFESS" -> "🤫"
                            "JACKPOT" -> "🏆"
                            else -> "★"
                        }
                        Text(
                            text = iconText,
                            fontSize = 9.sp,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 2.dp)
                        )
                    }

                    // Player tokens on space
                    if (tokensHere.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = 3.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            tokensHere.forEach { p ->
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .padding(0.5.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(p.colorHex))
                                )
                            }
                        }
                    } else if (space % 5 == 0) {
                        Text(
                            text = "$space",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                color = SoftCharcoal.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeductivePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CrimsonRed,
            contentColor = CreamWhite,
            disabledContainerColor = CrimsonRed.copy(alpha = 0.35f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun SeductiveGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "gold_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ChampagneGold,
            contentColor = ObsidianBlack,
            disabledContainerColor = ChampagneGold.copy(alpha = 0.35f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ObsidianBlack
            )
        )
    }
}

@Composable
fun SeductiveSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "secondary_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, AppTheme.colors.border),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AppTheme.colors.surfaceElevated.copy(alpha = 0.6f),
            contentColor = AppTheme.colors.textPrimary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun SafetyFloatingPill(
    safetyLevel: SafetyLevel,
    safeWord: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val indicatorColor = when (safetyLevel) {
        SafetyLevel.GREEN -> SafetyGreen
        SafetyLevel.YELLOW -> SafetyYellow
        SafetyLevel.RED -> SafetyRed
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(AppTheme.colors.surfaceElevated)
            .border(1.5.dp, indicatorColor, RoundedCornerShape(30.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("safety_fab")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = safetyLevel.icon, fontSize = 16.sp)
            Column {
                Text(
                    text = "SAFE: $safeWord",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.goldLight,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = safetyLevel.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = indicatorColor,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        CrimsonRed
    }
}
