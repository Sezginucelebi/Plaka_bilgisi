package com.sezgin.plaka_bilgisi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sezgin.plaka_bilgisi.model.Vehicle
import com.sezgin.plaka_bilgisi.repository.getAllVehicles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBackClick: () -> Unit,
    onUserManagementClick: () -> Unit // Yeni: Kullanıcı yönetimine geçiş için
) {
    var allVehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun loadData() {
        isLoading = true
        errorMessage = null
        getAllVehicles(
            onSuccess = {
                allVehicles = it
                isLoading = false
            },
            onFailure = {
                errorMessage = it.message
                isLoading = false
            }
        )
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yönetici Paneli", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    // 🔥 Yeni: Kullanıcı Yönetimi Butonu (Kişi simgesi)
                    IconButton(onClick = onUserManagementClick) {
                        Icon(Icons.Default.Group, contentDescription = "Kullanıcı Yönetimi")
                    }
                    IconButton(onClick = { loadData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hata: $errorMessage", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { loadData() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
            } else {
                if (allVehicles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Henüz kayıtlı araç yok.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allVehicles) { vehicle ->
                            AdminVehicleItem(vehicle)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminVehicleItem(vehicle: Vehicle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "PLAKA: ${vehicle.plate}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Marka/Model: ${vehicle.brand} ${vehicle.model}")
            Text(text = "Araç Sahibi: ${vehicle.ownerName}", style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = "Kaydı Yapan: ${vehicle.recordedBy}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
