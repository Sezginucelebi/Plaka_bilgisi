import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import com.google.cloud.Timestamp
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.NetworkInterface
import java.net.URL
import java.util.*
import kotlin.random.Random

const val FIREBASE_API_KEY = "AIzaSyAWh-HsV4kR5gyAHR6Jy3xrFXG2XG_Gto0"

data class Vehicle(
    val id: String = "",
    val plate: String = "",
    val ownerName: String = "",
    val block: String = "",
    val apartment: String = "",
    val floor: String = "",
    val phone: String = "",
    val brand: String = "",
    val model: String = "",
    val recordedBy: String = "",
    val ownerId: String = ""
)

data class UserSession(
    val email: String, 
    val userId: String, 
    val isAdmin: Boolean, 
    val isPremium: Boolean,
    val deviceLimit: Int = 1,
    val enabledFields: List<String> = listOf("plate", "ownerName"),
    val parentId: String? = null,
    val role: String = "user",
    var needsPasswordChange: Boolean = false
)

var isFirebaseInitialized = false

fun getHardwareId(): String {
    return try {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        val sb = StringBuilder()
        while (networkInterfaces.hasMoreElements()) {
            val ni = networkInterfaces.nextElement()
            val hardwareAddress = ni.hardwareAddress
            if (hardwareAddress != null) {
                for (b in hardwareAddress) {
                    sb.append(String.format("%02X", b))
                }
                return sb.toString()
            }
        }
        "UNKNOWN_ID"
    } catch (e: Exception) { "ERROR_ID" }
}

fun main() {
    val possiblePaths = listOf("serviceAccountKey.json", "../serviceAccountKey.json")
    var keyFile: File? = null
    for (path in possiblePaths) {
        val file = File(path)
        if (file.exists()) { keyFile = file; break }
    }
    if (keyFile != null) {
        try {
            val serviceAccount = FileInputStream(keyFile)
            val options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build()
            if (FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(options)
            isFirebaseInitialized = true
        } catch (e: Exception) { println("Firebase hatası: ${e.message}") }
    }
    application {
        val windowState = rememberWindowState(size = DpSize(1200.dp, 800.dp))
        Window(onCloseRequest = ::exitApplication, title = "Plaka Sorgulama", state = windowState) {
            MaterialTheme {
                if (!isFirebaseInitialized) FirebaseErrorScreen() else TerminalAuthFlow()
            }
        }
    }
}

@Composable
fun TerminalAuthFlow() {
    val hardwareId = remember { getHardwareId() }
    var authUser by remember { mutableStateOf<UserSession?>(null) }
    
    if (authUser == null) {
        LoginView(hardwareId) { session -> authUser = session }
    } else if (authUser!!.needsPasswordChange) {
        PasswordChangeScreen(authUser!!) { authUser = null }
    } else {
        if (authUser!!.isAdmin) AdminTerminalScreen(authUser!!) { authUser = null } 
        else MainScreen(authUser!!) { authUser = null }
    }
}

@Composable
fun LoginView(hardwareId: String, onLoginSuccess: (UserSession) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.width(400.dp).padding(64.dp).align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Terminal Girişi", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, 
                    onValueChange = { password = it }, 
                    label = { Text("Şifre") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    }
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showForgotDialog = true }) {
                        Text("Şifremi Unuttum", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isLoading) CircularProgressIndicator() else {
                    Button(onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            verifyLogin(email, password, hardwareId) { session, error ->
                                isLoading = false
                                if (session != null) onLoginSuccess(session) else errorMessage = error
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Giriş Yap") }
                }
                errorMessage?.let { Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 16.dp)) }
            }
            Text("ss yazılım", modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), fontSize = 12.sp, color = Color.Red)
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(onDismiss = { showForgotDialog = false })
    }
}

@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Dialog(onCloseRequest = onDismiss, state = rememberDialogState(size = DpSize(400.dp, 300.dp))) {
        Surface(modifier = Modifier.fillMaxSize().padding(24.dp), color = Color.White) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Şifre Sıfırlama", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("E-posta adresinizi girin, size bir sıfırlama bağlantısı gönderelim.", fontSize = 14.sp)
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth())
                
                message?.let {
                    Text(it, color = if (isSuccess) Color(0xFF4CAF50) else Color.Red, fontSize = 12.sp)
                }
                
                Spacer(Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Kapat") }
                    Button(onClick = {
                        if (email.isNotBlank()) {
                            sendPasswordResetEmail(email) { success, msg ->
                                isSuccess = success
                                message = msg
                            }
                        }
                    }) { Text("Gönder") }
                }
            }
        }
    }
}

fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
    Thread {
        try {
            val url = URL("https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=$FIREBASE_API_KEY")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            val body = "{\"requestType\":\"PASSWORD_RESET\",\"email\":\"$email\"}"
            conn.outputStream.write(body.toByteArray())
            if (conn.responseCode == 200) {
                onResult(true, "Sıfırlama maili gönderildi. Lütfen kutunuzu kontrol edin.")
            } else {
                onResult(false, "E-posta bulunamadı veya bir hata oluştu.")
            }
        } catch (e: Exception) {
            onResult(false, "Bağlantı hatası: ${e.message}")
        }
    }.start()
}

fun verifyLogin(email: String, pass: String, hwId: String, onResult: (UserSession?, String?) -> Unit) {
    Thread {
        try {
            val url = URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$FIREBASE_API_KEY")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            val body = "{\"email\":\"$email\",\"password\":\"$pass\",\"returnSecureToken\":true}"
            conn.outputStream.write(body.toByteArray())
            if (conn.responseCode != 200) { onResult(null, "E-posta veya şifre hatalı."); return@Thread }

            val db = FirestoreClient.getFirestore()
            val userRecord = FirebaseAuth.getInstance().getUserByEmail(email)
            val userDoc = db.collection("users").document(userRecord.uid).get().get()
            
            val isAdmin = email == "admin@adminmax.com"
            val isPremium = userDoc.getBoolean("isPremium") ?: false
            val deviceLimit = userDoc.getLong("deviceLimit")?.toInt() ?: 1
            val enabledFields = (userDoc.get("enabledFields") as? List<*>)?.map { it.toString() } ?: listOf("plate", "ownerName")
            val needsPass = userDoc.getBoolean("needsPasswordChange") ?: false
            val parentId = userDoc.getString("parentId")
            val role = userDoc.getString("role") ?: "user"

            if (isAdmin) {
                onResult(UserSession(email, userRecord.uid, true, true, 999, listOf("plate", "ownerName", "block", "apartment", "floor", "phone", "brand", "model"), null, "admin", false), null)
                return@Thread
            }

            val checkId = parentId ?: userRecord.uid
            val terminals = db.collection("terminals").whereEqualTo("ownerId", checkId).get().get()
            val registeredHwIds = terminals.documents.mapNotNull { it.getString("deviceId") }
            
            if (registeredHwIds.contains(hwId)) {
                onResult(UserSession(email, userRecord.uid, false, isPremium, deviceLimit, enabledFields, parentId, role, needsPass), null)
            } else if (terminals.size() < deviceLimit) {
                db.collection("terminals").add(mapOf("ownerId" to checkId, "deviceId" to hwId, "registerDate" to Timestamp.now())).get()
                onResult(UserSession(email, userRecord.uid, false, isPremium, deviceLimit, enabledFields, parentId, role, needsPass), null)
            } else { onResult(null, "Cihaz limiti doldu. Ücretli (Premium) hesaba geçin.") }
        } catch (e: Exception) { onResult(null, "Hata: ${e.message}") }
    }.start()
}

@Composable
fun PasswordChangeScreen(session: UserSession, onDone: () -> Unit) {
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var newPassVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(Modifier.padding(64.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Şifre Değiştirme Gerekli", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = newPass, 
                onValueChange = { newPass = it }, 
                label = { Text("Yeni Şifre") }, 
                visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { newPassVisible = !newPassVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPass, 
                onValueChange = { confirmPass = it }, 
                label = { Text("Tekrar Yeni Şifre") }, 
                visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPassVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            )
            if (error != null) Text(error!!, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                if (newPass.length < 6) error = "En az 6 karakter."
                else if (newPass != confirmPass) error = "Uyuşmuyor."
                else {
                    Thread {
                        FirebaseAuth.getInstance().updateUser(UserRecord.UpdateRequest(session.userId).setPassword(newPass))
                        FirestoreClient.getFirestore().collection("users").document(session.userId).update("needsPasswordChange", false).get()
                        onDone()
                    }.start()
                }
            }) { Text("Şifreyi Güncelle ve Yeniden Giriş Yap") }
        }
    }
}

