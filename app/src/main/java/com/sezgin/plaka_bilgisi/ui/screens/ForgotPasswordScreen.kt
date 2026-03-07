package com.sezgin.plaka_bilgisi.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Şifre Sıfırlama", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = { Text("Kayıtlı E-posta Adresi") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            message?.let {
                Text(
                    text = it,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        // Master Admin hesabının şifresinin sıfırlanmasını engelliyoruz
                        if (email.lowercase() == "admin@adminmax.com") {
                            message = "Bu hesabın şifresi değiştirilemez!"
                            isError = true
                        } else {
                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        message = "Şifre sıfırlama maili gönderildi!"
                                        isError = false
                                        // Opsiyonel: navController.navigate("resetPassword")
                                    } else {
                                        message = task.exception?.localizedMessage ?: "Hata oluştu."
                                        isError = true
                                    }
                                }
                        }
                    } else {
                        message = "Lütfen e-posta adresini gir."
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mail Gönder")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text("Geri Dön")
            }
        }
    }
}
