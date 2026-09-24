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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.domain.Player
import com.example.ui.components.SeductivePrimaryButton
import com.example.ui.components.SeductiveSecondaryButton
import com.example.ui.components.parseColor
import com.example.ui.theme.AppTheme
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.LightGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.SoftCharcoal

@Composable
fun PlayerSetupScreen(
    currentUser: UserEntity?,
    allSavedUsers: List<UserEntity>,
    onStartGame: (players: List<Player>, safeWord: String, sharePoints: Boolean) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    val defaultPalette = listOf(
        "#C92A45", "#E5C365", "#8B2247", "#5C7C8C",
        "#8A5A9E", "#6FA98A", "#E598A8", "#D97746"
    )

    val players = remember {
        mutableStateListOf<Player>().apply {
            if (currentUser != null) {
                add(
                    Player(
                        id = "user_${currentUser.id}",
                        name = currentUser.displayName,
                        colorHex = currentUser.avatarColorHex,
                        userId = currentUser.id
                    )
                )
            } else {
                add(
                    Player(
                        id = "p1",
                        name = "Player 1",
                        colorHex = defaultPalette[0]
                    )
                )
            }
        }
    }

    var newPlayerName by remember { mutableStateOf("") }
    var roomSafeWord by remember {
        mutableStateOf(currentUser?.safeWord ?: "Pineapple")
    }
    var sharePoints by remember { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("player_setup_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.gold)
                }
                Text(
                    text = "WHO'S PLAYING?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.goldLight,
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Setup Intimate Circle",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colors.textPrimary
                )
            )
            Text(
                text = "Add 2 to 8 players. Pass the phone around as turns progress.",
                style = MaterialTheme.typography.bodySmall.copy(color = AppTheme.colors.textSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Current Players List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                players.forEachIndexed { index, player ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(AppTheme.colors.surface)
                            .border(1.dp, AppTheme.colors.border, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(player.colorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = player.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CreamWhite
                                        )
                                    )
                                }
                                Column {
                                    Text(
                                        text = player.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = if (index == 0) "Host / Roller 1" else "Player ${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = AppTheme.colors.textSecondary)
                                    )
                                }
                            }

                            if (players.size > 1) {
                                IconButton(
                                    onClick = { players.removeAt(index) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = AppTheme.colors.textSecondary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add player section
            if (players.size < 8) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        placeholder = { Text("Player name (e.g. Liam)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonRed,
                            unfocusedBorderColor = AppTheme.colors.border,
                            focusedTextColor = AppTheme.colors.textPrimary,
                            unfocusedTextColor = AppTheme.colors.textPrimary,
                            focusedContainerColor = AppTheme.colors.surface,
                            unfocusedContainerColor = AppTheme.colors.surface
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_player_input")
                    )

                    IconButton(
                        onClick = {
                            val trimmed = newPlayerName.trim()
                            if (trimmed.isNotBlank() && players.size < 8) {
                                val color = defaultPalette[players.size % defaultPalette.size]
                                players.add(
                                    Player(
                                        id = "p_${System.currentTimeMillis()}",
                                        name = trimmed,
                                        colorHex = color
                                    )
                                )
                                newPlayerName = ""
                            }
                        },
                        enabled = newPlayerName.isNotBlank(),
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (newPlayerName.isNotBlank()) CrimsonRed else AppTheme.colors.surfaceElevated)
                            .testTag("add_player_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Player", tint = CreamWhite)
                    }
                }

                // Quick add other saved profiles
                val otherSaved = allSavedUsers.filter { saved ->
                    players.none { it.userId == saved.id || it.name.equals(saved.displayName, ignoreCase = true) }
                }
                if (otherSaved.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Or add saved account to game:",
                        style = MaterialTheme.typography.labelSmall.copy(color = AppTheme.colors.textSecondary)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        items(otherSaved) { savedUser ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(AppTheme.colors.surface)
                                    .border(1.dp, AppTheme.colors.gold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                    .clickable {
                                        players.add(
                                            Player(
                                                id = "user_${savedUser.id}",
                                                name = savedUser.displayName,
                                                colorHex = savedUser.avatarColorHex,
                                                userId = savedUser.id
                                            )
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "+ ${savedUser.avatarEmoji} ${savedUser.displayName}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.goldLight
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Safety Word Configuration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.surface)
                    .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "Safety", tint = AppTheme.colors.gold)
                        Text(
                            text = "ROOM SAFE WORD",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.goldLight
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Anyone can say this safe word anytime. It immediately pauses the game without questions.",
                        style = MaterialTheme.typography.bodySmall.copy(color = AppTheme.colors.textSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = roomSafeWord,
                        onValueChange = { roomSafeWord = it },
                        label = { Text("Safe Word") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonRed,
                            unfocusedBorderColor = AppTheme.colors.border,
                            focusedTextColor = AppTheme.colors.textPrimary,
                            unfocusedTextColor = AppTheme.colors.textPrimary,
                            focusedContainerColor = AppTheme.colors.surfaceElevated,
                            unfocusedContainerColor = AppTheme.colors.surfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("room_safeword_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Points Sharing Option
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.surface)
                    .border(1.dp, AppTheme.colors.border, RoundedCornerShape(16.dp))
                    .clickable { sharePoints = !sharePoints }
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = sharePoints,
                        onCheckedChange = { sharePoints = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CrimsonRed,
                            uncheckedColor = AppTheme.colors.textSecondary
                        )
                    )
                    Column {
                        Text(
                            text = "Split points among dare partners",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                        )
                        Text(
                            text = "If enabled, participants who opt into paired/group dares also earn points.",
                            style = MaterialTheme.typography.bodySmall.copy(color = AppTheme.colors.textSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Game Button
            SeductivePrimaryButton(
                text = "CONTINUE TO BOARD (${players.size} PLAYERS)",
                onClick = {
                    onStartGame(players.toList(), roomSafeWord, sharePoints)
                },
                enabled = players.size >= 2,
                testTag = "confirm_players_btn"
            )

            if (players.size < 2) {
                Text(
                    text = "Add at least 2 players to begin the game.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AppTheme.colors.textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