@Composable
fun AdminTerminalScreen(session: UserSession, onLogout: () -> Unit) {
    var corporateUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var regularUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Kurumsal, 1: Normal
    var isAddingUser by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserProfile?>(null) }
    
    fun loadUsers() {
        Thread {
            val db = FirestoreClient.getFirestore()
            val allUsers = db.collection("users").get().get().documents.map {
                UserProfile(
                    it.id, 
                    it.getString("name") ?: "", 
                    it.getString("email") ?: "", 
                    it.getBoolean("isPremium") ?: false, 
                    it.getLong("deviceLimit")?.toInt() ?: 1, 
                    (it.get("enabledFields") as? List<*>)?.map { f -> f.toString() } ?: listOf("plate", "ownerName"),
                    it.getString("role") ?: "user"
                )
            }
            corporateUsers = allUsers.filter { it.role == "corporate" }
            regularUsers = allUsers.filter { it.role == "user" }
        }.start()
    }
    
    LaunchedEffect(Unit) { loadUsers() }
    
    Scaffold(
        topBar = { 
            Column {
                TopAppBar(title = { Text("Satıcı Paneli") }, actions = {
                    IconButton(onClick = { isAddingUser = true }) { Icon(Icons.Default.Add, null) }
                    IconButton(onClick = { loadUsers() }) { Icon(Icons.Default.Refresh, null) }
                    IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) }
                })
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Kurumsal Kullanıcılar", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Normal Kullanıcılar", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }, 
        bottomBar = {
            BottomAppBar(backgroundColor = Color.Transparent, elevation = 0.dp) {
                Text("ss yazılım", modifier = Modifier.fillMaxWidth().padding(8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, color = Color.Red)
            }
        }
    ) { padding ->
        val currentList = if (selectedTab == 0) corporateUsers else regularUsers
        
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            items(currentList) { user ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 4.dp) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(user.name, fontWeight = FontWeight.Bold)
                            Text(user.email, fontSize = 12.sp, color = Color.Gray)
                            Text("Limit: ${user.deviceLimit}", fontSize = 10.sp)
                        }
                        IconButton(onClick = { editingUser = user }) { Icon(Icons.Default.Edit, null, tint = Color.Blue) }
                        IconButton(onClick = { 
                            Thread {
                                try {
                                    FirebaseAuth.getInstance().deleteUser(user.uid)
                                    FirestoreClient.getFirestore().collection("users").document(user.uid).delete().get()
                                    loadUsers()
                                } catch (e: Exception) { println(e.message) }
                            }.start()
                        }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        Button(onClick = { togglePremium(user.uid, !user.isPremium, if(!user.isPremium) 10 else 1) { loadUsers() } }) {
                            Text(if(user.isPremium) "Premium İptal" else "Premium Yap")
                        }
                    }
                }
            }
        }
    }
    
    if (isAddingUser) {
        val tempPass = (100000..999999).random().toString()
        AddUserDialog("Yeni Firma", "", "", "1", listOf("plate", "ownerName"), tempPass, { isAddingUser = false }, { n, m, l, f ->
            registerCorporateUser(n, m, l, f, tempPass) { isAddingUser = false; loadUsers() }
        })
    }
    editingUser?.let { u ->
        AddUserDialog("Düzenle", u.name, u.email, u.deviceLimit.toString(), u.enabledFields, null, { editingUser = null }, { n, m, l, f ->
            updateCorporateUser(u.uid, n, l, f) { editingUser = null; loadUsers() }
        })
    }
}

