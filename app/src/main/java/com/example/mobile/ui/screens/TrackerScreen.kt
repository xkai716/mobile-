package com.example.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray
import com.example.mobile.ui.viewmodel.EcoLog

@Composable
fun TrackerScreen(
    logs: List<EcoLog>,
    onLogActivity: (String, String, Double) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    var period by remember { mutableIntStateOf(1) }
    var showLogDialog by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    val periodFactor = listOf(0.28, 1.0, 3.8)[period]
    val loggedCarbon = logs.sumOf { it.carbonSavedKg }
    val totalCarbon = (42.8 + loggedCarbon) * periodFactor

    Column(
        Modifier.fillMaxSize().background(Color(0xFFFBFBFB))
            .verticalScroll(rememberScrollState()).padding(bottom = 28.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(onClick = onNavigateToProfile, shape = CircleShape, color = EcoGreenLight) {
                Icon(Icons.Default.Person, "Profile", tint = EcoGreen, modifier = Modifier.padding(8.dp))
            }
            Text(
                "EcoPulse",
                Modifier.weight(1f).padding(start = 10.dp),
                color = EcoGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            IconButton(onClick = { showNotifications = true }) {
                Icon(Icons.Outlined.Notifications, "Notifications")
            }
        }

        Text(
            "Impact Tracker",
            Modifier.padding(horizontal = 16.dp),
            color = EcoGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Text(
            "See how your everyday choices add up.",
            Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = TextGray
        )

        PeriodSelector(period) { period = it }
        ImpactSummary(totalCarbon, logs.sumOf { it.pointsEarned })

        Button(
            onClick = { showLogDialog = true },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.AddCircle, null)
            Spacer(Modifier.width(8.dp))
            Text("Log Eco Activity")
        }

        Text(
            "Your Carbon Breakdown",
            Modifier.padding(16.dp),
            color = EcoGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BreakdownCard("Transport", "18.4 kg", Icons.Default.DirectionsBike, Modifier.weight(1f))
            BreakdownCard("Waste", "14.2 kg", Icons.Default.Recycling, Modifier.weight(1f))
            BreakdownCard("Energy", "10.2 kg", Icons.Default.Bolt, Modifier.weight(1f))
        }

        WeeklyChart()

        Text(
            "Recent Eco Activities",
            Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp),
            color = EcoGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        if (logs.isEmpty()) {
            Text("No activities yet. Log your first action!", Modifier.padding(16.dp), color = TextGray)
        } else {
            logs.take(5).forEach { ActivityRow(it) }
        }
    }

    if (showLogDialog) {
        LogActivityDialog(
            onDismiss = { showLogDialog = false },
            onSave = { title, category, carbon ->
                onLogActivity(title, category, carbon)
                showLogDialog = false
                Toast.makeText(context, "Activity logged and EcoPoints added", Toast.LENGTH_LONG).show()
            }
        )
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            title = { Text("Impact update") },
            text = { Text("Your new activities appear here instantly and update your EcoPoints balance.") },
            confirmButton = {
                TextButton(onClick = { showNotifications = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun PeriodSelector(selected: Int, onSelected: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Week", "Month", "Year").forEachIndexed { index, label ->
            if (selected == index) {
                Button(
                    onClick = { onSelected(index) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                ) { Text(label) }
            } else {
                OutlinedButton(
                    onClick = { onSelected(index) },
                    modifier = Modifier.weight(1f)
                ) { Text(label, color = EcoGreen) }
            }
        }
    }
}

@Composable
private fun ImpactSummary(carbon: Double, points: Int) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGreen),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("TOTAL CARBON SAVED", color = Color.White.copy(.72f), fontSize = 11.sp)
            Text(
                String.format("%.1f kg CO₂", carbon),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, null, tint = Color(0xFFB7E4C7), modifier = Modifier.size(18.dp))
                Text(" 12% above your previous period", color = Color.White.copy(.86f), fontSize = 12.sp)
            }
            if (points > 0) Text("+$points points from logged activities", color = Color.White.copy(.75f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun BreakdownCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(12.dp)) {
            Icon(icon, null, tint = EcoGreen)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 8.dp))
            Text(title, color = TextGray, fontSize = 11.sp)
        }
    }
}

@Composable
private fun WeeklyChart() {
    val values = listOf(.38f, .62f, .48f, .82f, .56f, .92f, .70f)
    val labels = listOf("M", "T", "W", "T", "F", "S", "S")
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Weekly activity", fontWeight = FontWeight.Bold)
            Row(
                Modifier.fillMaxWidth().height(120.dp).padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.width(18.dp).height((86 * value).dp)
                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                .background(if (index == 5) EcoGreen else EcoGreenLight)
                        )
                        Text(labels[index], color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(log: EcoLog) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = EcoGreenLight) {
                Icon(
                    if (log.category == "Transport") Icons.Default.DirectionsWalk else Icons.Default.Eco,
                    null,
                    tint = EcoGreen,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(log.title, fontWeight = FontWeight.SemiBold)
                Text("${log.category} · ${log.timeLabel}", color = TextGray, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(String.format("%.2f kg", log.carbonSavedKg), color = EcoGreen, fontWeight = FontWeight.Bold)
                Text("+${log.pointsEarned} pts", color = TextGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun LogActivityDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Transport") }
    var carbonText by remember { mutableStateOf("") }
    val carbon = carbonText.toDoubleOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Eco Activity") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What did you do?") },
                    singleLine = true
                )
                Text("Category", fontWeight = FontWeight.SemiBold)
                listOf("Transport", "Waste", "Energy").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = category == option, onClick = { category = option })
                        Text(option)
                    }
                }
                OutlinedTextField(
                    value = carbonText,
                    onValueChange = { carbonText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Estimated CO₂ saved (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title.trim(), category, carbon ?: 0.0) },
                enabled = title.isNotBlank() && carbon != null && carbon > 0,
                colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
