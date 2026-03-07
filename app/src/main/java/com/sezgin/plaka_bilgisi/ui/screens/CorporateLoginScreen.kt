package com.sezgin.plaka_bilgisi.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sezgin.plaka_bilgisi.model.Vehicle
import com.sezgin.plaka_bilgisi.repository.*
import UpdateChecker 
import kotlinx.coroutines.launch
import com.sezgin.plaka_bilgisi.BuildConfig // Otomatik sürüm için eklendi

@Composable
fun CorporateLoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var userSession by remember { mutableStateOf<UserSessionData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Güncelleme Durumu
    var updateUrl by remember { mutableStateOf<String?>(null) }
    // BuildConfig üzerinden otomatik sürüm çekme (başına 'v' ekliyoruz ki GitHub taglarıyla eşleşsin)
    val currentVersion = "v${BuildConfig.VERSION_NAME}" 

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val checker = UpdateChecker()
                updateUrl = checker.checkForUpdates(currentVersion)
            } catch (e: Exception) {
                // Hata durumunda sessizce devam et
            }
        }

        val current = auth.currentUser
        if (current != null) {
            fetchUserSession(current.uid) { session -> userSession = session }
        }
    }

    updateUrl?.let { url ->
        AlertDialog(
            onDismissRequest = { updateUrl = null },
            title = { Text("Yeni Güncelleme Mevcut!") },
            text = { Text("Uygulamanın yeni bir sürümü bulundu. İndirip kurmak ister misiniz?") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                    updateUrl = null
                }) { Text("Şimdi İndir") }
            },
            dismissButton = {
                TextButton(onClick = { updateUrl = null }) { Text("Daha Sonra") }
            }
        )
    }

    if (userSession != null) {
        CorporateTerminalScreen(
            session = userSession!!,
            onLogout = {
                auth.signOut()
                userSession = null
            }
        )
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Kurumsal Giriş", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Şifre") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            val description = if (passwordVisible) "Şifreyi gizle" else "Şifreyi göster"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Lütfen e-posta ve şifre giriniz."
                                return@Button
                            }
                            isLoading = true
                            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                .addOnSuccessListener { result ->
                                    fetchUserSession(result.user?.uid ?: "") { session ->
                                        userSession = session
                                        isLoading = false
                                    }
                                }
                                .addOnFailureListener {
                                    errorMessage = "Giriş başarısız: ${it.message}"
                                    isLoading = false
                                }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLoading) "Giriş Yapılıyor..." else "Giriş Yap")
                    }
                    errorMessage?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 16.dp)) }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onBackClick) { Text("← Geri Dön") }
                }
                Text("ss yazılım", modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), style = MaterialTheme.typography.labelSmall, color = Color.Red)
            }
        }
    }
}

data class UserSessionData(val userId: String, val enabledFields: List<String>, val parentId: String?)

fun fetchUserSession(uid: String, onResult: (UserSessionData) -> Unit) {
    FirebaseFirestore.getInstance().collection("users").document(uid).get()
        .addOnSuccessListener { doc ->
            val fields = (doc.get("enabledFields") as? List<*>)?.map { it.toString() } ?: listOf("plate", "ownerName")
            val pId = doc.getString("parentId")
            onResult(UserSessionData(uid, fields, pId))
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorporateTerminalScreen(session: UserSessionData, onLogout: () -> Unit) {
    val context = LocalContext.current
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var isAddingVehicle by remember { mutableStateOf(false) }

    fun loadData() {
        isLoading = true
        val ownerId = session.parentId ?: session.userId
        getVehiclesForUser(ownerId,
            onSuccess = { vehicles = it; isLoading = false },
            onFailure = { Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_LONG).show(); isLoading = false }
        )
    }

    LaunchedEffect(Unit) { loadData() }

    val filteredList = vehicles.filter { it.plate.contains(searchQuery, true) || it.ownerName.contains(searchQuery, true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Takip Paneli") },
                actions = {
                    IconButton(onClick = { isAddingVehicle = true }) { Icon(Icons.Default.Add, null) }
                    IconButton(onClick = { loadData() }) { Icon(Icons.Default.Refresh, null) }
                    IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) }
                }
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.Transparent) {
                Text("ss yazılım", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = Color.Red)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, label = { Text("Arama...") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray.copy(alpha = 0.3f)).padding(8.dp)) {
                if (session.enabledFields.contains("plate")) Text("PLAKA", Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                if (session.enabledFields.contains("ownerName")) Text("SAHİBİ", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                if (session.enabledFields.contains("block")) Text("BLOK", Modifier.weight(0.8f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                if (session.enabledFields.contains("apartment")) Text("DAİRE", Modifier.weight(0.8f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            if (isLoading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredList) { vehicle ->
                        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (session.enabledFields.contains("plate")) Text(vehicle.plate, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            if (session.enabledFields.contains("ownerName")) Text(vehicle.ownerName, Modifier.weight(1.5f))
                            if (session.enabledFields.contains("block")) Text(vehicle.block, Modifier.weight(0.8f))
                            if (session.enabledFields.contains("apartment")) Text(vehicle.apartment, Modifier.weight(0.8f))
                            IconButton(onClick = { editingVehicle = vehicle }) { Icon(Icons.Default.Edit, null, tint = Color.Blue) }
                        }
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (isAddingVehicle) {
        VehicleDynamicDialog(
            title = "Yeni Kayıt",
            initialVehicle = Vehicle(ownerId = session.parentId ?: session.userId, recordedBy = "Mobil"),
            fields = session.enabledFields,
            onDismiss = { isAddingVehicle = false },
            onConfirm = {
                addVehicleToFirestore(it, { isAddingVehicle = false; loadData() }, { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() })
            }
        )
    }
    editingVehicle?.let { v -> 
        VehicleDynamicDialog(
            title = "Kaydı Düzenle", 
            initialVehicle = v, 
            fields = session.enabledFields, 
            onDismiss = { editingVehicle = null },
            onConfirm = {
                updateVehicleInFirestore(v.id, it, { editingVehicle = null; loadData() }, { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() })
            }
        )
    }
}

@Composable
fun VehicleDynamicDialog(title: String, initialVehicle: Vehicle, fields: List<String>, onDismiss: () -> Unit, onConfirm: (Vehicle) -> Unit) {
    var v by remember { mutableStateOf(initialVehicle) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (fields.contains("plate")) OutlinedTextField(value = v.plate, onValueChange = { v = v.copy(plate = it.uppercase()) }, label = { Text("Plaka") })
                if (fields.contains("ownerName")) OutlinedTextField(value = v.ownerName, onValueChange = { v = v.copy(ownerName = it) }, label = { Text("Araç Sahibi") })
                if (fields.contains("block")) OutlinedTextField(value = v.block, onValueChange = { v = v.copy(block = it) }, label = { Text("Blok") })
                if (fields.contains("apartment")) OutlinedTextField(value = v.apartment, onValueChange = { v = v.copy(apartment = it) }, label = { Text("Daire") })
                if (fields.contains("floor")) OutlinedTextField(value = v.floor, onValueChange = { v = v.copy(floor = it) }, label = { Text("Kat") })
                if (fields.contains("phone")) OutlinedTextField(value = v.phone, onValueChange = { v = v.copy(phone = it) }, label = { Text("Telefon") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(v) }) { Text("Kaydet") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
