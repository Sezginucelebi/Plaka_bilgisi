package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.sezgin.plaka_bilgisi.model.Vehicle
import com.sezgin.plaka_bilgisi.repository.addVehicleToFirestore
import com.sezgin.plaka_bilgisi.repository.getUserName
import com.sezgin.plaka_bilgisi.ui.theme.Plaka_kontrol_vol_2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRegisterScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current
    val currentUser = remember { if (isInPreview) null else try { FirebaseAuth.getInstance().currentUser } catch (_: Exception) { null } }

    var plate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var internalNo by remember { mutableStateOf("") }
    
    var recorderName by remember { mutableStateOf(if (isInPreview) "Test Kullanıcı" else "Yükleniyor...") }
    var isScanning by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (!isInPreview) {
            currentUser?.let { user ->
                getUserName(user.uid) { name ->
                    recorderName = name
                }
            }
        }
    }

    if (isScanning) {
        OCRScreen(
            onPlateDetected = { detectedPlate ->
                plate = detectedPlate
                isScanning = false
            },
            onBack = { isScanning = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Manuel Kayıt", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Lütfen araç ve iletişim bilgilerini eksiksiz doldurunuz.", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1117))
                )
            },
            containerColor = Color(0xFF010409)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Üst Plaka Önizleme Rozeti
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(2.dp, Color.Black)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Surface(
                                    color = Color(0xFF003399),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "TR",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (plate.isBlank()) "34 ABC 123" else plate,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp,
                                    color = if (plate.isBlank()) Color.LightGray else Color.Black
                                )
                            }
                        }
                    }
                }

                // 🚗 ARAÇ BİLGİLERİ KARTI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFF2F81F7),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ARAÇ BİLGİLERİ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        HorizontalDivider(color = Color(0xFF21262D), thickness = 1.dp)

                        // Araç Plakası
                        OutlinedTextField(
                            value = plate,
                            onValueChange = { plate = it.uppercase().trim() },
                            label = { Text("Araç Plakası") },
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.Gray) },
                            trailingIcon = {
                                IconButton(onClick = { isScanning = true }) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Tara", tint = Color(0xFF2F81F7))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Marka - Tek satır, Plakanın altında
                        BrandInputField(
                            brand = brand,
                            onBrandChange = { brand = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 📍 ADRES & İLETİŞİM KARTI
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF2F81F7),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ADRES & İLETİŞİM",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        HorizontalDivider(color = Color(0xFF21262D), thickness = 1.dp)

                        // Araç Sahibi
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Araç Sahibi") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Telefon
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Telefon") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) },
                            trailingIcon = {
                                Icon(Icons.Default.AccountBox, contentDescription = "Kişiler", tint = Color(0xFF2F81F7))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Satır 1: Blok | Daire No (2 Sütun)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = block,
                                onValueChange = { block = it.trim() },
                                label = { Text("Blok") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = apartment,
                                onValueChange = { apartment = it.trim() },
                                label = { Text("Daire No") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Satır 2: Kat | Dahili (2 Sütun)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = floor,
                                onValueChange = { floor = it.trim() },
                                label = { Text("Kat") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = internalNo,
                                onValueChange = { internalNo = it.trim() },
                                label = { Text("Dahili") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Buton 1: KAYDI TAMAMLA VE KAYDET
                Button(
                    onClick = {
                        val userId = currentUser?.uid ?: ""
                        val vehicle = Vehicle(
                            plate = plate,
                            ownerName = ownerName.trim(),
                            block = block,
                            apartment = apartment,
                            floor = floor,
                            phone = if (phone.isNotBlank()) phone else internalNo,
                            brand = brand,
                            model = "",
                            ownerId = userId,
                            recordedBy = recorderName
                        )

                        if (plate.isBlank()) {
                            Toast.makeText(context, "Lütfen en azından plakayı girin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (brand.isNotBlank()) {
                            saveBrandToPreferences(context, brand)
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
                                internalNo = ""
                                brand = ""
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Hata: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KAYDI TAMAMLA VE KAYDET", fontWeight = FontWeight.Bold)
                }

                // Buton 2: Kameraya Dön / Plaka Tara
                OutlinedButton(
                    onClick = { isScanning = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF30363D))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kameraya Dön / Plaka Tara", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Buton 3: Vazgeç ve Geri Dön
                TextButton(onClick = onBackClick) {
                    Text("Vazgeç ve Geri Dön", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleRegisterScreenPreview() {
    Plaka_kontrol_vol_2Theme {
        VehicleRegisterScreen(onBackClick = {})
    }
}
