package com.example.timeapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.timeapp.service.UnlockService

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm received, starting unlock service")
        
        // 清除SharedPreferences中的闹钟设置
        val sharedPreferences = context.getSharedPreferences("TimeAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putBoolean("alarmSet", false)
            apply()
        }
        
        // 启动解锁服务
        val serviceIntent = Intent(context, UnlockService::class.java)
        context.startForegroundService(serviceIntent)
    }
}