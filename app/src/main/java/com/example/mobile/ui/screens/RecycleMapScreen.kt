package com.example.mobile.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.R
import com.example.mobile.model.WasteCategory
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray

private data class RecycleStation(
    val name: String,
    val address: String,
    val category: String,
    val distanceKm: Double,
    val rating: Double,
    val hours: String,
    val tags: List<String>,
    val imageRes: Int,
    val specialAction: String? = null
)

@Composable
fun RecycleMapScreen(
    initialCategory: WasteCategory = WasteCategory.ALL,
    onNavigateToProfile: () -> Unit,
    onNavigateToWasteGuide: () -> Unit
) {
    val context = LocalContext.current
    var selectedMaterial by remember(initialCategory) { mutableStateOf(initialCategory) }
    var searchQuery by remember { mutableStateOf("") }
    var openOnly by remember { mutableStateOf(false) }
    var sortByRating by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var activeStation by remember { mutableStateOf<RecycleStation?>(null) }
    var stationDialogTitle by remember { mutableStateOf("") }

    val stations = remember {
        listOf(
            RecycleStation("GreenLife Community Center", "424 Atlantic Ave, Boerum Hill", "Plastic", 0.2, 4.8, "Open until 7:00 PM", listOf("PET Plastic", "Glass", "Cardboard"), R.drawable.screenshot_2026_09_05_022751, "Details & Hours"),
            RecycleStation("MetroTech Smart Kiosk", "15 MetroTech Causeway", "E-Waste", 0.8, 4.9, "Open 24/7", listOf("Phones", "Batteries", "Cables"), R.drawable.screenshot_2026_09_05_022859, "Reserve Locker"),
            RecycleStation("GreenPoint Waste & Glass", "72 Greenpoint Ave", "Glass", 1.2, 4.7, "Open until 5:30 PM", listOf("Glassware", "Books", "Shoes"), R.drawable.screenshot_2026_09_05_022912, "Drop-off Rules"),
            RecycleStation("Cadman Composting Site", "Cadman Plaza East", "Organic", 1.8, 4.5, "Open today until 5:00 PM", listOf("Food Scraps", "Yard Waste", "Coffee"), R.drawable.screenshot_2026_09_05_022919)
        )
    }

    val visibleStations = stations
        .filter { station ->
            val query = searchQuery.trim()
            val matchesMaterial = station.accepts(selectedMaterial)
            val matchesSearch = query.isBlank() ||
                station.name.contains(query, ignoreCase = true) ||
                station.category.contains(query, ignoreCase = true) ||
                station.tags.any { it.contains(query, ignoreCase = true) }
            val matchesOpen = !openOnly || station.hours.contains("Open", ignoreCase = true)
            matchesMaterial && matchesSearch && matchesOpen
        }
        .let { results ->
            if (sortByRating) results.sortedByDescending { it.rating }
            else results.sortedBy { it.distanceKm }
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB)),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onNavigateToProfile)) {
                    Surface(shape = CircleShape, color = EcoGreenLight, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = EcoGreen, modifier = Modifier.padding(7.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("EcoPulse", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                        Text("Nearby Eco Stations", fontSize = 10.sp, color = TextGray)
                    }
                }
                IconButton(onClick = { showNotifications = true }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Map notifications")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WasteSubNavTab(
                    label = "Sorting Guide",
                    icon = Icons.Default.MenuBook,
                    selected = false,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWasteGuide
                )
                WasteSubNavTab(
                    label = "Search Map",
                    icon = Icons.Default.Map,
                    selected = true,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Downtown Brooklyn", fontSize = 13.sp)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Location refreshed", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh location", modifier = Modifier.padding(10.dp), tint = TextGray)
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search centres or accepted materials", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = EcoGreen,
                    unfocusedBorderColor = Color(0xFFEEEEEE)
                )
            )

            Text("FILTER BY MATERIAL", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(WasteCategory.entries, key = { it.name }) { material ->
                    MapFilterChip(
                        label = material.label,
                        selected = selectedMaterial == material,
                        icon = materialIcon(material.label),
                        onClick = { selectedMaterial = material }
                    )
                }
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    MapFilterChip("Nearest", !sortByRating, Icons.Default.NearMe) { sortByRating = false }
                }
                item {
                    MapFilterChip("Open Now", openOnly, Icons.Outlined.AccessTime) { openOnly = !openOnly }
                }
                item {
                    MapFilterChip("Highest Rated", sortByRating, Icons.Outlined.StarBorder) { sortByRating = true }
                }
            }

            // Removed map placeholder card as requested

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${visibleStations.size} Stations Found", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text(if (sortByRating) "By Rating" else "By Distance", fontSize = 11.sp)
                }
            }
        }

        if (visibleStations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text("No stations match your search.", modifier = Modifier.padding(22.dp), color = TextGray)
                }
            }
        } else {
            items(visibleStations, key = { it.name }) { station ->
                StationCard(
                    station = station,
                    onSpecialAction = {
                        activeStation = station
                        stationDialogTitle = station.specialAction ?: "Station details"
                    },
                    onNavigate = { openStationInMaps(context, station) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    activeStation?.let { station ->
        AlertDialog(
            onDismissRequest = { activeStation = null },
            title = { Text(stationDialogTitle, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    when (stationDialogTitle) {
                        "Reserve Locker" -> "A 30-minute drop-off slot is ready to reserve at ${station.name}."
                        "Drop-off Rules" -> "Keep items clean and dry. Separate batteries and wrap broken glass safely."
                        else -> "${station.address}\n${station.hours}\nAccepts: ${station.tags.joinToString()}."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { activeStation = null }) {
                    Text(if (stationDialogTitle == "Reserve Locker") "Reserve" else "Close", color = EcoGreen)
                }
            }
        )
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            title = { Text("Map updates", fontWeight = FontWeight.Bold) },
            text = { Text("Centre availability and accepted material information is shown for the assignment prototype.") },
            confirmButton = { TextButton(onClick = { showNotifications = false }) { Text("Close", color = EcoGreen) } }
        )
    }
}

@Composable
private fun MapFilterChip(label: String, selected: Boolean, icon: ImageVector?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) EcoGreen else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(15.dp), tint = if (selected) Color.White else EcoGreen)
                Spacer(Modifier.width(5.dp))
            }
            Text(label, color = if (selected) Color.White else TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StationCard(station: RecycleStation, onSpecialAction: () -> Unit, onNavigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = station.imageRes),
                    contentDescription = station.name,
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(station.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = EcoGreen, modifier = Modifier.size(15.dp))
                    }
                    Text(station.address, color = TextGray, fontSize = 10.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF9A825), modifier = Modifier.size(14.dp))
                        Text("${station.rating} • ${station.distanceKm} km", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(station.hours, color = EcoGreen, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(station.tags) { tag ->
                    Surface(color = Color(0xFFF1F8F1), shape = RoundedCornerShape(5.dp)) {
                        Text(tag, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), color = EcoGreen, fontSize = 9.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                station.specialAction?.let {
                    Button(
                        onClick = onSpecialAction,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenLight, contentColor = EcoGreen)
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(it, fontSize = 10.sp)
                    }
                }
                Button(
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                ) {
                    Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Map", fontSize = 11.sp)
                }
            }
        }
    }
}

