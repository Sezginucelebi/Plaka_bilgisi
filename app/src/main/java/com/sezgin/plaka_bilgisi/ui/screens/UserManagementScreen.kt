package com.sezgin.plaka_bilgisi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sezgin.plaka_bilgisi.repository.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var users by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadUsers() {
        isLoading = true
        getAllUsers(
            onSuccess = { 
                users = it
                isLoading = false 
            },
            onFailure = { 
                Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                isLoading = false 
            }
        )
    }

    LaunchedEffect(Unit) { loadUsers() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kullanıcı Yönetimi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { loadUsers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(users) { user ->
                        UserItem(
                            user = user,
                            onRoleChange = { newRole ->
                                updateUserRole(user.uid, newRole, 
                                    onSuccess = {
                                        Toast.makeText(context, "Yetki güncellendi", Toast.LENGTH_SHORT).show()
                                        loadUsers()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onPremiumToggle = { isPremium ->
                                updatePremiumStatus(user.uid, isPremium,
                                    onSuccess = {
                                        Toast.makeText(context, "Premium durum güncellendi", Toast.LENGTH_SHORT).show()
                                        loadUsers()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: UserProfile, 
    onRoleChange: (String) -> Unit,
    onPremiumToggle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = user.email, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Mevcut Rol: ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = user.role.uppercase(), 
                    style = MaterialTheme.typography.bodyMedium, 
                    fontWeight = FontWeight.Bold,
                    color = if (user.role == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Premium: ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (user.isPremium) "AKTİF" else "PASİF", 
                    style = MaterialTheme.typography.bodyMedium, 
                    fontWeight = FontWeight.Bold,
                    color = if (user.isPremium) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Rol Değiştirme Butonları
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onRoleChange("admin") },
                    enabled = user.role != "admin"
                ) {
                    Text("Admin Yap")
                }
                OutlinedButton(
                    onClick = { onRoleChange("user") },
                    enabled = user.role != "user"
                ) {
                    Text("Kullanıcı Yap")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Premium Yönetim Butonu
            Button(
                onClick = { onPremiumToggle(!user.isPremium) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (user.isPremium) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (user.isPremium) "Premium İptal Et" else "Premium Yap (Sınırsız Terminal)")
            }
        }
    }
}
