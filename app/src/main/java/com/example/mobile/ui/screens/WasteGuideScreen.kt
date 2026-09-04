package com.example.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.mobile.model.WasteCategory
import com.example.mobile.model.WasteItem
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray
import com.example.mobile.ui.viewmodel.WasteGuideUiState

@Composable
fun WasteGuideScreen(
    uiState: WasteGuideUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (WasteCategory) -> Unit,
    onClearFilters: () -> Unit,
    onWasteItemClick: (Int) -> Unit,
    onLogDisposal: (WasteItem) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToMapForCategory: (WasteCategory) -> Unit
) {
    val context = LocalContext.current
    var showNotifications by remember { mutableStateOf(false) }
    var showAllCategories by remember { mutableStateOf(false) }
    var showAllGuides by remember { mutableStateOf(false) }
    var allMatchesExpanded by remember { mutableStateOf(true) }
    var nearbyStationsExpanded by remember { mutableStateOf(true) }
    val featuredItem = uiState.visibleItems.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp)
    ) {
        WasteTopBar(
            onNotificationsClick = { showNotifications = true },
            onProfileClick = onNavigateToProfile
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WasteSubNavTab(
                label = "Sorting Guide",
                icon = Icons.Default.MenuBook,
                selected = true,
                modifier = Modifier.weight(1f),
                onClick = {}
            )
            WasteSubNavTab(
                label = "Search Map",
                icon = Icons.Default.Map,
                selected = false,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToMap
            )
        }

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search items, e.g. coffee cup", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedBorderColor = EcoGreen
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(onClick = onNavigateToMap),
            color = Color(0xFFE8F5E9),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Need to dispose nearby?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Find verified recycling drop-offs", fontSize = 11.sp, color = Color(0xFF2E7D32))
                }
                Text("View on Map →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (uiState.selectedCategory == WasteCategory.ALL) " " else "Clear filter",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.clickable(onClick = onClearFilters)
                )
                Text(
                    text = if (showAllCategories) "Show less" else "See all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreen,
                    modifier = Modifier.clickable { showAllCategories = !showAllCategories }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        WasteCategoriesRow(
            selectedCategory = uiState.selectedCategory,
            showAll = showAllCategories,
            onCategoryClick = onCategorySelected
        )

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Disposal Guide", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
            Column(horizontalAlignment = Alignment.End) {
                Text("${uiState.visibleItems.size} matching items", fontSize = 12.sp, color = TextGray)
                if (uiState.visibleItems.size > 1) {
                    Text(
                        text = if (showAllGuides) "Show less" else "See all",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreen,
                        modifier = Modifier.clickable { showAllGuides = !showAllGuides }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (featuredItem == null) {
            EmptyWasteResult(onClearFilters)
        } else {
            val guideItems = if (showAllGuides) uiState.visibleItems else listOf(featuredItem)
            guideItems.forEachIndexed { index, item ->
                DisposalGuideCard(
                    item = item,
                    onOpenDetails = { onWasteItemClick(item.id) },
                    onNavigateToMap = { onNavigateToMapForCategory(item.category) },
                    onLogDisposal = {
                        onLogDisposal(item)
                        Toast.makeText(context, "Disposal logged! +5 EcoPoints", Toast.LENGTH_SHORT).show()
                    }
                )
                if (index < guideItems.lastIndex) Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(26.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { allMatchesExpanded = !allMatchesExpanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "All Matches",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreen,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (allMatchesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (allMatchesExpanded) "Hide all matches" else "Show all matches",
                    tint = EcoGreen
                )
            }
            if (allMatchesExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                uiState.visibleItems.forEach { item ->
                    WasteResultCard(item = item, onClick = { onWasteItemClick(item.id) })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { nearbyStationsExpanded = !nearbyStationsExpanded }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Nearby Stations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                Text("Prototype locations", fontSize = 12.sp, color = TextGray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Open map",
                    fontSize = 12.sp,
                    color = EcoGreen,
                    modifier = Modifier.clickable(onClick = onNavigateToMap)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (nearbyStationsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (nearbyStationsExpanded) "Hide nearby stations" else "Show nearby stations",
                    tint = EcoGreen
                )
            }
        }

        if (nearbyStationsExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            StationListItem(
                name = "GreenLife Community Hub",
                address = "424 Atlantic Ave, Boerum Hill",
                rating = "4.8",
                status = "Open until 7:00 PM",
                distance = "0.2 km",
                tags = listOf("PET Plastic", "Cardboard"),
                onOpenMap = onNavigateToMap
            )
            Spacer(modifier = Modifier.height(12.dp))
            StationListItem(
                name = "MetroTech Smart E-Waste Locker",
                address = "15 MetroTech Causeway (Plaza Level)",
                rating = "4.9",
                status = "24/7 automated access",
                distance = "0.8 km",
                tags = listOf("Batteries", "Electronics"),
                onOpenMap = onNavigateToMap
            )
        }
    }

    if (showNotifications) {
        WasteNotificationDialog(onDismiss = { showNotifications = false })
    }
}

@Composable
internal fun WasteSubNavTab(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (selected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) BorderStroke(1.dp, Color(0xFFEEEEEE)) else null,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selected) EcoGreen else TextGray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WasteCategoriesRow(
    selectedCategory: WasteCategory,
    showAll: Boolean,
    onCategoryClick: (WasteCategory) -> Unit
) {
    if (showAll) {
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WasteCategory.entries.forEach { category ->
                WasteCategoryButton(
                    category = category,
                    selected = selectedCategory == category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    } else {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(WasteCategory.entries, key = { it.name }) { category ->
                WasteCategoryButton(
                    category = category,
                    selected = selectedCategory == category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
private fun WasteCategoryButton(
    category: WasteCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selected) EcoGreen else EcoGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon(category),
                contentDescription = category.label,
                tint = if (selected) Color.White else EcoGreen,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoGreen)
    }
}

private fun categoryIcon(category: WasteCategory): ImageVector = when (category) {
    WasteCategory.ALL -> Icons.Default.CheckCircle
    WasteCategory.PLASTIC -> Icons.Default.Autorenew
    WasteCategory.PAPER -> Icons.Default.Description
    WasteCategory.GLASS -> Icons.Default.WineBar
    WasteCategory.ORGANIC -> Icons.Default.Eco
    WasteCategory.METAL -> Icons.Default.Recycling
    WasteCategory.E_WASTE -> Icons.Default.Devices
}

@Composable
private fun DisposalGuideCard(
    item: WasteItem,
    onOpenDetails: () -> Unit,
    onNavigateToMap: () -> Unit,
    onLogDisposal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(color = EcoGreenLight, shape = RoundedCornerShape(50)) {
                Text(
                    item.category.label,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreen
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = item.photoResId),
                contentDescription = "Photo of ${item.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
            Text(item.description, fontSize = 14.sp, color = TextGray)

            Spacer(modifier = Modifier.height(20.dp))
            item.steps.take(3).forEachIndexed { index, step ->
                GuideStepItem(
                    stepNumber = index + 1,
                    title = step,
                    icon = if (index == 0) Icons.Default.Opacity else Icons.Default.VerticalAlignBottom
                )
                if (index < item.steps.take(3).lastIndex) Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToMap),
                color = Color(0xFFF9F9F9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recommended route", fontSize = 11.sp, color = TextGray)
                        Text(item.disposalType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                    }
                    Text("Directions ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                }
            }

            item.reminder?.let { reminder ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFF4E5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Important", fontWeight = FontWeight.Bold, color = Color(0xFF8A4B08))
                        Text(reminder, fontSize = 12.sp, color = Color(0xFF6B4A24))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onOpenDetails) {
                    Text("Full guide", color = EcoGreen, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onLogDisposal,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LOG +5 PTS", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GuideStepItem(stepNumber: Int, title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF9F9F9), RoundedCornerShape(16.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("STEP $stepNumber", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
        }
    }
}

@Composable
private fun WasteResultCard(item: WasteItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = item.photoResId),
                contentDescription = "Photo of ${item.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.category.label, color = EcoGreen, fontSize = 11.sp)
                Text(item.description, color = TextGray, fontSize = 11.sp, maxLines = 2)
            }
            Icon(Icons.Default.NearMe, contentDescription = "Open guide", tint = EcoGreen)
        }
    }
}

@Composable
private fun EmptyWasteResult(onClearFilters: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = EcoGreen, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No matching waste item", fontWeight = FontWeight.Bold)
            Text("Try a different search or category.", color = TextGray, fontSize = 13.sp)
            TextButton(onClick = onClearFilters) { Text("Clear filters", color = EcoGreen) }
        }
    }
}

@Composable
private fun StationListItem(
    name: String,
    address: String,
    rating: String,
    status: String,
    distance: String,
    tags: List<String>,
    onOpenMap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                    Text(address, fontSize = 12.sp, color = TextGray)
                }
                Text(distance, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBC02D), modifier = Modifier.size(14.dp))
                Text(rating, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(" • $status", fontSize = 12.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    tags.take(2).forEach { tag ->
                        Surface(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp)) {
                            Text(tag, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, color = TextGray)
                        }
                    }
                }
                OutlinedButton(
                    onClick = onOpenMap,
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Map", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun WasteTopBar(onNotificationsClick: () -> Unit, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onProfileClick)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray)) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("EcoPulse", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                Text("Waste & Drop-off", fontSize = 12.sp, color = TextGray)
            }
        }
        IconButton(onClick = onNotificationsClick) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Waste guide notifications")
        }
    }
}

@Composable
private fun WasteNotificationDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Waste Guide Tips", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("• Rinse plastic containers before recycling.")
                Text("• Batteries should never go into ordinary household bins.")
                Text("• Coffee grounds can be composted when your system accepts them.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = EcoGreen) }
        }
    )
}
