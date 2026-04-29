package com.dailylift.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailylift.app.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(notificationHelper: NotificationHelper) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("DailyLift", Context.MODE_PRIVATE)
    }

    var notificationsEnabled by remember {
        mutableStateOf(prefs.getBoolean("notifications_enabled", false))
    }
    var notificationHour by remember {
        mutableStateOf(prefs.getInt("notification_hour", 8))
    }
    var notificationMinute by remember {
        mutableStateOf(prefs.getInt("notification_minute", 0))
    }
    var themePreference by remember {
        mutableStateOf(prefs.getString("theme_preference", "auto") ?: "auto")
    }
    var showTimePicker by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Notifications Section ──
            SectionHeader("Notifications")

            // Enable toggle
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = "Daily Reminder",
                trailing = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            notificationsEnabled = enabled
                            prefs.edit().putBoolean("notifications_enabled", enabled).apply()
                            if (enabled) {
                                notificationHelper.scheduleDailyNotification(
                                    notificationHour,
                                    notificationMinute
                                )
                            } else {
                                notificationHelper.cancelNotifications()
                            }
                        }
                    )
                }
            )

            // Time picker
            if (notificationsEnabled) {
                SettingsRow(
                    icon = Icons.Default.Schedule,
                    title = "Reminder Time",
                    onClick = { showTimePicker = true },
                    trailing = {
                        Text(
                            text = formatTime(notificationHour, notificationMinute),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            SectionFooter("Get inspired every day with a morning quote delivered right to your notifications.")

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── Appearance Section ──
            SectionHeader("Appearance")

            SettingsRow(
                icon = Icons.Default.Brightness4,
                title = "Theme",
                onClick = { showThemeDialog = true },
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (themePreference) {
                                "light" -> "Light"
                                "dark" -> "Dark"
                                else -> "System"
                            },
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // ── About Section ──
            SectionHeader("About")

            // Rate app
            SettingsRow(
                icon = Icons.Default.Star,
                title = "Rate DailyLift",
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.dailylift.app")
                    )
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.dailylift.app")
                            )
                        )
                    }
                },
                trailing = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            )

            // Share app
            SettingsRow(
                icon = Icons.Default.Share,
                title = "Share DailyLift",
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Check out DailyLift — a beautiful app for daily motivation!\nhttps://play.google.com/store/apps/details?id=com.dailylift.app"
                        )
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share DailyLift"))
                },
                trailing = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            )

            // Version
            SettingsRow(
                icon = Icons.Default.Info,
                title = "Version",
                trailing = {
                    Text(
                        text = getAppVersion(context),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            )

            // Privacy policy
            SettingsRow(
                icon = Icons.Default.Security,
                title = "Privacy Policy",
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://dailylift.app/privacy")
                    )
                    context.startActivity(intent)
                },
                trailing = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = notificationHour,
            initialMinute = notificationMinute
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Set Reminder Time") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                TextButton(onClick = {
                    notificationHour = timePickerState.hour
                    notificationMinute = timePickerState.minute
                    prefs.edit()
                        .putInt("notification_hour", notificationHour)
                        .putInt("notification_minute", notificationMinute)
                        .apply()
                    notificationHelper.scheduleDailyNotification(
                        notificationHour,
                        notificationMinute
                    )
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Theme dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    ThemeOption("System Default", "auto", themePreference) {
                        themePreference = "auto"
                        prefs.edit().putString("theme_preference", "auto").apply()
                        showThemeDialog = false
                    }
                    ThemeOption("Light Mode", "light", themePreference) {
                        themePreference = "light"
                        prefs.edit().putString("theme_preference", "light").apply()
                        showThemeDialog = false
                    }
                    ThemeOption("Dark Mode", "dark", themePreference) {
                        themePreference = "dark"
                        prefs.edit().putString("theme_preference", "dark").apply()
                        showThemeDialog = false
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SectionFooter(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        lineHeight = 18.sp
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

@Composable
private fun ThemeOption(
    label: String,
    value: String,
    current: String,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (value) {
                "light" -> Icons.Default.BrightnessHigh
                "dark" -> Icons.Default.DarkMode
                else -> Icons.Default.Brightness4
            },
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (current == value) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (current == value) FontWeight.SemiBold else FontWeight.Normal,
            color = if (current == value) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
}

private fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "${pInfo.versionName} (${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode else @Suppress("DEPRECATION") pInfo.versionCode})"
    } catch (_: Exception) {
        "1.0.0 (1)"
    }
}
