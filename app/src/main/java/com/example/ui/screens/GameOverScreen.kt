package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.dao.TopMomentDetail
import com.example.domain.Player
import com.example.ui.components.SeductiveGoldButton
import com.example.ui.components.SeductivePrimaryButton
import com.example.ui.components.SeductiveSecondaryButton
import com.example.ui.components.parseColor
import com.example.ui.theme.AppTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepBurgundy
import com.example.ui.theme.LightGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftCharcoal

@Composable
fun GameOverScreen(
    players: List<Player>,
    topMoments: List<TopMomentDetail> = emptyList(),
    onPlayAgain: () -> Unit,
    onNewGame: () -> Unit,
    onViewPastGames: () -> Unit
) {
    val sorted = players.sortedByDescending { it.points }
    val winner = sorted.firstOrNull()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepBurgundy.copy(alpha = 0.35f),
                        AppTheme.colors.background,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GAME OVER",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.5.sp,
                    color = AppTheme.colors.gold,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "👑 ${winner?.name ?: "Champion"} Wins",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.goldLight,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = "With ${winner?.points ?: 0} points earned through audacity and charm",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Top Moments (Hearts Feature)
            if (topMoments.isNotEmpty()) {
                Text(
                    text = "🖤 TOP MOMENTS OF THE GAME",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 1.8.sp,
                        color = AppTheme.colors.goldLight,
                        fontWeight = FontWeight.ExtraBold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    topMoments.forEachIndexed { index, moment ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppTheme.colors.surfaceElevated)
                                .border(1.dp, AppTheme.colors.gold, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "#${index + 1} • ${moment.playerName}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.gold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = moment.cardText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = AppTheme.colors.textPrimary
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DeepBurgundy)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "🖤 ${moment.heartCount}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CreamWhite
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Leaderboard / Podium
            Text(
                text = "🏆 FINAL LEADERBOARD",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 1.8.sp,
                    color = AppTheme.colors.gold,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sorted.forEachIndexed { index, player ->
                    val isFirst = index == 0
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isFirst) AppTheme.colors.surfaceElevated else AppTheme.colors.surface)
                            .border(
                                width = if (isFirst) 1.5.dp else 1.dp,
                                color = if (isFirst) AppTheme.colors.gold else AppTheme.colors.border,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isFirst) AppTheme.colors.goldLight else AppTheme.colors.textSecondary
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(player.colorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = player.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CreamWhite
                                        )
                                    )
                                }
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                )
                            }

                            Text(
                                text = "${player.points} pts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.goldLight
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeductiveGoldButton(
                    text = "PLAY AGAIN WITH SAME PLAYERS",
                    onClick = onPlayAgain,
                    testTag = "play_again_btn"
                )

                SeductivePrimaryButton(
                    text = "NEW GAME / HOME",
                    onClick = onNewGame,
                    testTag = "new_game_home_btn"
                )

                SeductiveSecondaryButton(
                    text = "VIEW PAST SESSIONS",
                    onClick = onViewPastGames,
                    testTag = "view_past_sessions_btn"
                )
            }
        }
    }
}
