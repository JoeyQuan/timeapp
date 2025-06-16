# 构建与安装说明

## 前提条件

要构建此应用，您需要安装以下软件：

1. Android Studio (推荐使用最新版本)
2. JDK 8 或更高版本
3. Android SDK (通过Android Studio安装)

## 使用Android Studio构建

1. 打开Android Studio
2. 选择 "Open an existing Android Studio project"
3. 浏览并选择 `timeapp` 目录
4. 等待Gradle同步完成
5. 点击工具栏中的 "Build" > "Build Bundle(s) / APK(s)" > "Build APK(s)"
6. 构建完成后，APK文件将位于 `app/build/outputs/apk/debug/app-debug.apk`

## 安装APK

### 方法1：通过Android Studio

1. 将Android设备连接到电脑，并确保已启用USB调试
2. 在Android Studio中点击 "Run" > "Run 'app'"
3. 选择您的设备，然后点击 "OK"

### 方法2：手动安装

1. 将生成的APK文件 (`app-debug.apk`) 复制到您的Android设备
2. 在设备上找到并点击该文件
3. 按照屏幕上的说明完成安装

## 注意事项

- 确保在设备设置中允许安装来自未知来源的应用
- 应用需要以下权限才能正常工作：
  - 接收开机广播
  - 唤醒锁定
  - 禁用键盘锁
  - 系统警报窗口
  - 前台服务

## 故障排除

如果遇到构建问题：

1. 确保Gradle版本兼容 (在 `gradle/wrapper/gradle-wrapper.properties` 中检查)
2. 尝试在Android Studio中执行 "File" > "Invalidate Caches / Restart"
3. 确保所有依赖项都可以正确解析

如果应用无法解锁屏幕：

1. 请注意，在Android 8.1 (API 27) 及以上版本，由于系统安全限制，应用只能点亮屏幕，无法直接解锁
2. 确保已授予应用所有必要的权限