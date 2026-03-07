package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.sezgin.plaka_bilgisi.model.Vehicle
import com.sezgin.plaka_bilgisi.repository.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSearchScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val userId = currentUser?.uid ?: ""
    val userEmail = currentUser?.email ?: ""
    
    var allVehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var filteredVehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }

    // 🔥 Yetki kontrolü ve veri çekme fonksiyonu
    fun loadData() {
        isLoading = true
        val isMasterAdmin = userEmail == "admin@adminmax.com"
        if (isMasterAdmin) {
            getAllVehicles(
                onSuccess = { result ->
                    allVehicles = result
                    filteredVehicles = result
                    isLoading = false
                },
                onFailure = { exception ->
                    errorMessage = exception.message
                    isLoading = false
                }
            )
        } else {
            checkIfAdmin(userId) { isAdmin ->
                if (isAdmin) {
                    getAllVehicles(
                        onSuccess = { result ->
                            allVehicles = result
                            filteredVehicles = result
                            isLoading = false
                        },
                        onFailure = { exception ->
                            errorMessage = exception.message
                            isLoading = false
                        }
                    )
                } else {
                    getVehiclesForUser(userId,
                        onSuccess = { result ->
                            allVehicles = result
                            filteredVehicles = result
                            isLoading = false
                        },
                        onFailure = { exception ->
                            errorMessage = exception.message
                            isLoading = false
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(userId) {
        loadData()
    }

    // Arama filtreleme
    LaunchedEffect(searchQuery, allVehicles) {
        filteredVehicles = if (searchQuery.isEmpty()) {
            allVehicles
        } else {
            allVehicles.filter { 
                it.plate.contains(searchQuery, ignoreCase = true) || 
                it.ownerName.contains(searchQuery, ignoreCase = true) ||
                it.block.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    if (isScanning) {
        OCRScreen(onPlateDetected = { detectedPlate ->
            searchQuery = detectedPlate
            isScanning = false
        })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kayıtlı Araçlar", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Plaka, Sahibi veya Blok Ara...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            shape = MaterialTheme.shapes.medium
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(
                            onClick = { isScanning = true },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Kamerayla Sorgula",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (!errorMessage.isNullOrEmpty()) {
                        Text("Hata: $errorMessage", color = MaterialTheme.colorScheme.error)
                    } else {
                        if (filteredVehicles.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Kayıt bulunamadı.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredVehicles) { vehicle ->
                                    VehicleItem(vehicle, onEditClick = { editingVehicle = vehicle })
                                }
                            }
                        }
                    }
                }
            }

            // Düzenleme Dialogu
            editingVehicle?.let { vehicle ->
                EditVehicleDialog(
                    vehicle = vehicle,
                    onDismiss = { editingVehicle = null },
                    onConfirm = { updatedVehicle ->
                        updateVehicleInFirestore(
                            vehicleId = vehicle.id,
                            vehicle = updatedVehicle,
                            onSuccess = {
                                Toast.makeText(context, "Güncellendi ✅", Toast.LENGTH_SHORT).show()
                                editingVehicle = null
                                loadData() // Listeyi yenile
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Hata: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun VehicleItem(vehicle: Vehicle, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = vehicle.plate,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.secondary)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            
            Text(
                text = "Araç Sahibi: ${vehicle.ownerName}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Blok: ${vehicle.block}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Daire: ${vehicle.apartment}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Kaydı Yapan: ${vehicle.recordedBy}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun EditVehicleDialog(
    vehicle: Vehicle,
    onDismiss: () -> Unit,
    onConfirm: (Vehicle) -> Unit
) {
    var plate by remember { mutableStateOf(vehicle.plate) }
    var ownerName by remember { mutableStateOf(vehicle.ownerName) }
    var block by remember { mutableStateOf(vehicle.block) }
    var apartment by remember { mutableStateOf(vehicle.apartment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kaydı Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = plate, onValueChange = { plate = it.uppercase() }, label = { Text("Plaka") })
                OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("Araç Sahibi") })
                OutlinedTextField(value = block, onValueChange = { block = it }, label = { Text("Blok") })
                OutlinedTextField(value = apartment, onValueChange = { apartment = it }, label = { Text("Daire") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (plate.isNotBlank()) {
                    onConfirm(vehicle.copy(plate = plate, ownerName = ownerName, block = block, apartment = apartment))
                }
            }) {
                Text("Güncelle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
