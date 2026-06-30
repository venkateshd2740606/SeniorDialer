package com.seniordialer.presentation.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.seniordialer.presentation.ui.screens.dialer.*

sealed class Screen(val route: String, val label: String = "") {
    data object DialerHome : Screen("dialer_home", "Dialer")
    data object Favorites : Screen("favorites", "Favorites")
    data object Settings : Screen("settings", "Settings")
    data object AddContact : Screen("add_contact")
    data object EditContact : Screen("edit_contact/{id}") {
        fun create(id: Long) = "edit_contact/$id"
    }
}

@Composable
fun SeniorDialerNavHost(navController: NavHostController) {
    val tabs = listOf(Screen.DialerHome, Screen.Favorites, Screen.Settings)
    val navBackStack by navController.currentBackStackEntryAsState()
    val current = navBackStack?.destination?.route
    val showBar = tabs.any { it.route == current }

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar(Modifier.height(72.dp)) {
                    tabs.forEach { tab ->
                        val icon = when (tab) {
                            Screen.DialerHome -> Icons.Default.Call
                            Screen.Favorites -> Icons.Default.ContactPhone
                            Screen.Settings -> Icons.Default.Settings
                            else -> Icons.Default.Call
                        }
                        NavigationBarItem(
                            selected = current == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, tab.label) },
                            label = { Text(tab.label, style = MaterialTheme.typography.titleMedium) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = Screen.DialerHome.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.DialerHome.route) { DialerHomeScreen() }
            composable(Screen.Favorites.route) {
                FavoritesScreen(onAddEdit = { id ->
                    if (id == null) navController.navigate(Screen.AddContact.route)
                    else navController.navigate(Screen.EditContact.create(id))
                })
            }
            composable(Screen.Settings.route) { DialerSettingsScreen() }
            composable(Screen.AddContact.route) {
                AddEditContactScreen(null, onBack = { navController.popBackStack() })
            }
            composable(
                Screen.EditContact.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                AddEditContactScreen(entry.arguments?.getLong("id"), onBack = { navController.popBackStack() })
            }
        }
    }
}
