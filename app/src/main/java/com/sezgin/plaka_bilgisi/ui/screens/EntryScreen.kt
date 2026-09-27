package com.sezgin.plaka_bilgisi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sezgin.plaka_bilgisi.R

@Composable
fun EntryScreen(
    onPersonalClick: () -> Unit,
    onCorporateClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔹 Logo veya uygulama görseli
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_logo),
                contentDescription = "Uygulama Logosu",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
            )

            Text(
                text = "AuroNova",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 🔹 Alt başlık
            Text(
                text = "PTS TERMINAL SİSTEMİ",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF66A7FF), fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // 🔹 Bireysel Giriş Butonu
            Button(
                onClick = onPersonalClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "PERSONEL GİRİŞİ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Kurumsal Giriş Butonu
            OutlinedButton(
                onClick = onCorporateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
            ) {
                Text(
                    text = "KURUMSAL GİRİŞ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 Bilgilendirme metni
            Text(
                text = "Giriş türünüzü seçerek devam edin.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 16.sp
            )
        }
    }
}