@Composable
fun AddUserDialog(title: String, inN: String, inM: String, inL: String, inF: List<String>, pass: String?, onDismiss: () -> Unit, onConfirm: (String, String, Int, List<String>) -> Unit) {
    var n by remember { mutableStateOf(inN) }
    var m by remember { mutableStateOf(inM) }
    var l by remember { mutableStateOf(inL) }
    val selectedFields = remember { mutableStateListOf<String>().apply { addAll(inF) } }
    val allFields = listOf("plate" to "Plaka", "ownerName" to "Araç Sahibi", "block" to "Blok", "apartment" to "Daire", "floor" to "Kat", "phone" to "Telefon", "brand" to "Marka", "model" to "Model")

    Dialog(onCloseRequest = onDismiss, state = rememberDialogState(size = DpSize(600.dp, 600.dp))) {
        Surface(Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if(pass != null) Text("Geçici Şifre: $pass (Not edin!)", color = Color.Red, fontWeight = FontWeight.Bold)
                
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Firma Adı") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth(), enabled = pass != null)
                    OutlinedTextField(value = l, onValueChange = { l = it }, label = { Text("Limit") }, modifier = Modifier.fillMaxWidth())
                    Text("Kullanılacak Alanlar (2 Sütun):", fontWeight = FontWeight.Bold)
                    allFields.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { (k, label) ->
                                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = selectedFields.contains(k), onCheckedChange = { if(it) selectedFields.add(k) else if(k != "plate") selectedFields.remove(k) })
                                    Text(label, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("İptal") }
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = { onConfirm(n, m, l.toIntOrNull() ?: 1, selectedFields.toList()) }) { Text("Kaydet") }
                }
            }
        }
    }
}

fun registerCorporateUser(name: String, email: String, limit: Int, fields: List<String>, pass: String, onDone: () -> Unit) {
    Thread {
        try {
            val userRecord = FirebaseAuth.getInstance().createUser(UserRecord.CreateRequest().setEmail(email).setPassword(pass).setDisplayName(name))
            FirestoreClient.getFirestore().collection("users").document(userRecord.uid).set(mapOf("name" to name, "email" to email, "isPremium" to (limit > 1), "deviceLimit" to limit, "role" to "corporate", "enabledFields" to fields, "needsPasswordChange" to true)).get()
            onDone()
        } catch (e: Exception) { println(e.message) }
    }.start()
}

fun updateCorporateUser(uid: String, name: String, limit: Int, fields: List<String>, onDone: () -> Unit) {
    Thread {
        FirestoreClient.getFirestore().collection("users").document(uid).update(mapOf("name" to name, "deviceLimit" to limit, "enabledFields" to fields)).get()
        onDone()
    }.start()
}

fun deleteUser(uid: String, onDone: () -> Unit) {
    Thread {
        try { FirebaseAuth.getInstance().deleteUser(uid); FirestoreClient.getFirestore().collection("users").document(uid).delete().get(); onDone() }
        catch (e: Exception) { println(e.message) }
    }.start()
}

fun togglePremium(uid: String, status: Boolean, limit: Int, onDone: () -> Unit) {
    Thread { FirestoreClient.getFirestore().collection("users").document(uid).update(mapOf("isPremium" to status, "deviceLimit" to limit)).get(); onDone() }.start()
}

data class UserProfile(val uid: String, val name: String, val email: String, val isPremium: Boolean, val deviceLimit: Int, val enabledFields: List<String>, val role: String = "user")

