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
fun FirmManagementScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var firms by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadFirms() {
        isLoading = true
        getAllUsers(
            onSuccess = { allUsers ->
                // Sadece Kurumsal Firmaları Filtrele
                firms = allUsers.filter { it.role == "corporate" || it.role == "admin" }
                isLoading = false 
            },
            onFailure = { 
                Toast.makeText(context, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                isLoading = false 
            }
        )
    }

    LaunchedEffect(Unit) { loadFirms() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kayıtlı Firmalar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { loadFirms() }) {
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
                if (firms.isEmpty()) {
                    Text("Kayıtlı firma bulunamadı.", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        items(firms) { firm ->
                            UserItem(
                                user = firm,
                                onRoleChange = { newRole ->
                                    updateUserRole(firm.uid, newRole, 
                                        onSuccess = { loadFirms() },
                                        onFailure = { Toast.makeText(context, "Hata", Toast.LENGTH_SHORT).show() }
                                    )
                                },
                                onPremiumToggle = { isPremium ->
                                    updatePremiumStatus(firm.uid, isPremium,
                                        onSuccess = { loadFirms() },
                                        onFailure = { Toast.makeText(context, "Hata", Toast.LENGTH_SHORT).show() }
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
