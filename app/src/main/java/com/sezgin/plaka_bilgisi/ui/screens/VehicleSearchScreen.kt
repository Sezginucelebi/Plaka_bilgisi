package com.sezgin.plaka_bilgisi.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
        OCRScreen(
            onPlateDetected = { detectedPlate ->
                searchQuery = detectedPlate
                isScanning = false
            },
            onBack = { isScanning = false }
        )
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
                        
                        FilledIconButton(
                            onClick = { isScanning = true },
                            modifier = Modifier.size(56.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Kamerayla Sorgula",
                                tint = Color.White,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                    Surface(color = Color.White, shape = RoundedCornerShape(7.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                            Surface(color = Color(0xFF123C9E), shape = RoundedCornerShape(4.dp)) {
                                Text("TR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp))
                            }
                            Text(text = vehicle.plate, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp), color = Color.Black, modifier = Modifier.padding(horizontal = 9.dp))
                        }
                    }
                }
                
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Düzenle", tint = MaterialTheme.colorScheme.secondary)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            
            Text(
                text = vehicle.ownerName.ifBlank { "Araç sahibi belirtilmemiş" },
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Blok / Daire: ${vehicle.block.ifBlank { "-" }} / ${vehicle.apartment.ifBlank { "-" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Kat: ${vehicle.floor.ifBlank { "-" }}",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandInputField(
    brand: String,
    onBrandChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val sharedPrefs = remember { context.getSharedPreferences("vehicle_brands_prefs", Context.MODE_PRIVATE) }
    
    val savedBrands = remember {
        val defaultBrands = setOf("Mercedes", "BMW", "Audi", "Volkswagen", "Renault", "Toyota", "Ford", "Fiat", "Honda", "Hyundai", "Togg", "Skoda", "Nissan", "Peugeot")
        val stored = sharedPrefs.getStringSet("brands", null) ?: defaultBrands
        stored.toMutableStateList()
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = brand,
            onValueChange = { 
                onBrandChange(it)
                expanded = true
            },
            label = { Text("Marka") },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true
        )

        val filteredBrands = savedBrands.filter { it.contains(brand, ignoreCase = true) }
        if (filteredBrands.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                filteredBrands.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            onBrandChange(suggestion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun saveBrandToPreferences(context: Context, newBrand: String) {
    if (newBrand.isBlank()) return
    val sharedPrefs = context.getSharedPreferences("vehicle_brands_prefs", Context.MODE_PRIVATE)
    val currentBrands = sharedPrefs.getStringSet("brands", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    if (!currentBrands.contains(newBrand)) {
        currentBrands.add(newBrand.trim())
        sharedPrefs.edit().putStringSet("brands", currentBrands).apply()
    }
}

@Composable
fun EditVehicleDialog(
    vehicle: Vehicle,
    onDismiss: () -> Unit,
    onConfirm: (Vehicle) -> Unit
) {
    val context = LocalContext.current
    var plate by remember { mutableStateOf(vehicle.plate) }
    var brand by remember { mutableStateOf(vehicle.brand) }
    var ownerName by remember { mutableStateOf(vehicle.ownerName) }
    var block by remember { mutableStateOf(vehicle.block) }
    var apartment by remember { mutableStateOf(vehicle.apartment) }
    var floor by remember { mutableStateOf(vehicle.floor) }
    var phone by remember { mutableStateOf(vehicle.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kaydı Düzenle") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase() },
                    label = { Text("Plaka") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Marka - tek satır, plakanın altında
                BrandInputField(
                    brand = brand,
                    onBrandChange = { brand = it },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Araç Sahibi") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Blok ve Daire (2 sütun)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = block,
                        onValueChange = { block = it },
                        label = { Text("Blok") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = apartment,
                        onValueChange = { apartment = it },
                        label = { Text("Daire No") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Kat ve Dahili (2 sütun)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = floor,
                        onValueChange = { floor = it },
                        label = { Text("Kat") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Dahili") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (plate.isNotBlank()) {
                    if (brand.isNotBlank()) {
                        saveBrandToPreferences(context, brand)
                    }
                    onConfirm(
                        vehicle.copy(
                            plate = plate,
                            brand = brand,
                            ownerName = ownerName,
                            block = block,
                            apartment = apartment,
                            floor = floor,
                            phone = phone,
                            model = ""
                        )
                    )
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
