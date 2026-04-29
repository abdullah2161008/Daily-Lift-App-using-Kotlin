package com.dailylift.app

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dailylift.app.ui.QuoteViewModel
import com.dailylift.app.ui.navigation.Screen
import com.dailylift.app.ui.navigation.bottomNavItems
import com.dailylift.app.ui.screens.BrowseScreen
import com.dailylift.app.ui.screens.FavoritesScreen
import com.dailylift.app.ui.screens.SettingsScreen
import com.dailylift.app.ui.screens.TodayScreen
import com.dailylift.app.ui.theme.DailyLiftTheme
import com.dailylift.app.utils.NotificationHelper

class MainActivity : ComponentActivity() {

    private lateinit var notificationHelper: NotificationHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val prefs = getSharedPreferences("DailyLift", Context.MODE_PRIVATE)
            val hour = prefs.getInt("notification_hour", 8)
            val minute = prefs.getInt("notification_minute", 0)
            notificationHelper.scheduleDailyNotification(hour, minute)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationHelper = NotificationHelper(this)

        // Request notification permission on first launch
        val prefs = getSharedPreferences("DailyLift", Context.MODE_PRIVATE)
        val hasLaunched = prefs.getBoolean("has_launched", false)
        if (!hasLaunched) {
            prefs.edit().putBoolean("has_launched", true).apply()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val themePreference = remember {
                prefs.getString("theme_preference", "auto") ?: "auto"
            }

            val darkTheme = when (themePreference) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            DailyLiftTheme(darkTheme = darkTheme) {
                DailyLiftApp(notificationHelper)
            }
        }
    }
}

@Composable
fun DailyLiftApp(notificationHelper: NotificationHelper) {
    val navController = rememberNavController()
    val viewModel: QuoteViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on Today screen for full immersive experience
    val showBottomBar = currentRoute != Screen.Today.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            composable(Screen.Today.route) {
                TodayScreen(viewModel = viewModel)
            }
            composable(Screen.Browse.route) {
                BrowseScreen(viewModel = viewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(notificationHelper = notificationHelper)
            }
        }
    }
}
