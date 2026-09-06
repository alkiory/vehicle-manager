package com.example.vehiclemanager.core.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Application-wide icon mappings.
 *
 * EPIC-008 / TASK-001: FluentUI Color Icons Dependency & Theme Integration
 *
 * The FluentUI System Icons library (com.microsoft.design:fluent-system-icons:1.1.260)
 * is added to libs.versions.toml for Android vector drawable resources.
 *
 * Note: The KMP-based fluentui-system-icons library (io.github.niyajali:fluentui-system-icons)
 * provides Compose ImageVector icons but requires multiplatform setup that is not currently
 * configured in this project. The Material Icons Extended library is used as the primary
 * icon source for now, which provides similar icon coverage.
 *
 * For colored Fluent icons, the recommended approach is to download individual SVG/vector
 * assets from https://composables.com/icons/icon-libraries/fluentui-system-icons/color
 * and add them to res/drawable/ as needed.
 */

object AppIcons {

    // Navigation destinations
    val NavHome = Icons.Filled.Home
    val NavVehicles = Icons.Filled.DirectionsCar
    val NavFuel = Icons.Filled.LocalGasStation
    val NavMaintenance = Icons.Filled.Build
    val NavSettings = Icons.Filled.Settings

    // Quick actions / FAB
    val ActionPlus = Icons.Filled.Add
    val ActionClose = Icons.Filled.Close
    val ActionFuel = Icons.Filled.LocalGasStation
    val ActionWrench = Icons.Filled.Build
    val ActionChart = Icons.Filled.TrendingUp

    // Feature icons
    val IconFuelDrop = Icons.Filled.LocalGasStation
    val IconWrench = Icons.Filled.Build
    val IconTrending = Icons.Filled.TrendingUp
    val IconVehicleBadge = Icons.Filled.DirectionsCar

    /**
     * Primary tint color used for single-tone icons.
     */
    val DefaultIconTint = Color.Black

    /**
     * Compose wrapper that applies tint to an icon.
     */
    @Composable
    fun Icon(
        icon: ImageVector,
        tint: Color = DefaultIconTint,
        contentDescription: String? = null,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

/**
 * Typed icon references for navigation.
 */
enum class NavIcon(val vector: ImageVector) {
    Home(Icons.Filled.Home),
    Vehicles(Icons.Filled.DirectionsCar),
    Fuel(Icons.Filled.LocalGasStation),
    Maintenance(Icons.Filled.Build),
    Settings(Icons.Filled.Settings),
}

/**
 * Typed quick-action icons for FAB and action sheets.
 */
enum class ActionIcon(val vector: ImageVector) {
    Plus(Icons.Filled.Add),
    Fuel(Icons.Filled.LocalGasStation),
    Wrench(Icons.Filled.Build),
    Chart(Icons.Filled.TrendingUp),
}

