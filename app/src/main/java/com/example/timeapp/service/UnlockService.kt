package com.example.timeapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.timeapp.MainActivity
import com.example.timeapp.R
import android.app.KeyguardManager
import android.os.Handler
import android.os.Looper

class UnlockService : Service() {

    companion object {
        private const val TAG = "UnlockService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "unlock_channel"
    }

    private lateinit var powerManager: PowerManager
    private lateinit var keyguardManager: KeyguardManager
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "UnlockService created")
        
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "UnlockService started")
        
        // 创建并显示前台服务通知
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // 解锁屏幕
        unlockScreen()
        
        // 服务完成任务后停止
        Handler(Looper.getMainLooper()).postDelayed({
            stopSelf()
        }, 10000) // 10秒后停止服务
        
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "UnlockService destroyed")
        
        // 释放WakeLock
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val description = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text, "现在"))
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun unlockScreen() {
        Log.d(TAG, "Attempting to unlock screen")
//
//        // 获取WakeLock来点亮屏幕
//        wakeLock = powerManager.newWakeLock(
//            PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
//        PowerManager.ACQUIRE_CAUSES_WAKEUP,
//        "TimeApp:UnlockWakeLock"
//        )
        // 对于Android 10+，使用以下方法点亮屏幕
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // 在Service中不能直接使用setTurnScreenOn和setShowWhenLocked
            // 这些方法只能在Activity中使用
            // 使用PowerManager的WakeLock来点亮屏幕
            wakeLock = powerManager.newWakeLock(
                @Suppress("DEPRECATION") // 这些API已废弃但在某些场景下仍是最佳选择
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "TimeApp:UnlockWakeLock"
            )
            wakeLock?.acquire(10000) // 获取10秒的WakeLock
            
            // 如果需要在锁屏上显示内容，需要启动一个Activity并在Activity中使用setShowWhenLocked
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            
            // 注意：WakeLock会在10秒后自动释放，或者在onDestroy()中释放
            // 如果需要立即释放，可以取消下面的注释
            // wakeLock?.release()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            // 对于较低版本，尝试使用KeyguardManager解锁
            try {
                @Suppress("DEPRECATION")
                val keyguardLock = keyguardManager.newKeyguardLock("TimeApp")
                @Suppress("DEPRECATION")
                keyguardLock.disableKeyguard()
                Log.d(TAG, "Keyguard disabled")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to disable keyguard: ${e.message}")
            }
        } else {
            Log.d(TAG, "On Android 8.1+, can only wake up screen, not unlock it")
            // 在较新的Android版本上，我们只能点亮屏幕，无法直接解锁
            // 用户需要手动解锁或使用生物识别解锁
        }
    }
}