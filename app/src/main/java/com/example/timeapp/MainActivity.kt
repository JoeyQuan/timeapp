package com.example.timeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timeapp.databinding.ActivityMainBinding
import com.example.timeapp.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化
        sharedPreferences = getSharedPreferences("TimeAppPrefs", Context.MODE_PRIVATE)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 设置解锁时间按钮点击事件
        binding.setAlarmButton.setOnClickListener {
            setAlarm()
        }

        // 取消解锁时间按钮点击事件
        binding.cancelAlarmButton.setOnClickListener {
            cancelAlarm()
        }

        // 更新UI显示当前状态
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun setAlarm() {
        // 获取用户选择的时间
        val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute

        // 创建日历实例并设置时间
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        // 如果设置的时间已经过去，则设置为明天的这个时间
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // 创建Intent和PendingIntent
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 设置精确闹钟
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        // 保存设置的时间到SharedPreferences
        sharedPreferences.edit().apply {
            putLong("alarmTime", calendar.timeInMillis)
            putBoolean("alarmSet", true)
            apply()
        }

        // 更新UI
        updateUI()

        // 显示提示
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeString = timeFormat.format(calendar.time)
        val message = getString(R.string.alarm_set, timeString)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm() {
        // 创建Intent和PendingIntent
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 取消闹钟
        alarmManager.cancel(pendingIntent)

        // 更新SharedPreferences
        sharedPreferences.edit().apply {
            putBoolean("alarmSet", false)
            apply()
        }

        // 更新UI
        updateUI()

        // 显示提示
        Toast.makeText(this, R.string.alarm_canceled, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        val isAlarmSet = sharedPreferences.getBoolean("alarmSet", false)

        if (isAlarmSet) {
            val alarmTime = sharedPreferences.getLong("alarmTime", 0)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeString = timeFormat.format(Date(alarmTime))

            binding.statusTextView.text = getString(R.string.alarm_set, timeString)
            binding.cancelAlarmButton.visibility = android.view.View.VISIBLE
        } else {
            binding.statusTextView.text = getString(R.string.no_alarm_set)
            binding.cancelAlarmButton.visibility = android.view.View.GONE
        }
    }
}