package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.DareCardEntity
import com.example.domain.CategoryMeta
import com.example.domain.GameConstants
import com.example.domain.LEVEL_INFO
import com.example.ui.components.SeductiveGoldButton
import com.example.ui.components.SeductivePrimaryButton
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

enum class DaresTab {
    DECK,
    CUSTOM,
    FAVORITES,
    CATEGORIES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDaresScreen(
    allCards: List<DareCardEntity>,
    isDark: Boolean = true,
    onToggleTheme: (() -> Unit)? = null,
    onAddCustomCard: (category: String, level: Int, scope: String, text: String, timerSeconds: Int?) -> Unit,
    onToggleFavorite: (DareCardEntity) -> Unit,
    onDeleteCard: (DareCardEntity) -> Unit,
    onBack: () -> Unit
) {
    var currentTab by remember { mutableStateOf(DaresTab.DECK) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var selectedLevelFilter by remember { mutableIntStateOf(0) } // 0 = all
    var showAddDialog by remember { mutableStateOf(false) }

    val customCards = allCards.filter { it.isCustom }
    val favoriteCards = allCards.filter { it.isFavorite }

    val activeDisplayCards = when (currentTab) {
        DaresTab.DECK -> allCards.filter { card ->
            (selectedCategoryFilter == "ALL" || card.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
                    (selectedLevelFilter == 0 || card.level == selectedLevelFilter)
        }
        DaresTab.CUSTOM -> customCards.filter { card ->
            (selectedCategoryFilter == "ALL" || card.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
                    (selectedLevelFilter == 0 || card.level == selectedLevelFilter)
        }
        DaresTab.FAVORITES -> favoriteCards.filter { card ->
            (selectedCategoryFilter == "ALL" || card.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
                    (selectedLevelFilter == 0 || card.level == selectedLevelFilter)
        }
        DaresTab.CATEGORIES -> emptyList()
    }

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
                    IconButton(onClick = onBack, modifier = Modifier.testTag("custom_dares_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.gold)
                    }
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = "DECK & CATEGORIES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.goldLight,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Text(
                            text = "${allCards.size} Dares • ${customCards.size} Custom • 11 Categories",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AppTheme.colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onToggleTheme != null) {
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppTheme.colors.surfaceElevated)
                                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(10.dp))
                                .testTag("theme_toggle_dares_btn")
                        ) {
                            Text(text = if (isDark) "☀️" else "🌙", fontSize = 15.sp)
                        }
                    }

                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonRed)
                            .testTag("add_custom_card_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Card", tint = CreamWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Tab Navigation: All Deck | My Custom | Favorites | 11 Categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppTheme.colors.surface)
                    .border(1.dp, AppTheme.colors.border, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabButton(
                    title = "All (${allCards.size})",
                    isSelected = currentTab == DaresTab.DECK,
                    onClick = { currentTab = DaresTab.DECK },
                    modifier = Modifier.weight(1f),
                    testTag = "tab_deck"
                )
                TabButton(
                    title = "Custom (${customCards.size})",
                    isSelected = currentTab == DaresTab.CUSTOM,
                    onClick = { currentTab = DaresTab.CUSTOM },
                    modifier = Modifier.weight(1f),
                    testTag = "tab_custom"
                )
                TabButton(
                    title = "Favs (${favoriteCards.size})",
                    isSelected = currentTab == DaresTab.FAVORITES,
                    onClick = { currentTab = DaresTab.FAVORITES },
                    modifier = Modifier.weight(1f),
                    testTag = "tab_favs"
                )
                TabButton(
                    title = "11 Categories",
                    isSelected = currentTab == DaresTab.CATEGORIES,
                    onClick = { currentTab = DaresTab.CATEGORIES },
                    modifier = Modifier.weight(1f),
                    testTag = "tab_categories"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (currentTab == DaresTab.CATEGORIES) {
                // Show the complete guide to all 11 categories
                CategoriesGuideView(
                    allCards = allCards,
                    onSelectCategory = { catKey ->
                        selectedCategoryFilter = catKey
                        selectedLevelFilter = 0
                        currentTab = DaresTab.DECK
                    }
                )
            } else {
                // Filter chips & card list
                Column(modifier = Modifier.fillMaxSize()) {
                    if (currentTab == DaresTab.CUSTOM) {
                        // Room persistence reassurance banner
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceElevated),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ROOM DATABASE PERSISTENCE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = ChampagneGold,
                                            letterSpacing = 1.sp,
                                            fontSize = 10.sp
                                        )
                                    )
                                    Text(
                                        text = "Custom dares are saved locally on this device and automatically prioritized in future game sessions.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = AppTheme.colors.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val cats = listOf("ALL") + GameConstants.CATEGORIES.map { it.key }
                        cats.forEach { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            val catMeta = GameConstants.CATEGORIES.find { it.key.equals(cat, ignoreCase = true) }
                            val label = if (cat == "ALL") "All Categories" else "${catMeta?.emoji ?: "🃏"} $cat"

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) CrimsonRed else AppTheme.colors.surface)
                                    .border(
                                        1.dp,
                                        if (isSelected) ChampagneGold else AppTheme.colors.border,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedCategoryFilter = cat }
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) CreamWhite else AppTheme.colors.textSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Level Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val levels = listOf(0 to "All Levels", 1 to "🌸 L1", 2 to "🍷 L2", 3 to "🔥 L3", 4 to "🖤 L4")
                        levels.forEach { (lvl, label) ->
                            val isSelected = selectedLevelFilter == lvl
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) ObsidianCardElevated else AppTheme.colors.surface)
                                    .border(
                                        1.dp,
                                        if (isSelected) ChampagneGold else AppTheme.colors.border,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedLevelFilter = lvl }
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) LightGold else AppTheme.colors.textSecondary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cards List or Empty State
                    if (activeDisplayCards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppTheme.colors.surface)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (currentTab == DaresTab.CUSTOM) "✨ No Custom Dares Yet" else "No cards match filter",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (currentTab == DaresTab.CUSTOM)
                                        "Create your own spicy or intimate dares! They will be saved to Room persistence on this device."
                                    else
                                        "Try selecting 'ALL' in category or level filters above.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AppTheme.colors.textSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                )
                                if (currentTab == DaresTab.CUSTOM) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    SeductiveGoldButton(
                                        text = "+ CREATE FIRST CUSTOM DARE",
                                        onClick = { showAddDialog = true }
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(activeDisplayCards, key = { it.id }) { card ->
                                DareCardItem(
                                    card = card,
                                    onToggleFavorite = { onToggleFavorite(card) },
                                    onDeleteCard = { onDeleteCard(card) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Card Bottom Sheet
        if (showAddDialog) {
            AddCardSheet(
                onDismiss = { showAddDialog = false },
                onAdd = { cat, lvl, scp, txt, tmr ->
                    onAddCustomCard(cat, lvl, scp, txt, tmr)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CrimsonRed else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isSelected) CreamWhite else AppTheme.colors.textSecondary
            )
        )
    }
}

@Composable
private fun DareCardItem(
    card: DareCardEntity,
    onToggleFavorite: () -> Unit,
    onDeleteCard: () -> Unit
) {
    val lvlMeta = LEVEL_INFO[card.level] ?: LEVEL_INFO[1]!!
    val lvlColor = parseColor(lvlMeta.colorHex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.colors.cardBackground)
            .border(0.8.dp, AppTheme.colors.border, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(lvlColor.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "L${card.level} ${lvlMeta.name}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = lvlColor
                            )
                        )
                    }

                    if (card.isCustom) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChampagneGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "✨ CUSTOM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                            )
                        }
                    }

