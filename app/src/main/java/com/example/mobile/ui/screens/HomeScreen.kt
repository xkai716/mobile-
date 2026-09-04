package com.example.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray

@Composable
fun HomeScreen(
    userName: String,
    points: Int,
    challengeCompleted: Boolean,
    onCompleteChallenge: () -> Unit,
    onNavigateToWaste: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToTracker: () -> Unit,
    onNavigateToRewards: () -> Unit
) {
    val context = LocalContext.current
    var showNotifications by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onNavigateToProfile)) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = EcoGreenLight) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = EcoGreen, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Hello, $userName!", fontSize = 13.sp, color = TextGray)
                    Text("EcoPulse", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                }
            }
            IconButton(onClick = { showNotifications = true }) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
            }
        }

        EcoImpactCard(points)

        HomeSectionTitle("Today's Challenge")
        TodayChallengeCard(
            completed = challengeCompleted,
            onComplete = {
                if (!challengeCompleted) {
                    onCompleteChallenge()
                    Toast.makeText(context, "Challenge completed! +5 pts", Toast.LENGTH_SHORT).show()
                }
            }
        )

        HomeSectionTitle("Quick Actions")
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeAction("Waste Guide", Icons.Default.MenuBook, Modifier.weight(1f), onNavigateToWaste)
                HomeAction("Recycle", Icons.Default.Autorenew, Modifier.weight(1f), onNavigateToMap)
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeAction("Carbon Tracker", Icons.Default.BarChart, Modifier.weight(1f), onNavigateToTracker)
                HomeAction("Rewards", Icons.Default.Stars, Modifier.weight(1f), onNavigateToRewards)
            }
        }

        HomeSectionTitle("Community Impact")
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = EcoGreen)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Forest, contentDescription = null, tint = Color(0xFFB8E6C1), modifier = Modifier.size(44.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("3.2 tonnes saved this month", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Small actions from the EcoPulse community add up.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            title = { Text("Notifications", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HomeNotification("Welcome", "Start your sustainable journey today.")
                    HomeNotification("New challenge", "Zero-waste week is now active.")
                    HomeNotification("EcoPoints", "Correct disposal and tracker logs earn points.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotifications = false }) { Text("Close", color = EcoGreen) }
            }
        )
    }
}

@Composable
private fun EcoImpactCard(points: Int) {
    val target = 3000
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGreen)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text("YOUR ECO IMPACT", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$points", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Text(" / $target pts", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (points / target.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f)) {
                    Text("Level: Nature Guardian", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
                Text("↗ +15% this week", color = Color(0xFFB8E6C1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TodayChallengeCard(completed: Boolean, onComplete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(12.dp)).background(EcoGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Say no to plastic straws", fontWeight = FontWeight.Bold)
                Text("Save 5g of plastic waste today.", color = TextGray, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onComplete,
                    enabled = !completed,
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (completed) "Completed" else "Complete Task", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun HomeAction(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1.25f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, color = EcoGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HomeSectionTitle(title: String) {
    Text(
        title,
        color = EcoGreen,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
    )
}

@Composable
private fun HomeNotification(title: String, message: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, color = EcoGreen)
        Text(message, fontSize = 12.sp, color = TextGray)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray.copy(alpha = 0.3f))
    }
}
