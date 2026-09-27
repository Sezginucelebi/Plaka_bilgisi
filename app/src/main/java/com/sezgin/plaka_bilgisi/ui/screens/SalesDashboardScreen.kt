package com.sezgin.plaka_bilgisi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesDashboardScreen(
    onUserManagementClick: () -> Unit,
    onVehicleSearchClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sistem Yönetim Paneli (Sales)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hoş Geldiniz (Sistem Admin)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentUser?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onUserManagementClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Firmaları ve Kullanıcıları Yönet")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVehicleSearchClick,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Tüm Plaka Kayıtlarını Sorgula")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("Güvenli Çıkış")
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Not: Detaylı analizler için Python Dashboard'u kullanabilirsiniz.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
