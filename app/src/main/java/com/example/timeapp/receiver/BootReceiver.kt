package com.example.timeapp.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, restoring alarm if needed")
            
            // 检查是否有设置的闹钟
            val sharedPreferences = context.getSharedPreferences("TimeAppPrefs", Context.MODE_PRIVATE)
            val isAlarmSet = sharedPreferences.getBoolean("alarmSet", false)
            
            if (isAlarmSet) {
                val alarmTime = sharedPreferences.getLong("alarmTime", 0)
                
                // 如果设置的时间已经过去，则不重新设置闹钟
                if (alarmTime > System.currentTimeMillis()) {
                    // 重新设置闹钟
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(context, AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                    
                    Log.d(TAG, "Alarm restored for time: $alarmTime")
                } else {
                    // 清除过期的闹钟设置
                    sharedPreferences.edit().apply {
                        putBoolean("alarmSet", false)
                        apply()
                    }
                    Log.d(TAG, "Alarm time already passed, clearing alarm settings")
                }
            }
        }
    }
}