private fun RecycleStation.accepts(material: WasteCategory): Boolean {
    if (material == WasteCategory.ALL) return true

    val acceptedText = "$category ${tags.joinToString(" ")}".lowercase()
    val keywords = when (material) {
        WasteCategory.ALL -> emptyList()
        WasteCategory.PLASTIC -> listOf("plastic", "pet")
        WasteCategory.PAPER -> listOf("paper", "cardboard", "book")
        WasteCategory.GLASS -> listOf("glass")
        WasteCategory.ORGANIC -> listOf("organic", "compost", "food", "yard")
        WasteCategory.METAL -> listOf("metal", "aluminium", "aluminum", "can")
        WasteCategory.E_WASTE -> listOf("e-waste", "electronic", "phone", "battery", "batteries", "cable", "device")
    }
    return keywords.any(acceptedText::contains)
}

private fun materialIcon(material: String): ImageVector? = when (material.lowercase()) {
    "all" -> Icons.Default.CheckCircle
    "plastic" -> Icons.Default.Autorenew
    "paper" -> Icons.Default.Description
    "e-waste" -> Icons.Default.Devices
    "glass" -> Icons.Default.WineBar
    "organic" -> Icons.Default.Eco
    "metal" -> Icons.Default.Recycling
    else -> null
}

private fun openStationInMaps(context: android.content.Context, station: RecycleStation) {
    val query = Uri.encode("${station.name}, ${station.address}")
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$query"))
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "No maps application is available", Toast.LENGTH_SHORT).show()
    }
}
