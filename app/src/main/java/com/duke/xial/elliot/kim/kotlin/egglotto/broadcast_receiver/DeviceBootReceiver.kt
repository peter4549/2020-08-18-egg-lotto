package com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.duke.xial.elliot.kim.kotlin.egglotto.showToast
import java.util.*

class DeviceBootReceiver : BroadcastReceiver()  {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

        }
    }

    private fun setWeeklyAlarm(context: Context) {
        val switchedTime = Calendar.getInstance().timeInMillis
        if (switchedTime > calendar.timeInMillis)
            setBlockNotification()
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this,
            0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        showToast(this, longTimeToString(calendar.timeInMillis))

        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR/6, pendingIntent)//AlarmManager.INTERVAL_DAY * 7
    }
}