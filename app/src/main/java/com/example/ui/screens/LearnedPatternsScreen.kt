package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.DareCardEntity
import com.example.domain.CategoryAffinity
import com.example.domain.LEVEL_INFO
import com.example.domain.LearnedPatterns
import com.example.ui.components.SeductiveGoldButton
import com.example.ui.components.SeductivePrimaryButton
import com.example.ui.components.SeductiveSecondaryButton
import com.example.ui.components.parseColor
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
import com.example.ui.theme.SoftCharcoal

@Composable
fun LearnedPatternsScreen(
    patterns: LearnedPatterns,
    isDark: Boolean = true,
    onToggleTheme: (() -> Unit)? = null,
    onToggleAdaptiveEngine: (Boolean) -> Unit,
    onToggleBoostCustomDares: (Boolean) -> Unit,
    onUnblockCard: (Long) -> Unit,
    onResetLearnedPatterns: () -> Unit,
    onBack: () -> Unit
) {
    var showResetConfirmation by remember { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("patterns_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.gold)
                    }
                    Column(modifier = Modifier.padding(start = 6.dp)) {
                        Text(
                            text = "PREFERENCES & PATTERNS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.goldLight,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "Adaptive Learning & Comfort Profile",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AppTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (onToggleTheme != null) {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppTheme.colors.surfaceElevated)
                            .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                            .testTag("theme_toggle_patterns_btn")
                    ) {
                        Text(text = if (isDark) "☀️" else "🌙", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Playstyle Archetype & Vibe
                item {
                    ArchetypeCard(patterns = patterns)
                }

                // Section 2: Adaptive Settings (Toggle AI Learning & Custom Dares Boost)
                item {
                    AdaptiveEngineSettingsCard(
                        adaptiveEnabled = patterns.adaptiveEngineEnabled,
                        boostCustomEnabled = patterns.boostCustomDares,
                        onToggleAdaptive = onToggleAdaptiveEngine,
                        onToggleBoostCustom = onToggleBoostCustomDares
                    )
                }

                // Section 3: Intensity Sweet Spot
                item {
                    IntensitySweetSpotCard(
                        comfortPercents = patterns.intensityComfortPercents,
                        sweetSpot = patterns.intensitySweetSpot
                    )
                }

                // Section 4: All 11 Categories Affinities
                item {
                    CategoryAffinitiesCard(affinities = patterns.categoryAffinities)
                }

                // Section 5: Boundaries & Excluded Cards
                item {
                    BoundariesCard(
                        blockedCards = patterns.blockedCards,
                        onUnblock = onUnblockCard
                    )
                }

                // Section 6: Reset Actions
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!showResetConfirmation) {
                            SeductiveSecondaryButton(
                                text = "↺ RESET LEARNED PATTERNS & STATS",
                                onClick = { showResetConfirmation = true },
                                testTag = "reset_patterns_btn"
                            )
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SafetyRed.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Reset Play History & Learned Profile?",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = AppTheme.colors.textPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "This will unblock all previously skipped cards and reset category affinity calculations to a fresh slate.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = AppTheme.colors.textSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        SeductiveSecondaryButton(
                                            text = "CANCEL",
                                            onClick = { showResetConfirmation = false }
                                        )
                                        SeductiveGoldButton(
                                            text = "YES, RESET",
                                            onClick = {
                                                onResetLearnedPatterns()
                                                showResetConfirmation = false
                                            },
                                            testTag = "confirm_reset_patterns_btn"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchetypeCard(patterns: LearnedPatterns) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CrimsonRed.copy(alpha = 0.2f))
                            .border(1.dp, AppTheme.colors.gold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✨", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PLAYER VIBE ARCHETYPE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                color = AppTheme.colors.gold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = patterns.playstyleArchetype,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.goldLight
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = patterns.archetypeDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppTheme.colors.textSecondary,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "PLAYED", value = "${patterns.totalDaresPlayed}")
                StatItem(label = "COMPLETED", value = "${patterns.totalCompleted}", color = SafetyGreen)
                StatItem(label = "SKIPPED", value = "${patterns.totalSkipped}", color = SoftCharcoal)
                StatItem(label = "BOUNDARIES", value = "${patterns.blockedCardIds.size}", color = SafetyRed)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color = CreamWhite) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = AppTheme.colors.textSecondary,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
private fun AdaptiveEngineSettingsCard(
    adaptiveEnabled: Boolean,
    boostCustomEnabled: Boolean,
    onToggleAdaptive: (Boolean) -> Unit,
    onToggleBoostCustom: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "ADAPTIVE ENGINE & DECK BEHAVIOR",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    color = AppTheme.colors.gold,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle 1: Adaptive Learning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Smart Preference Learning",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    )
                    Text(
                        text = "Adapts card suggestions and wildcard picks to categories your group enjoys most.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = adaptiveEnabled,
                    onCheckedChange = onToggleAdaptive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppTheme.colors.goldLight,
                        checkedTrackColor = CrimsonRed
                    ),
                    modifier = Modifier.testTag("adaptive_engine_switch")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggle 2: Boost Custom Dares
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Boost User-Created Dares",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    )
                    Text(
                        text = "Prioritizes your custom-created dares so they appear prominently in future games.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = boostCustomEnabled,
                    onCheckedChange = onToggleBoostCustom,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppTheme.colors.goldLight,
                        checkedTrackColor = CrimsonRed
                    ),
                    modifier = Modifier.testTag("boost_custom_switch")
                )
            }
        }
    }
}

