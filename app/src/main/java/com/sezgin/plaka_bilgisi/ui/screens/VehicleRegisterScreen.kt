package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.sezgin.plaka_bilgisi.model.Vehicle
import com.sezgin.plaka_bilgisi.repository.addVehicleToFirestore
import com.sezgin.plaka_bilgisi.repository.getUserName

@Composable
fun VehicleRegisterScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }

    var plate by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    
    var recorderName by remember { mutableStateOf("Yükleniyor...") }
    var isScanning by remember { mutableStateOf(false) }

    // 🔥 Kullanıcı adını Firestore'dan otomatik çek
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            getUserName(user.uid) { name ->
                recorderName = name
            }
        }
    }

    if (isScanning) {
        OCRScreen(onPlateDetected = { detectedPlate ->
            plate = detectedPlate
            isScanning = false
        })
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Araç İkonu",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Yeni Araç Kaydı",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Kaydı Yapan: $recorderName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Araç Plakası
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase().trim() },
                        label = { Text("Araç Plakası") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = { isScanning = true },
                        modifier = Modifier
                            .size(56.dp)
                            .padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Kamerayla Oku",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Araç Sahibi
                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Araç Sahibi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Blok ve Daire
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = block,
                        onValueChange = { block = it.trim() },
                        label = { Text("Blok") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    OutlinedTextField(
                        value = apartment,
                        onValueChange = { apartment = it.trim() },
                        label = { Text("Daire") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Kat ve Telefon
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = floor,
                        onValueChange = { floor = it.trim() },
                        label = { Text("Kat") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it.trim() },
                        label = { Text("Telefon") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Marka ve Model
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Marka") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val userId = currentUser?.uid ?: ""
                        val vehicle = Vehicle(
                            plate = plate,
                            ownerName = ownerName.trim(),
                            block = block,
                            apartment = apartment,
                            floor = floor,
                            phone = phone,
                            brand = brand,
                            model = model,
                            ownerId = userId,
                            recordedBy = recorderName
                        )

                        if (plate.isBlank()) {
                            Toast.makeText(context, "Lütfen en azından plakayı girin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        addVehicleToFirestore(
                            vehicle,
                            onSuccess = {
                                Toast.makeText(context, "Kayıt Başarılı ✅", Toast.LENGTH_SHORT).show()
                                plate = ""
                                ownerName = ""
                                block = ""
                                apartment = ""
                                floor = ""
                                phone = ""
                                brand = ""
                                model = ""
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Hata: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("KAYDET", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBackClick) {
                    Text("Geri Dön", textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
