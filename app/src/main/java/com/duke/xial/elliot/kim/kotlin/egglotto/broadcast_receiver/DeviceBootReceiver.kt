package com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.duke.xial.elliot.kim.kotlin.egglotto.*
import java.util.*

class DeviceBootReceiver : BroadcastReceiver()  {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if (getWeeklyAlarmState(context))
                setWeeklyAlarm(context)
        }
    }

    private fun getWeeklyAlarmState(context: Context) =
        context.getSharedPreferences(MainActivity.PREFERENCES_OPTIONS, Context.MODE_PRIVATE)
            .getBoolean(MainActivity.KEY_WEEKLY_ALARM_STATE, false)

    private fun setWeeklyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, WEEKLY_ALARM_DAY_OF_WEEK)
            set(Calendar.HOUR_OF_DAY, WEEKLY_ALARM_HOUR_OF_DAY)
            set(Calendar.MINUTE, WEEKLY_ALARM_MINUTE)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val switchedTime = Calendar.getInstance().timeInMillis
        if (switchedTime > calendar.timeInMillis)
            setBlockNotification(context)
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context,
            0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val receiver = ComponentName(context, DeviceBootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR/6, pendingIntent)//AlarmManager.INTERVAL_DAY * 7
    }

    private fun setBlockNotification(context: Context) {
        context.getSharedPreferences(MainActivity.PREFERENCES_OPTIONS, Context.MODE_PRIVATE).edit()
            .putBoolean(MainActivity.KEY_BLOCK_NOTIFICATION, true).apply()
    }
}