@Composable
fun MainScreen(session: UserSession, onLogout: () -> Unit) {
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var personels by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isAddingVehicle by remember { mutableStateOf(false) }
    var isAddingPersonel by remember { mutableStateOf(false) }
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var currentTab by remember { mutableStateOf(0) }

    fun refreshData() {
        Thread {
            val db = FirestoreClient.getFirestore()
            val ownerId = session.parentId ?: session.userId
            vehicles = db.collection("vehicles").whereEqualTo("ownerId", ownerId).get().get().documents.map { doc ->
                Vehicle(
                    id = doc.id, 
                    plate = doc.getString("plate") ?: "", 
                    ownerName = doc.getString("ownerName") ?: "", 
                    block = doc.getString("block") ?: "", 
                    apartment = doc.getString("apartment") ?: "", 
                    floor = doc.getString("floor") ?: "", 
                    phone = doc.getString("phone") ?: "", 
                    brand = doc.getString("brand") ?: "", 
                    model = doc.getString("model") ?: "", 
                    recordedBy = doc.getString("recordedBy") ?: "", 
                    ownerId = doc.getString("ownerId") ?: ""
                )
            }
            if (session.role == "corporate") {
                personels = db.collection("users").whereEqualTo("parentId", session.userId).get().get().documents.map {
                    UserProfile(it.id, it.getString("name") ?: "", it.getString("email") ?: "", false, 0, emptyList())
                }
            }
        }.start()
    }
    LaunchedEffect(Unit) { refreshData() }
    val filteredList = vehicles.filter { it.plate.contains(searchQuery, true) || it.ownerName.contains(searchQuery, true) }

    Scaffold(topBar = {
        Column {
            val title = if (session.isAdmin) "Satıcı Paneli"
                        else if (session.role == "corporate") "Plaka Sorgulama - Kurumsal"
                        else "Plaka Sorgulama - Bireysel"
            
            TopAppBar(title = { Text(title) }, actions = {
                IconButton(onClick = { refreshData() }) { Icon(Icons.Default.Refresh, null) }
                IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) }
            })
            if (session.role == "corporate") {
                TabRow(selectedTabIndex = currentTab) {
                    Tab(selected = currentTab == 0, onClick = { currentTab = 0 }) { Text("Araç Takibi", Modifier.padding(12.dp)) }
                    Tab(selected = currentTab == 1, onClick = { currentTab = 1 }) { Text("Personel Yönetimi", Modifier.padding(12.dp)) }
                }
            }
        }
    }, bottomBar = {
        BottomAppBar(backgroundColor = Color.Transparent, elevation = 0.dp) {
            Text("ss yazılım", modifier = Modifier.fillMaxWidth().padding(8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, color = Color.Red)
        }
    }) { padding ->
        if (currentTab == 0) VehicleTab(padding, session, vehicles, searchQuery, { searchQuery = it }, { isAddingVehicle = true }, { editingVehicle = it })
        else PersonelTab(padding, personels, { isAddingPersonel = true })
    }

    if (isAddingVehicle) VehicleDynamicDialog("Yeni Kayıt", "Ekle", Vehicle(), session.enabledFields, onDismiss = { isAddingVehicle = false }) { saveVehicle(it, session.parentId ?: session.userId); isAddingVehicle = false; refreshData() }
    editingVehicle?.let { v -> VehicleDynamicDialog("Düzenle", "Güncelle", v, session.enabledFields, onDismiss = { editingVehicle = null }) { updateVehicleRecord(it); editingVehicle = null; refreshData() } }
    if (isAddingPersonel) AddPersonelDialog(onDismiss = { isAddingPersonel = false }, onConfirm = { name, mail, pass -> registerPersonel(name, mail, pass, session.userId, session.enabledFields) { isAddingPersonel = false; refreshData() } })
}

