package com.example.vehiclemanager.core.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagerAppBar(
    title: String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onToggleTheme: (() -> Unit)? = null,
    navigationIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    navigationContentDescription: String? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationContentDescription,
                    )
                }
            }
        },
        actions = {
            if (onToggleTheme != null) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Cambiar a tema claro" else "Cambiar a tema oscuro",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            actions()
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