                    Text(
                        text = "• ${card.category} • ${card.scope}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppTheme.colors.textSecondary,
                            fontSize = 11.sp
                        )
                    )

                    if (card.timerSeconds != null) {
                        Text(
                            text = "⏱ ${card.timerSeconds}s",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ChampagneGold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            if (card.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (card.isFavorite) ChampagneGold else SoftCharcoal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (card.isCustom) {
                        IconButton(
                            onClick = onDeleteCard,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = SafetyRed.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppTheme.colors.textPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun CategoriesGuideView(
    allCards: List<DareCardEntity>,
    onSelectCategory: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surfaceElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "THE 11 QUESTION & DARE CATEGORIES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.gold,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Here is the comprehensive catalog of all 11 curated categories. Each roll of the 12-sided category die corresponds to these distinct intimate, playful, and provocative themes.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        items(GameConstants.CATEGORIES) { cat ->
            val matchingCards = allCards.filter { it.category.equals(cat.key, ignoreCase = true) }
            val count = matchingCards.size
            val sampleCard = matchingCards.firstOrNull()

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
                            Text(text = cat.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = cat.label,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.goldLight
                                    )
                                )
                                Text(
                                    text = "KEY: ${cat.key} • DIE # ${(GameConstants.CATEGORIES.indexOf(cat) + 1)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = AppTheme.colors.gold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppTheme.colors.surfaceElevated)
                                .border(1.dp, AppTheme.colors.border, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$count Dares",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = cat.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    if (sampleCard != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppTheme.colors.surface)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "SAMPLE QUESTION / DARE:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.gold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${sampleCard.text}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AppTheme.colors.textPrimary,
                                        fontSize = 11.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CrimsonRed.copy(alpha = 0.2f))
                                .border(1.dp, CrimsonRed, RoundedCornerShape(10.dp))
                                .clickable { onSelectCategory(cat.key) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "BROWSE ${cat.label.uppercase()} ➔",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LightGold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCardSheet(
    onDismiss: () -> Unit,
    onAdd: (category: String, level: Int, scope: String, text: String, timer: Int?) -> Unit
) {
    var category by remember { mutableStateOf("SENSATION") }
    var level by remember { mutableIntStateOf(2) }
    var scope by remember { mutableStateOf("paired") }
    var promptText by remember { mutableStateOf("") }
    var timerText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ObsidianCard,
        contentColor = CreamWhite,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CREATE CUSTOM DARE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = LightGold
                        )
                    )
                    Text(
                        text = "Saved permanently to Room local database",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ChampagneGold,
                            fontSize = 11.sp
                        )
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SoftCharcoal)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category selector (All 11 Categories)
            Text("Category:", style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GameConstants.CATEGORIES.forEach { catMeta ->
                    val isSel = category.equals(catMeta.key, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) CrimsonRed else AppTheme.colors.surfaceElevated)
                            .border(1.dp, if (isSel) ChampagneGold else AppTheme.colors.border, RoundedCornerShape(16.dp))
                            .clickable { category = catMeta.key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "${catMeta.emoji} ${catMeta.label}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSel) CreamWhite else AppTheme.colors.textPrimary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Level selector (1 to 4)
            Text("Intensity Zone:", style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal))
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (lvl in 1..4) {
                    val meta = LEVEL_INFO[lvl]!!
                    val isSel = level == lvl
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) parseColor(meta.colorHex) else ObsidianCardElevated)
                            .border(1.dp, if (isSel) ChampagneGold else ObsidianBorder, RoundedCornerShape(12.dp))
                            .clickable { level = lvl }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${meta.emoji} L$lvl",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CreamWhite
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scope selector (Solo, Paired, Group)
            Text("Scope:", style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal))
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("solo", "paired", "group").forEach { scp ->
                    val isSel = scope == scp
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) ChampagneGold else ObsidianCardElevated)
                            .clickable { scope = scp }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            scp.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) ObsidianBlack else CreamWhite
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prompt text
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                label = { Text("Dare Prompt (Use {p} for partner's name)") },
                placeholder = { Text("e.g. Whisper three genuine desires to {p}...") },
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonRed,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite,
                    focusedContainerColor = ObsidianCardElevated,
                    unfocusedContainerColor = ObsidianCardElevated
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_dare_prompt_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Optional Timer
            OutlinedTextField(
                value = timerText,
                onValueChange = { timerText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Optional Duration in seconds (e.g. 30, 60)") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonRed,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite,
                    focusedContainerColor = ObsidianCardElevated,
                    unfocusedContainerColor = ObsidianCardElevated
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeductivePrimaryButton(
                text = "SAVE DARE TO LOCAL ROOM DB",
                onClick = {
                    val timerInt = timerText.toIntOrNull()
                    onAdd(category, level, scope, promptText.trim(), timerInt)
                },
                enabled = promptText.trim().isNotBlank(),
                testTag = "save_custom_dare_btn"
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
