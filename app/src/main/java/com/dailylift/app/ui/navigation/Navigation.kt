package com.dailylift.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Today : Screen("today", "Today", Icons.Default.WbSunny)
    data object Browse : Screen("browse", "Browse", Icons.Default.Explore)
    data object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Today,
    Screen.Browse,
    Screen.Favorites,
    Screen.Settings
)