@Composable
fun VehicleTab(padding: PaddingValues, session: UserSession, vehicles: List<Vehicle>, query: String, onQuery: (String) -> Unit, onAdd: () -> Unit, onEdit: (Vehicle) -> Unit) {
    val list = vehicles.filter { it.plate.contains(query, true) || it.ownerName.contains(query, true) }
    Column(Modifier.padding(padding).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = query, onValueChange = onQuery, label = { Text("Arama...") }, modifier = Modifier.weight(1f))
            IconButton(onClick = onAdd) { Icon(Icons.Default.Add, null, tint = Color.Blue) }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
            if(session.enabledFields.contains("plate")) Text("PLAKA", Modifier.weight(1f), fontWeight = FontWeight.Bold)
            if(session.enabledFields.contains("ownerName")) Text("SAHİBİ", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            if(session.enabledFields.contains("block")) Text("BLOK", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            if(session.enabledFields.contains("apartment")) Text("DAİRE", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            if(session.enabledFields.contains("floor")) Text("KAT", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            if(session.enabledFields.contains("phone")) Text("TEL", Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(48.dp))
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(list) { v ->
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if(session.enabledFields.contains("plate")) Text(v.plate, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    if(session.enabledFields.contains("ownerName")) Text(v.ownerName, Modifier.weight(1.5f))
                    if(session.enabledFields.contains("block")) Text(v.block, Modifier.weight(0.5f))
                    if(session.enabledFields.contains("apartment")) Text(v.apartment, Modifier.weight(0.5f))
                    if(session.enabledFields.contains("floor")) Text(v.floor, Modifier.weight(0.5f))
                    if(session.enabledFields.contains("phone")) Text(v.phone, Modifier.weight(1f))
                    IconButton(onClick = { onEdit(v) }) { Icon(Icons.Default.Edit, null, tint = Color.Blue) }
                }
                Divider()
            }
        }
    }
}

@Composable
fun PersonelTab(padding: PaddingValues, personels: List<UserProfile>, onAdd: () -> Unit) {
    Column(Modifier.padding(padding).padding(16.dp)) {
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) { Text("Yeni Personel Tanımla") }
        Spacer(Modifier.height(16.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            items(personels) { p ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold); Text(p.email, fontSize = 12.sp, color = Color.Gray) }
                        IconButton(onClick = { 
                            Thread {
                                try {
                                    FirebaseAuth.getInstance().deleteUser(p.uid)
                                    FirestoreClient.getFirestore().collection("users").document(p.uid).delete().get()
                                } catch (e: Exception) { println(e.message) }
                            }.start()
                        }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPersonelDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var n by remember { mutableStateOf("") }
    var m by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    Dialog(onCloseRequest = onDismiss, state = rememberDialogState(size = DpSize(400.dp, 500.dp))) {
        Surface(Modifier.fillMaxSize().padding(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Bireysel Personel Tanımla", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Ad Soyad") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = p, onValueChange = { p = it }, label = { Text("Şifre") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.weight(1f))
                Button(onClick = { onConfirm(n, m, p) }, modifier = Modifier.fillMaxWidth()) { Text("Personeli Kaydet") }
            }
        }
    }
}

fun registerPersonel(name: String, email: String, pass: String, parentId: String, fields: List<String>, onDone: () -> Unit) {
    Thread {
        try {
            val userRecord = FirebaseAuth.getInstance().createUser(UserRecord.CreateRequest().setEmail(email).setPassword(pass).setDisplayName(name))
            FirestoreClient.getFirestore().collection("users").document(userRecord.uid).set(mapOf("name" to name, "email" to email, "parentId" to parentId, "role" to "personel", "enabledFields" to fields)).get()
            onDone()
        } catch (e: Exception) { println(e.message) }
    }.start()
}

fun saveVehicle(v: Vehicle, userId: String) {
    Thread { FirestoreClient.getFirestore().collection("vehicles").add(mapOf("plate" to v.plate, "ownerName" to v.ownerName, "block" to v.block, "apartment" to v.apartment, "floor" to v.floor, "phone" to v.phone, "brand" to v.brand, "model" to v.model, "ownerId" to userId, "recordedBy" to "Terminal")).get() }.start()
}

fun updateVehicleRecord(v: Vehicle) {
    Thread { FirestoreClient.getFirestore().collection("vehicles").document(v.id).update(mapOf("plate" to v.plate, "ownerName" to v.ownerName, "block" to v.block, "apartment" to v.apartment, "floor" to v.floor, "phone" to v.phone, "brand" to v.brand, "model" to v.model)).get() }.start()
}

@Composable
fun VehicleDynamicDialog(title: String, btn: String, initial: Vehicle, enabledFields: List<String>, onDismiss: () -> Unit, onConfirm: (Vehicle) -> Unit) {
    var v by remember { mutableStateOf(initial) }
    Dialog(onCloseRequest = onDismiss, state = rememberDialogState(size = DpSize(500.dp, 600.dp))) {
        Surface(Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Column(modifier = Modifier.weight(1f).padding(vertical = 16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if(enabledFields.contains("plate")) OutlinedTextField(value = v.plate, onValueChange = { v = v.copy(plate = it.uppercase()) }, label = { Text("Plaka") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("ownerName")) OutlinedTextField(value = v.ownerName, onValueChange = { v = v.copy(ownerName = it) }, label = { Text("Araç Sahibi") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("block")) OutlinedTextField(value = v.block, onValueChange = { v = v.copy(block = it) }, label = { Text("Blok") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("apartment")) OutlinedTextField(value = v.apartment, onValueChange = { v = v.copy(apartment = it) }, label = { Text("Daire") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("floor")) OutlinedTextField(value = v.floor, onValueChange = { v = v.copy(floor = it) }, label = { Text("Kat") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("phone")) OutlinedTextField(value = v.phone, onValueChange = { v = v.copy(phone = it) }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("brand")) OutlinedTextField(value = v.brand, onValueChange = { v = v.copy(brand = it) }, label = { Text("Marka") }, modifier = Modifier.fillMaxWidth())
                    if(enabledFields.contains("model")) OutlinedTextField(value = v.model, onValueChange = { v = v.copy(model = it) }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("İptal") }
                    Spacer(Modifier.width(16.dp))
                    Button(onClick = { onConfirm(v) }) { Text(btn) }
                }
            }
        }
    }
}

@Composable
fun FirebaseErrorScreen() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Firebase Hatası!") } }

fun confirm(msg: String): Boolean = true
