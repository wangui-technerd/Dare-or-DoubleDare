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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.SeductiveGoldButton
import com.example.ui.components.SeductivePrimaryButton
import com.example.ui.components.SeductiveSecondaryButton
import com.example.ui.components.parseColor
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.LightGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.PlayerColors
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SoftCharcoal

@Composable
fun AuthScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    errorMessage: String?,
    successMessage: String?,
    onLogin: (username: String, pin: String) -> Unit,
    onRegister: (
        username: String,
        pin: String,
        displayName: String,
        avatarEmoji: String,
        avatarColorHex: String,
        safeWord: String,
        intensity: Int
    ) -> Unit,
    onSelectUser: (UserEntity) -> Unit,
    onContinueAsGuest: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (currentUser != null) 2 else 0) }
    val scrollState = rememberScrollState()

    // Sign In states
    var loginUsername by remember { mutableStateOf("") }
    var loginPin by remember { mutableStateOf("") }

    // Register states
    var regUsername by remember { mutableStateOf("") }
    var regPin by remember { mutableStateOf("") }
    var regDisplayName by remember { mutableStateOf("") }
    var regSafeWord by remember { mutableStateOf("Pineapple") }
    var regAvatarEmoji by remember { mutableStateOf("💋") }
    var regAvatarColorHex by remember { mutableStateOf("#C92A45") }
    var regIntensity by remember { mutableIntStateOf(2) }

    val emojis = listOf("💋", "🍷", "👑", "🔥", "🖤", "🎭", "🧊", "🃏", "🌹", "🍒")
    val colors = listOf("#C92A45", "#E5C365", "#8B2247", "#5C7C8C", "#8A5A9E", "#6FA98A", "#E598A8")

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("auth_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppTheme.colors.gold)
                }
                Text(
                    text = "USER AUTHENTICATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.goldLight,
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = AppTheme.colors.surface,
                contentColor = AppTheme.colors.gold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AppTheme.colors.gold
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AppTheme.colors.border, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Sign In", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Register", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Profiles", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error / Success Feedback
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SafetyRed.copy(alpha = 0.2f))
                        .border(1.dp, SafetyRed, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall.copy(color = CreamWhite)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SafetyGreen.copy(alpha = 0.2f))
                        .border(1.dp, SafetyGreen, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = successMessage,
                        style = MaterialTheme.typography.bodySmall.copy(color = CreamWhite)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Sign In Tab
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Access Your Profile & History",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CreamWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        OutlinedTextField(
                            value = loginUsername,
                            onValueChange = { loginUsername = it },
                            label = { Text("Username") },
                            singleLine = true,
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input")
                        )

                        OutlinedTextField(
                            value = loginPin,
                            onValueChange = { loginPin = it },
                            label = { Text("PIN or Passcode") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_pin_input")
                        )

                        SeductivePrimaryButton(
                            text = "SIGN IN",
                            onClick = { onLogin(loginUsername, loginPin) },
                            enabled = loginUsername.isNotBlank() && loginPin.isNotBlank(),
                            testTag = "login_submit_btn"
                        )

                        if (allUsers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Or quick-select saved account:",
                                style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal)
                            )
                            allUsers.forEach { user ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ObsidianCard)
                                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                                        .clickable { onSelectUser(user) }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(parseColor(user.avatarColorHex)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(user.avatarEmoji, fontSize = 16.sp)
                                        }
                                        Column {
                                            Text(
                                                text = user.displayName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = CreamWhite
                                                )
                                            )
                                            Text(
                                                text = "@${user.username} • Safe: ${user.safeWord}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        SeductiveSecondaryButton(
                            text = "CONTINUE AS GUEST",
                            onClick = onContinueAsGuest,
                            testTag = "guest_login_btn"
                        )
                    }
                }

                1 -> {
                    // Register Tab
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Create Intimate User Account",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CreamWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        OutlinedTextField(
                            value = regDisplayName,
                            onValueChange = { regDisplayName = it },
                            label = { Text("Display Name (e.g. Elena)") },
                            singleLine = true,
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_name_input")
                        )

                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = { regUsername = it },
                            label = { Text("Username Handle (e.g. elena_x)") },
                            singleLine = true,
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_username_input")
                        )

                        OutlinedTextField(
                            value = regPin,
                            onValueChange = { regPin = it },
                            label = { Text("PIN / Passcode") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_pin_input")
                        )

                        OutlinedTextField(
                            value = regSafeWord,
                            onValueChange = { regSafeWord = it },
                            label = { Text("Personal Safe Word (Default: Pineapple)") },
                            singleLine = true,
                            colors = customTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_safeword_input")
                        )

                        // Avatar Emoji selection
                        Text(
                            text = "Pick Avatar Icon:",
                            style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            emojis.take(5).forEach { em ->
                                val isSelected = em == regAvatarEmoji
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CrimsonRed else ObsidianCardElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) ChampagneGold else ObsidianBorder,
                                            CircleShape
                                        )
                                        .clickable { regAvatarEmoji = em },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = em, fontSize = 20.sp)
                                }
                            }
                        }

                        // Avatar Color selection
                        Text(
                            text = "Pick Brand Color:",
                            style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            colors.forEach { hex ->
                                val isSelected = hex == regAvatarColorHex
                                val c = parseColor(hex)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(c)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) ChampagneGold else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { regAvatarColorHex = hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = CreamWhite,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        SeductiveGoldButton(
                            text = "CREATE PROFILE & SAVE",
                            onClick = {
                                onRegister(
                                    regUsername,
                                    regPin,
                                    regDisplayName,
                                    regAvatarEmoji,
                                    regAvatarColorHex,
                                    regSafeWord,
                                    regIntensity
                                )
                                selectedTab = 2
                            },
                            enabled = regUsername.isNotBlank() && regPin.isNotBlank() && regDisplayName.isNotBlank(),
                            testTag = "reg_submit_btn"
                        )
                    }
                }

                2 -> {
                    // Profiles Tab
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if (currentUser != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(ObsidianCardElevated)
                                    .border(1.5.dp, ChampagneGold, RoundedCornerShape(16.dp))
                                    .padding(18.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(CircleShape)
                                                .background(parseColor(currentUser.avatarColorHex)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(currentUser.avatarEmoji, fontSize = 28.sp)
                                        }
                                        Column {
                                            Text(
                                                text = currentUser.displayName,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = LightGold
                                                )
                                            )
                                            Text(
                                                text = "@${currentUser.username}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = SoftCharcoal)
                                            )
                                            Text(
                                                text = "Safe Word: ${currentUser.safeWord}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ChampagneGold,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Lifetime Stats Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        StatColumn("Games", "${currentUser.gamesPlayed}")
                                        StatColumn("Dares Done", "${currentUser.daresCompleted}")
                                        StatColumn("Total Score", "${currentUser.totalPoints} pts")
                                    }
                                }
                            }

                            SeductiveSecondaryButton(
                                text = "LOGOUT / SWITCH",
                                onClick = onLogout,
                                testTag = "logout_btn"
                            )
                        } else {
                            Text(
                                text = "No active user logged in. You are currently in Guest mode.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SoftCharcoal)
                            )
                        }

                        if (allUsers.isNotEmpty()) {
                            Text(
                                text = "All Saved Accounts on this Device (${allUsers.size}):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            allUsers.forEach { user ->
                                val isCurrent = currentUser?.id == user.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isCurrent) ObsidianCardElevated else ObsidianCard)
                                        .border(
                                            1.dp,
                                            if (isCurrent) ChampagneGold else ObsidianBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectUser(user) }
                                        .padding(12.dp)
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
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(parseColor(user.avatarColorHex)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(user.avatarEmoji, fontSize = 16.sp)
                                            }
                                            Column {
                                                Text(
                                                    text = user.displayName,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = CreamWhite
                                                    )
                                                )
                                                Text(
                                                    text = "${user.gamesPlayed} games • ${user.totalPoints} pts",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = SoftCharcoal)
                                                )
                                            }
                                        }

                                        if (isCurrent) {
                                            Text(
                                                text = "ACTIVE",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ChampagneGold
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
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = LightGold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = SoftCharcoal,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CrimsonRed,
    unfocusedBorderColor = AppTheme.colors.border,
    focusedLabelColor = AppTheme.colors.gold,
    unfocusedLabelColor = AppTheme.colors.textSecondary,
    focusedTextColor = AppTheme.colors.textPrimary,
    unfocusedTextColor = AppTheme.colors.textPrimary,
    cursorColor = AppTheme.colors.gold,
    focusedContainerColor = AppTheme.colors.surface,
    unfocusedContainerColor = AppTheme.colors.surface
)
