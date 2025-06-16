# TimeApp - 定时解锁应用

## 项目简介

TimeApp是一个Android应用，允许用户设置一个特定的时间，应用将在该时间自动解锁手机屏幕。这对于需要在特定时间查看手机但不想错过提醒的用户非常有用。

## 功能特点

- 简洁直观的用户界面
- 时间选择器，方便设置解锁时间
- 在指定时间自动点亮并尝试解锁屏幕
- 支持设备重启后恢复设置的闹钟
- 前台服务通知，确保应用不会被系统终止

## 技术实现

- 使用AlarmManager设置精确的定时任务
- 使用PowerManager和WakeLock唤醒设备
- 在支持的设备上尝试使用KeyguardManager解锁屏幕
- 使用前台服务确保解锁操作可靠执行
- 使用SharedPreferences保存用户设置

## 注意事项

- 在Android 8.1及以上版本，由于系统安全限制，应用只能点亮屏幕，无法直接解锁（需要用户手动解锁或使用生物识别）
- 应用需要以下权限：
  - RECEIVE_BOOT_COMPLETED：在设备重启后恢复闹钟
  - WAKE_LOCK：唤醒设备
  - DISABLE_KEYGUARD：尝试解锁屏幕（在支持的设备上）
  - SYSTEM_ALERT_WINDOW：在锁屏上显示窗口
  - FOREGROUND_SERVICE：运行前台服务

## 构建与安装

1. 使用Android Studio打开项目
2. 连接Android设备或启动模拟器
3. 点击"Run"按钮安装应用

或者使用命令行：

```
./gradlew assembleDebug
```

生成的APK文件位于`app/build/outputs/apk/debug/app-debug.apk`

## 使用方法

1. 打开应用
2. 使用时间选择器设置所需的解锁时间
3. 点击"设置解锁时间"按钮
4. 应用将在指定时间尝试解锁屏幕
5. 如需取消，点击"取消解锁时间"按钮