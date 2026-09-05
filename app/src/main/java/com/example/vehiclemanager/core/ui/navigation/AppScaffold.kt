package com.example.vehiclemanager.core.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.vehiclemanager.feature.dashboard.DashboardScreen
import com.example.vehiclemanager.feature.fuel.FuelScreen
import com.example.vehiclemanager.feature.maintenance.MaintenanceScreen
import com.example.vehiclemanager.feature.statistics.StatisticsScreen
import com.example.vehiclemanager.feature.vehicles.VehiclesScreen

private const val TABLET_BREAKPOINT_DP = 600

private data class PrimaryDestination(
    val label: String,
    val shortLabel: String,
    val navigate: (NavHostController) -> Unit,
    val isSelected: (NavDestination?) -> Boolean,
)

private fun NavHostController.navigateTo(destination: Any) {
    when (destination) {
        DashboardRoute -> navigate(DashboardRoute) {
            popUpTo(graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }

        VehiclesRoute -> navigate(VehiclesRoute) {
            popUpTo(graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }

        FuelRoute -> navigate(FuelRoute) {
            popUpTo(graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }

        MaintenanceRoute -> navigate(MaintenanceRoute) {
            popUpTo(graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }

        StatsRoute -> navigate(StatsRoute) {
            popUpTo(graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

private val primaryDestinations = listOf(
    PrimaryDestination(
        label = "Dashboard",
        shortLabel = "D",
        navigate = { it.navigateTo(DashboardRoute) },
        isSelected = { it?.hasRoute<DashboardRoute>() == true },
    ),
    PrimaryDestination(
        label = "Vehicles",
        shortLabel = "V",
        navigate = { it.navigateTo(VehiclesRoute) },
        isSelected = { it?.hasRoute<VehiclesRoute>() == true },
    ),
    PrimaryDestination(
        label = "Fuel",
        shortLabel = "F",
        navigate = { it.navigateTo(FuelRoute) },
        isSelected = { it?.hasRoute<FuelRoute>() == true },
    ),
    PrimaryDestination(
        label = "Maintenance",
        shortLabel = "M",
        navigate = { it.navigateTo(MaintenanceRoute) },
        isSelected = { it?.hasRoute<MaintenanceRoute>() == true },
    ),
    PrimaryDestination(
        label = "Statistics",
        shortLabel = "S",
        navigate = { it.navigateTo(StatsRoute) },
        isSelected = { it?.hasRoute<StatsRoute>() == true },
    ),
)

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= TABLET_BREAKPOINT_DP.dp

        if (useNavigationRail) {
            Row(modifier = Modifier.fillMaxSize()) {
                AppNavigationRail(
                    currentDestination = currentDestination,
                    onDestinationSelected = { it.navigate(navController) },
                )
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Scaffold(
                bottomBar = {
                    AppNavigationBar(
                        currentDestination = currentDestination,
                        onDestinationSelected = { it.navigate(navController) },
                    )
                },
            ) { paddingValues ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun AppNavigationBar(
    currentDestination: NavDestination?,
    onDestinationSelected: (PrimaryDestination) -> Unit,
) {
    NavigationBar {
        primaryDestinations.forEach { destination ->
            NavigationBarItem(
                selected = destination.isSelected(currentDestination),
                onClick = { onDestinationSelected(destination) },
                icon = { Text(text = destination.shortLabel) },
                label = { Text(text = destination.label) },
            )
        }
    }
}

@Composable
private fun AppNavigationRail(
    currentDestination: NavDestination?,
    onDestinationSelected: (PrimaryDestination) -> Unit,
) {
    NavigationRail {
        primaryDestinations.forEach { destination ->
            NavigationRailItem(
                selected = destination.isSelected(currentDestination),
                onClick = { onDestinationSelected(destination) },
                icon = { Text(text = destination.shortLabel) },
                label = { Text(text = destination.label) },
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoute,
        modifier = modifier,
    ) {
        composable<DashboardRoute> { DashboardScreen() }
        composable<VehiclesRoute> { VehiclesScreen() }
        composable<FuelRoute> { FuelScreen() }
        composable<MaintenanceRoute> { MaintenanceScreen() }
        composable<StatsRoute> { StatisticsScreen() }
    }
}
