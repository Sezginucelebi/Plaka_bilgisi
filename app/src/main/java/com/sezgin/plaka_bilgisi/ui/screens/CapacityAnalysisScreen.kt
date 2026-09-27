package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sezgin.plaka_bilgisi.repository.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapacityAnalysisScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var firms by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadData() {
        isLoading = true
        getAllUsers(
            onSuccess = { allUsers ->
                // Sadece Kurumsal Firmaları Analiz Et
                firms = allUsers.filter { it.role == "corporate" || it.role == "admin" }
                isLoading = false 
            },
            onFailure = { 
                Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                isLoading = false 
            }
        )
    }

    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kapasite Analizi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(firms) { firm ->
                        CapacityItem(firm)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CapacityItem(user: UserProfile) {
    var vehicleCount by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(user.uid) {
        getVehicleCount(user.uid) { vehicleCount = it }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)) // Koyu Tema Uyumu
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "Firma Kodu: ${user.firmCode}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF30363D))

            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(1.2f)) {
                    val remaining = if(user.isPremium) "Sınırsız" else (user.maxVehicles - vehicleCount).coerceAtLeast(0).toString()
                    Text("Kullanım Durumu", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    LinearProgressIndicator(
                        progress = { if (user.isPremium) 0.1f else (vehicleCount / user.maxVehicles.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = if (vehicleCount >= user.maxVehicles && !user.isPremium) Color.Red else Color(0xFF2F81F7),
                        trackColor = Color(0xFF30363D)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$vehicleCount / ${if(user.isPremium) "∞" else user.maxVehicles} ARAÇ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "KALAN KAYIT: $remaining",
                        style = MaterialTheme.typography.labelMedium,
                        color = if(remaining == "0") Color.Red else Color(0xFF4CAF50)
                    )
                }
                
                Column(Modifier.weight(0.8f)) {
                    Text("Paket & Ödeme", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Surface(
                        color = if(user.isPremium) Color(0xFF238636) else Color(0xFF8B949E),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = user.plan.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if(user.expiryDate.isNotBlank()) "Son: ${user.expiryDate}" else "Süresiz",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
