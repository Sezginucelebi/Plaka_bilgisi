package com.sezgin.plaka_bilgisi.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // ⏳ Gecikmeli yönlendirme (2 saniye sonra giriş seçimine gider)
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("entry") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // 🎨 Arayüz (Logo kaldırıldı, sadece boş bir ekran)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Logo buradaydı, kaldırıldı.
    }
}
