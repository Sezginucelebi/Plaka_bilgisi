package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PairingScreen(onPairingSuccess: () -> Unit) {
    val context = LocalContext.current
    var pairingCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Cihaz Eşleşmesi Gerekli", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Lütfen yöneticinizin ekranındaki QR kodu okutun veya eşleşme kodunu girin.", fontSize = 14.sp)
        
        Spacer(Modifier.height(32.dp))
        
        OutlinedTextField(
            value = pairingCode,
            onValueChange = { pairingCode = it },
            label = { Text("Eşleşme Kodu (Token)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (pairingCode.isBlank()) return@Button
                isLoading = true
                
                val db = FirebaseFirestore.getInstance()
                val androidId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
                
                // 🚀 QR TOKEN KONTROLÜ
                db.collection("pairings").document(pairingCode.trim())
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists() && doc.getBoolean("isUsed") == false) {
                            val firmId = doc.getString("firmId") ?: ""
                            
                            // Cihazı bu firmaya kaydet
                            val terminalData = hashMapOf(
                                "deviceId" to androidId,
                                "ownerId" to firmId,
                                "registerDate" to com.google.firebase.Timestamp.now()
                            )
                            
                            db.collection("terminals").add(terminalData)
                                .addOnSuccessListener {
                                    // Token'ı kullanıldı yap
                                    db.collection("pairings").document(pairingCode.trim()).update("isUsed", true)
                                    Toast.makeText(context, "Cihaz Başarıyla Eşleşti", Toast.LENGTH_SHORT).show()
                                    onPairingSuccess()
                                }
                        } else {
                            Toast.makeText(context, "Geçersiz veya Kullanılmış Kod", Toast.LENGTH_LONG).show()
                        }
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                        Toast.makeText(context, "Bağlantı Hatası", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            else Text("Cihazı Eşleştir")
        }
    }
}
