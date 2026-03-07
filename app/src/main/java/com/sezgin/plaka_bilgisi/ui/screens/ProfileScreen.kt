package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: ""
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Bilgileri", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Ad Soyad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { },
                        label = { Text("E-posta") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "Lütfen adınızı girin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            currentUser?.let { user ->
                                val userUpdate = mapOf("name" to name)
                                db.collection("users").document(user.uid)
                                    .update(userUpdate)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Profil güncellendi ✅", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        // Eğer doküman yoksa set kullanalım
                                        db.collection("users").document(user.uid)
                                            .set(mapOf("name" to name, "email" to user.email, "role" to "user"))
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Profil oluşturuldu ✅", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Hata: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Güncelle")
                    }
                }
            }
        }
    }
}
