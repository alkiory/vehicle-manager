package com.example.vehiclemanager.core.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Blue10 = Color(0xFF001B3F)
private val Blue20 = Color(0xFF003062)
private val Blue30 = Color(0xFF00478D)
private val Blue40 = Color(0xFF1A5EAA)
private val Blue80 = Color(0xFFA8C8FF)
private val Blue90 = Color(0xFFD5E3FF)
private val Blue95 = Color(0xFFEBF1FF)

private val Amber10 = Color(0xFF261900)
private val Amber20 = Color(0xFF402D00)
private val Amber30 = Color(0xFF5C4200)
private val Amber40 = Color(0xFF795900)
private val Amber80 = Color(0xFFF8C947)
private val Amber90 = Color(0xFFFFE088)

val VehicleManagerLightColors = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Amber40,
    onSecondary = Color.White,
    secondaryContainer = Amber90,
    onSecondaryContainer = Amber10,
    tertiary = Color(0xFF006874),
    onTertiary = Color.White,
    background = Color(0xFFF9F9FF),
    onBackground = Color(0xFF191B20),
    surface = Color(0xFFF9F9FF),
    onSurface = Color(0xFF191B20),
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
    tertiary = Color(0xFF83D8E5),
    onTertiary = Color(0xFF00363D),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),
)
