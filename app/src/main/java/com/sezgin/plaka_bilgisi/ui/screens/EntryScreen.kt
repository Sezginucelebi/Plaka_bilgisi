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

            // 🔹 Başlık
            Text(
                text = "",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 🔹 Alt başlık
            Text(
                text = "Giriş türünüzü seçin",
                style = MaterialTheme.typography.bodyLarge,
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
                    text = "Bireysel Giriş",
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
                    text = "Kurumsal Giriş",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 Bilgilendirme metni
            Text(
                text = "Kurumsal hesaplar ek güvenlik (SMS + Şifre) ile korunur.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 16.sp
            )
        }
    }
}