@Composable
private fun IntensitySweetSpotCard(
    comfortPercents: Map<Int, Int>,
    sweetSpot: Int
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INTENSITY COMFORT SWEET SPOT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        color = AppTheme.colors.gold,
                        fontWeight = FontWeight.Bold
                    )
                )
                val meta = LEVEL_INFO[sweetSpot] ?: LEVEL_INFO[2]!!
                Text(
                    text = "Sweet Spot: Level $sweetSpot ${meta.emoji}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = parseColor(meta.colorHex),
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..4).forEach { lvl ->
                    val meta = LEVEL_INFO[lvl] ?: LEVEL_INFO[1]!!
                    val comfort = comfortPercents[lvl] ?: 100
                    val isSweetSpot = lvl == sweetSpot

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSweetSpot) parseColor(meta.colorHex).copy(alpha = 0.2f) else AppTheme.colors.surface)
                            .border(
                                1.dp,
                                if (isSweetSpot) parseColor(meta.colorHex) else AppTheme.colors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = meta.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "L$lvl ${meta.name}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = parseColor(meta.colorHex)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$comfort%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.textPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryAffinitiesCard(affinities: List<CategoryAffinity>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "CATEGORY AFFINITIES (ALL 11 CATEGORIES)",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    color = AppTheme.colors.gold,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                affinities.forEach { aff ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = aff.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = aff.categoryLabel,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${aff.completedCount}/${aff.totalPresented} done",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AppTheme.colors.textSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${aff.affinityPercent}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (aff.affinityPercent >= 80) ChampagneGold else AppTheme.colors.textPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { aff.affinityPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (aff.affinityPercent >= 80) ChampagneGold else CrimsonRed,
                            trackColor = AppTheme.colors.surfaceElevated
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoundariesCard(
    blockedCards: List<DareCardEntity>,
    onUnblock: (Long) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = AppTheme.colors.gold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LEARNED BOUNDARIES (${blockedCards.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            color = AppTheme.colors.gold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (blockedCards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.colors.surface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cards marked uncomfortable. All 250+ cards are actively eligible!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppTheme.colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                Text(
                    text = "These dares were marked uncomfortable and will never be drawn in future sessions. Tap Unblock to allow them back into rotation.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AppTheme.colors.textSecondary,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    blockedCards.forEach { card ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppTheme.colors.surface)
                                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${card.category} • L${card.level}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = AppTheme.colors.gold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = card.text,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AppTheme.colors.textPrimary,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CrimsonRed.copy(alpha = 0.2f))
                                    .border(1.dp, CrimsonRed, RoundedCornerShape(8.dp))
                                    .clickable { onUnblock(card.id) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "UNBLOCK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LightGold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
