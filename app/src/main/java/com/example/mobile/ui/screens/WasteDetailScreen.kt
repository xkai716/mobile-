package com.example.mobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile.model.WasteCategory
import com.example.mobile.model.WasteItem
import com.example.mobile.ui.theme.BgCream
import com.example.mobile.ui.theme.EcoGreen
import com.example.mobile.ui.theme.EcoGreenLight
import com.example.mobile.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteDetailScreen(
    item: WasteItem?,
    onLogDisposal: (WasteItem) -> Unit,
    onNavigateToMap: (WasteCategory) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = {
                    Text(item?.name ?: "Waste Details", fontWeight = FontWeight.Bold, color = EcoGreen)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCream)
            )
        }
    ) { innerPadding ->
        if (item == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Recycling, contentDescription = null, tint = EcoGreen)
                Spacer(Modifier.height(8.dp))
                Text("Waste item not found", fontWeight = FontWeight.Bold)
                Button(onClick = onBack, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Back to guide")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = item.photoResId),
                    contentDescription = "Photo of ${item.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoGreen)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Surface(color = Color.White.copy(alpha = 0.16f), shape = CircleShape) {
                            Text(
                                item.category.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(item.name, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.description, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
                    }
                }

                Card(
                    onClick = { onNavigateToMap(item.category) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = EcoGreenLight, shape = CircleShape) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EcoGreen,
                                modifier = Modifier.padding(9.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Recommended route", color = TextGray, fontSize = 11.sp)
                            Text(item.disposalType, color = EcoGreen, fontWeight = FontWeight.Bold)
                            Text(
                                if (item.recyclable) "Accepted by a suitable recycling stream" else "Check local acceptance first",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                        Text("Directions ↗", color = EcoGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text("How to dispose", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EcoGreen)
                item.steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(14.dp)).padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(color = EcoGreenLight, shape = CircleShape) {
                            Text(
                                "${index + 1}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                color = EcoGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, modifier = Modifier.weight(1f), fontSize = 14.sp)
                    }
                }

                item.reminder?.let { reminder ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Important", fontWeight = FontWeight.Bold, color = Color(0xFF8A4B08))
                            Text(reminder, fontSize = 13.sp, color = Color(0xFF6B4A24))
                        }
                    }
                }

                Button(
                    onClick = {
                        onLogDisposal(item)
                        Toast.makeText(context, "Disposal logged! +5 EcoPoints", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Log disposal and earn 5 points", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
