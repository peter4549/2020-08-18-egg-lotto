package com.duke.xial.elliot.kim.kotlin.egglotto.broadcast_receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.duke.xial.elliot.kim.kotlin.egglotto.activities.MainActivity
import com.duke.xial.elliot.kim.kotlin.egglotto.R

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if(getBlockNotification(context!!))
            updateBlockNotification(context)
        else
            notifyWeeklyAlarm(context)
    }

    private fun getBlockNotification(context: Context) = context
        .getSharedPreferences(MainActivity.PREFERENCES_OPTIONS, Context.MODE_PRIVATE)
        .getBoolean(MainActivity.KEY_BLOCK_NOTIFICATION, false)

    private fun updateBlockNotification(context: Context) {
        context.getSharedPreferences(MainActivity.PREFERENCES_OPTIONS, Context.MODE_PRIVATE)
            .edit().putBoolean(MainActivity.KEY_BLOCK_NOTIFICATION, false).apply()
    }

    private fun notifyWeeklyAlarm(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        val notificationIntent = Intent(context, MainActivity::class.java)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = PendingIntent.getActivity(context,
            0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            builder.setSmallIcon(R.drawable.ic_raw_egg)
            channel.description = "ic_raw_egg lotto weekly alarm channel"
            notificationManager.createNotificationChannel(channel)
        } else
            builder.setSmallIcon(R.mipmap.ic_raw_egg)

        val title = context.getString(R.string.app_name)
        val contentText = context.getString(R.string.time_to_check_winning_number)

        builder.setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentInfo(CONTENT_INFO)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 200819
        private const val CHANNEL_ID = "default"
        private const val CHANNEL_NAME = "alarm.receiver.weekly.alarm"
        private const val CONTENT_INFO = "weekly reminder message"
    }
}