package com.sezgin.plaka_bilgisi.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Renklerinizi Color.kt dosyasında tanımladığınızı varsayalım
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3582F4),
    onPrimary = Color.White,
    secondary = Color(0xFF9DA7B7),
    onSecondary = Color(0xFF0C1018),
    tertiary = Color(0xFF66A7FF),
    background = Color(0xFF0C1018),
    onBackground = Color.White,
    surface = Color(0xFF181E26),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF202833),
    onSurfaceVariant = Color(0xFFB8C0CC),
    outline = Color(0xFF303946),
    error = Color(0xFFE23A3A)
)

@Composable
fun PlakaBilgisiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography, // Typography.kt dosyasının varlığını varsayar
        content = content
    )
}
    
