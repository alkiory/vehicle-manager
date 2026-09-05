package com.example.vehiclemanager.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Azul — primario (combustible, CTA, FAB, selección de navegación)
private val Blue10 = Color(0xFF0E1C3F)
private val Blue20 = Color(0xFF13285C)
private val Blue30 = Color(0xFF1F3A7A)
private val Blue40 = Color(0xFF315BBD)
private val Blue80 = Color(0xFFAEC1EA)
private val Blue90 = Color(0xFFE5EDFF)

// Naranja — secundario (mantenimiento)
private val Amber10 = Color(0xFF3A1E09)
private val Amber20 = Color(0xFF5C2F0E)
private val Amber30 = Color(0xFF8A4B1D)
private val Amber40 = Color(0xFFE38B48)
private val Amber80 = Color(0xFFF0B27E)
private val Amber90 = Color(0xFFFFF0E3)

// Verde — terciario (coste por kilómetro / eficiencia)
private val Green10 = Color(0xFF0F3D2C)
private val Green20 = Color(0xFF1C5440)
private val Green30 = Color(0xFF297A5C)
private val Green40 = Color(0xFF31936F)
private val Green80 = Color(0xFFAFDFCD)
private val Green90 = Color(0xFFE4F6ED)

// Degradado de la tarjeta "hero" del vehículo activo (no forma parte del ColorScheme estándar)
val HeroGradientStart = Color(0xFF2B4FA0)
val HeroGradientEnd = Color(0xFF4675DA)

val VehicleManagerLightColors = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Amber40,
    onSecondary = Color.White,
    secondaryContainer = Amber90,
    onSecondaryContainer = Amber10,
    tertiary = Green40,
    onTertiary = Color.White,
    tertiaryContainer = Green90,
    onTertiaryContainer = Green10,
    background = Color(0xFFE6EAF3),
    onBackground = Color(0xFF191B20),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191B20),
    surfaceVariant = Color(0xFFEEF1F8),
    onSurfaceVariant = Color(0xFF44474F),
)

val VehicleManagerDarkColors = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = Amber80,
    onSecondary = Amber20,
    secondaryContainer = Amber30,
    onSecondaryContainer = Amber90,
    tertiary = Green80,
    onTertiary = Green20,
    tertiaryContainer = Green30,
    onTertiaryContainer = Green90,
    background = Color(0xFF0A1120),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF1A2437),
    onSurfaceVariant = Color(0xFFC4C6D0),
)