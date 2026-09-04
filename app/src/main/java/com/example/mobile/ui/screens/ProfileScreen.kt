package com.example.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray

@Composable
fun ProfileScreen(
    userName: String,
    points: Int,
    onUserNameChange: (String) -> Unit,
    onNavigateToRewards: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var dialog by remember { mutableStateOf<ProfileDialog?>(null) }
    var draftName by remember(userName) { mutableStateOf(userName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp)
    ) {
        ProfileHero(
            userName = userName,
            points = points,
            onNotificationsClick = { dialog = ProfileDialog.Notifications }
        )

        ProfileSectionTitle("Your Rewards", "Open store", onNavigateToRewards)
        Card(
            onClick = onNavigateToRewards,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = EcoGreenLight),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(36.dp))
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Available to redeem", fontSize = 12.sp, color = TextGray)
                    Text("$points EcoPoints", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EcoGreen)
            }
        }

        ProfileSectionTitle("Recent Achievements")
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Achievement("Recycle King", Icons.Default.Verified, Modifier.weight(1f))
            Achievement("Early Bird", Icons.Default.WbSunny, Modifier.weight(1f))
            Achievement("Eco Guru", Icons.Default.AutoGraph, Modifier.weight(1f))
        }

        ProfileSectionTitle("Account")
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            ProfileSetting("Edit Profile", Icons.Outlined.Edit) {
                draftName = userName
                dialog = ProfileDialog.Edit
            }
            ProfileSetting("Notifications", Icons.Outlined.Notifications) { dialog = ProfileDialog.Notifications }
            ProfileSetting("Privacy Policy", Icons.Outlined.PrivacyTip) { dialog = ProfileDialog.Privacy }
            ProfileSetting("Help & Support", Icons.Outlined.HelpOutline) { dialog = ProfileDialog.Help }
            ProfileSetting("Logout", Icons.Outlined.Logout, Color(0xFFB3261E)) { dialog = ProfileDialog.Logout }
        }
    }

    when (dialog) {
        ProfileDialog.Edit -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Edit profile", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    label = { Text("Display name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUserNameChange(draftName)
                        dialog = null
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    },
                    enabled = draftName.isNotBlank()
                ) { Text("Save", color = EcoGreen) }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } }
        )

        ProfileDialog.Notifications -> InformationDialog(
            title = "Account alerts",
            message = "Your EcoPoints, challenge, and reward updates will appear here.",
            onDismiss = { dialog = null }
        )

        ProfileDialog.Privacy -> InformationDialog(
            title = "Privacy policy",
            message = "Privacy policy will show at here.",
            onDismiss = { dialog = null }
        )

        ProfileDialog.Help -> InformationDialog(
            title = "Help & support",
            message = "Help and support will show at here.",
            onDismiss = { dialog = null }
        )

        ProfileDialog.Logout -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to log out of EcoPulse?") },
            confirmButton = {
                TextButton(onClick = { dialog = null; onLogout() }) {
                    Text("Logout", color = Color(0xFFB3261E))
                }
            },
            dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } }
        )

        null -> Unit
    }
}

private enum class ProfileDialog { Edit, Notifications, Privacy, Help, Logout }

@Composable
private fun ProfileHero(userName: String, points: Int, onNotificationsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(EcoGreen, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .padding(top = 12.dp, bottom = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier.align(Alignment.TopEnd).padding(horizontal = 12.dp)
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 28.dp)) {
            Surface(modifier = Modifier.size(92.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.18f)) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(18.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Sustainability Leader", fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProfileStat("Impact", "$points pts")
                ProfileStat("Rank", "Top 5%")
                ProfileStat("Streak", "15 days")
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
    }
}

@Composable
private fun Achievement(title: String, icon: ImageVector, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = Color(0xFFFFF8E1)) {
                Icon(icon, contentDescription = null, tint = Color(0xFFF9A825), modifier = Modifier.padding(10.dp).size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(title, color = EcoGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileSetting(title: String, icon: ImageVector, color: Color = EcoGreen, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(Modifier.width(14.dp))
        Text(title, modifier = Modifier.weight(1f), color = color, fontWeight = FontWeight.Medium)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
    HorizontalDivider(color = Color(0xFFEEEEEE))
}

@Composable
private fun ProfileSectionTitle(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
        action?.let {
            Text(it, fontSize = 12.sp, color = EcoGreen, modifier = Modifier.clickable(onClick = onAction))
        }
    }
}

@Composable
private fun InformationDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = EcoGreen) } }
    )
}
