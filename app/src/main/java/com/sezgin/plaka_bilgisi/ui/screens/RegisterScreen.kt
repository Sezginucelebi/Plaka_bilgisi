package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Kayıt Ol", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ad Soyad") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Şifreyi Tekrar Gir") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            message?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (isLoading) return@Button
                    
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            message = "Lütfen tüm alanları doldurun."
                        }
                        password.length < 6 -> {
                            message = "Şifre en az 6 karakter olmalı."
                        }
                        password != confirmPassword -> {
                            message = "Şifreler uyuşmuyor!"
                        }
                        else -> {
                            isLoading = true
                            FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(email.trim(), password.trim())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val userId = task.result?.user?.uid
                                        if (userId != null) {
                                            val userMap = hashMapOf(
                                                "name" to name.trim(),
                                                "email" to email.trim(),
                                                "role" to "user"
                                            )
                                            FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(userId)
                                                .set(userMap)
                                                .addOnSuccessListener {
                                                    isLoading = false
                                                    Toast.makeText(context, "Kayıt Başarılı! Firestore'a eklendi.", Toast.LENGTH_LONG).show()
                                                    onRegisterSuccess()
                                                }
                                                .addOnFailureListener { e ->
                                                    isLoading = false
                                                    message = "Auth başarılı ama Firestore hatası: ${e.localizedMessage}"
                                                    Toast.makeText(context, "Hata: Veritabanına yazılamadı.", Toast.LENGTH_LONG).show()
                                                }
                                        }
                                    } else {
                                        isLoading = false
                                        message = "Auth Hatası: ${task.exception?.localizedMessage}"
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Kaydediliyor..." else "Kayıt Ol")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                Text("Zaten hesabım var")
            }
        }
    }
}
