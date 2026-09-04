package com.example.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray

@Composable
fun RewardsScreen(points: Int, onRedeemReward: (Int) -> Boolean, onNavigateToProfile: () -> Unit) {
    val context = LocalContext.current
    var tab by remember { mutableIntStateOf(0) }
    var showInfo by remember { mutableStateOf(false) }
    
    // 模拟追踪兑换次数以解锁勋章
    var treesPlanted by remember { mutableIntStateOf(0) }
    var tumblersRedeemed by remember { mutableIntStateOf(0) }
    var vouchersRedeemed by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB)).verticalScroll(rememberScrollState()).padding(bottom = 28.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(onClick = onNavigateToProfile, shape = CircleShape, color = EcoGreenLight) {
                Icon(Icons.Default.Person, "Profile", tint = EcoGreen, modifier = Modifier.padding(8.dp))
            }
            Text("EcoPulse", Modifier.weight(1f).padding(start = 10.dp), color = EcoGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { showInfo = true }) { Icon(Icons.Outlined.Notifications, "Notifications") }
        }
        Text("Eco Rewards & Badges", Modifier.padding(horizontal = 16.dp), color = EcoGreen, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Turn your eco actions into tangible rewards.", Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = TextGray)
        PointsCard(points)
        TabRow(tab, containerColor = Color.Transparent, contentColor = EcoGreen, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            listOf("Rewards", "Badges", "Challenges").forEachIndexed { index, title ->
                Tab(tab == index, onClick = { tab = index }, text = { Text(title, fontSize = 12.sp) })
            }
        }
        when (tab) {
            0 -> {
                RewardsHeading("Featured Rewards")
                RewardRow("Plant 1 Real Tree", 1000, Icons.Default.Park, points) {
                    val success = onRedeemReward(1000)
                    if (success) treesPlanted++
                    rewardToast(context, success, "A tree will be planted! Forest Friend badge unlocked!")
                }
                RewardRow("Organic Coffee Voucher", 300, Icons.Default.Coffee, points) {
                    val success = onRedeemReward(300)
                    if (success) vouchersRedeemed++
                    rewardToast(context, success, "Voucher added! Community Hero badge unlocked!")
                }
                RewardRow("Reusable Bamboo Tumbler", 850, Icons.Default.WaterDrop, points) {
                    val success = onRedeemReward(850)
                    if (success) tumblersRedeemed++
                    rewardToast(context, success, "Tumbler redeemed! Ocean Savior badge unlocked!")
                }
                RewardRow("Eco-friendly Tote Bag", 500, Icons.Default.ShoppingBag, points) {
                    rewardToast(context, onRedeemReward(500), "Tote bag redeemed!")
                }
                RewardRow("Solar Power Bank", 2500, Icons.Default.WbSunny, points) {
                    rewardToast(context, onRedeemReward(2500), "Solar charger redeemed!")
                }
            }
            1 -> {
                RewardsHeading("Unlocked Badges")
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BadgeCard("Zero Waste", Icons.Default.Stars, Modifier.weight(1f))
                        BadgeCard("E-Waste Pro", Icons.Default.Devices, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BadgeCard("Eco Warrior", Icons.Default.Eco, Modifier.weight(1f))
                        BadgeCard("Clean Commuter", Icons.Default.DirectionsBike, Modifier.weight(1f))
                    }
                    
                    // 动态显示的解锁勋章
                    if (treesPlanted > 0 || tumblersRedeemed > 0 || vouchersRedeemed > 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (treesPlanted > 0) BadgeCard("Forest Friend", Icons.Default.Park, Modifier.weight(1f))
                            if (tumblersRedeemed > 0) BadgeCard("Ocean Savior", Icons.Default.WaterDrop, Modifier.weight(1f))
                            if (treesPlanted == 0 || tumblersRedeemed == 0) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                RewardsHeading("Locked Badges")
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BadgeCard("Forest Friend", Icons.Default.Park, Modifier.weight(1f), locked = treesPlanted == 0)
                        BadgeCard("Ocean Savior", Icons.Default.WaterDrop, Modifier.weight(1f), locked = tumblersRedeemed == 0)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BadgeCard("Community Hero", Icons.Default.VolunteerActivism, Modifier.weight(1f), locked = vouchersRedeemed == 0)
                        BadgeCard("Master Recycler", Icons.Default.Autorenew, Modifier.weight(1f), locked = points < 2000)
                    }
                }
                
                Spacer(Modifier.height(20.dp))
                Card(
                    Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFBC02D))
                        Spacer(Modifier.width(12.dp))
                        Text("Tip: Earn badges by completing specific challenges and logging your daily eco-actions!", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }
            else -> {
                RewardsHeading("Active Challenges")
                ChallengeCard("Zero Plastic Straws", 1f)
                ChallengeCard("Walk to Work", .16f)
            }
        }
    }
    if (showInfo) AlertDialog(
        onDismissRequest = { showInfo = false },
        title = { Text("Rewards updates") },
        text = { Text("Challenges, disposal logs, and tracker activities add EcoPoints to this live balance.") },
        confirmButton = { TextButton(onClick = { showInfo = false }) { Text("Close") } }
    )
}

private fun rewardToast(context: android.content.Context, success: Boolean, message: String) {
    Toast.makeText(context, if (success) message else "Not enough EcoPoints", Toast.LENGTH_LONG).show()
}

@Composable
private fun PointsCard(points: Int) {
    val target = 3000
    Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = EcoGreen), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(20.dp)) {
            Text("CURRENT BALANCE", color = Color.White.copy(.7f), fontSize = 10.sp)
            Text("$points EcoPoints", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            LinearProgressIndicator({ (points / target.toFloat()).coerceIn(0f, 1f) }, Modifier.fillMaxWidth().padding(top = 12.dp).height(7.dp).clip(RoundedCornerShape(4.dp)), color = Color.White, trackColor = Color.White.copy(.2f))
            Text(if (points >= target) "Platinum tier reached" else "${target - points} pts to Platinum tier", color = Color.White.copy(.8f), fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
private fun RewardRow(name: String, cost: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, points: Int, onRedeem: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = EcoGreen, modifier = Modifier.size(34.dp))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(name, fontWeight = FontWeight.Bold); Text("$cost pts", color = EcoGreen) }
            Button(onClick = onRedeem, enabled = points >= cost, colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)) { Text("Redeem", fontSize = 11.sp) }
        }
    }
}

@Composable
private fun BadgeCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, locked: Boolean = false) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if (locked) Color(0xFFF5F5F5) else Color.White),
        border = if (locked) null else androidx.compose.foundation.BorderStroke(1.dp, EcoGreenLight.copy(alpha = 0.5f))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                null,
                tint = if (locked) Color.LightGray else EcoGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = if (locked) Color.Gray else EcoGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            if (locked) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(10.dp))
                    Text(" Locked", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun ChallengeCard(title: String, progress: Float) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) { Text(title, fontWeight = FontWeight.Bold); LinearProgressIndicator({ progress }, Modifier.fillMaxWidth().padding(top = 10.dp), color = EcoGreen, trackColor = EcoGreenLight) }
    }
}

@Composable
private fun RewardsHeading(title: String) { Text(title, Modifier.padding(16.dp), color = EcoGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
