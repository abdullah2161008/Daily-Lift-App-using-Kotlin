package com.dailylift.app.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-schedules daily notification after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("DailyLift", Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean("notifications_enabled", false)
            if (enabled) {
                val hour = prefs.getInt("notification_hour", 8)
                val minute = prefs.getInt("notification_minute", 0)
                NotificationHelper(context).scheduleDailyNotification(hour, minute)
            }
        }
    }
}
