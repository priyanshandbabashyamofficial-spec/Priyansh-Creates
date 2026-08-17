package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = VioletLight,
  onPrimary = DeepViolet,
  primaryContainer = DeepViolet,
  onPrimaryContainer = Color(0xFFEDE9FE),
  secondary = GoldLight,
  onSecondary = GoldDark,
  secondaryContainer = Color(0xFF78350F),
  onSecondaryContainer = Color(0xFFFEF3C7),
  tertiary = CyanLight,
  onTertiary = Color(0xFF164E63),
  tertiaryContainer = Color(0xFF155E75),
  onTertiaryContainer = Color(0xFFCFFAFE),
  background = DarkBackground,
  onBackground = Color(0xFFF1F5F9),
  surface = DarkSurface,
  onSurface = Color(0xFFF1F5F9),
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = Color(0xFF475569),
  outlineVariant = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
  primary = ElectricViolet,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEDE9FE),
  onPrimaryContainer = DeepViolet,
  secondary = GodGold,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFEF3C7),
  onSecondaryContainer = GoldDark,
  tertiary = CyanAccent,
  onTertiary = Color.White,
  tertiaryContainer = Color(0xFFCFFAFE),
  onTertiaryContainer = Color(0xFF164E63),
  background = LightBackground,
  onBackground = Color(0xFF0F172A),
  surface = LightSurface,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1),
  outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep distinctive theme identity
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
