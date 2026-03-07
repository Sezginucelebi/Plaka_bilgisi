package com.sezgin.plaka_bilgisi.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.sezgin.plaka_bilgisi.repository.checkIfAdmin

@Composable
fun DashboardScreen(
    onVehicleRegisterClick: () -> Unit,
    onVehicleSearchClick: () -> Unit,
    onAdminClick: () -> Unit,
    onProfileClick: () -> Unit, // Yeni eklenen profil parametresi
    onLogoutClick: () -> Unit
) {
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    var isAdminState by remember { mutableStateOf(false) }

    val isMasterAdmin = currentUser?.email == "admin@adminmax.com"

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (isMasterAdmin) {
                isAdminState = true
            } else {
                checkIfAdmin(user.uid) { result ->
                    isAdminState = result
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Plaka Kontrol Paneli",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Hoş geldin: ${currentUser?.email ?: "Kullanıcı"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onVehicleRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Araç Kaydı")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVehicleSearchClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Araç Sorgulama")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profil Butonu
            Button(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Profilimi Güncelle", color = MaterialTheme.colorScheme.onTertiary)
            }

            if (isAdminState) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAdminClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Yönetici Paneli (Master)")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text("Çıkış Yap", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
