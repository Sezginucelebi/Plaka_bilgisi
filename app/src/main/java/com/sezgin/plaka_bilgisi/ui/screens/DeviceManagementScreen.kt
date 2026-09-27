package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceManagementScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    var terminals by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadTerminals() {
        isLoading = true
        db.collection("terminals")
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { result ->
                terminals = result.documents.map { doc ->
                    val data = doc.data?.toMutableMap() ?: mutableMapOf()
                    data["docId"] = doc.id
                    data
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Cihazlar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    LaunchedEffect(Unit) { loadTerminals() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cihaz Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Kayıtlı Cihazlarınız", style = MaterialTheme.typography.titleMedium)
            Text("Limitinizde yer açmak için kullanmadığınız cihazları silebilirsiniz.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (terminals.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Kayıtlı cihaz bulunamadı.") }
            } else {
                LazyColumn {
                    items(terminals) { terminal ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Cihaz Kimliği:", fontWeight = FontWeight.Bold)
                                    Text(terminal["deviceId"].toString(), style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = {
                                    db.collection("terminals").document(terminal["docId"].toString()).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Cihaz silindi", Toast.LENGTH_SHORT).show()
                                            loadTerminals()
                                        }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
