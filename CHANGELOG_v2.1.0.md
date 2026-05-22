---
name: optimization-v2.1.0
description: 2026-05-22 v2.1.0 全面优化记录 - 修复核心功能缺陷、完善设置、美化UI、升级依赖
metadata:
  type: project
---

# 远期待办 v2.1.0 优化记录

**日期**: 2026-05-22
**版本**: v2.0.0 → v2.1.0 (versionCode 2→3)

## 阶段1：修复核心功能缺陷

### 1.1 生物识别锁集成
- **问题**: BiometricGuardScreen 存在但从未被导航调用，开关打开后不起作用
- **修复**: 
  - MainActivity 注入 SettingsRepository，读取 biometricLock 设置
  - 启动时若开启生物识别锁，先显示 BiometricGuardScreen
  - 验证通过后用 AnimatedVisibility 过渡到 TodoNavHost
  - 无生物识别硬件时自动跳过
- **文件**: `MainActivity.kt`, `BiometricGuardScreen.kt`

### 1.2 数据导出修复
- **问题**: exportData() 只生成JSON存ViewModel，从未写入文件
- **修复**:
  - 使用 ActivityResultContracts.CreateDocument 让用户选择保存位置
  - JSON 生成后自动触发系统文件选择器
  - 写入成功/失败通过 Snackbar 提示
  - 文件名自动带时间戳
- **文件**: `SettingsScreen.kt`, `SettingsViewModel.kt`

### 1.3 数据导入实现
- **问题**: onClick 为空 TODO 注释，完全未实现
- **修复**:
  - 使用 ActivityResultContracts.OpenDocument 选择 JSON 文件
  - 调用已有的 ImportDataUseCase 解析并写入数据库
  - 导入成功/失败通过 Snackbar 提示
- **文件**: `SettingsScreen.kt`, `SettingsViewModel.kt`

## 阶段2：完善设置功能

### 2.1 主题色设置生效
- 新增4套完整 Material3 配色: sage(深林绿), amber(暖琥珀), blue(天空蓝), rose(玫瑰粉)
- Theme.kt 根据 themeColor 参数选择配色
- MainActivity 从 DataStore 读取传入 Theme
- **文件**: `Color.kt`, `Theme.kt`, `MainActivity.kt`

### 2.2 深色模式切换
- 新增 darkMode 设置 (system/light/dark)
- Theme.kt 根据 darkMode 决定深浅色
- **文件**: `SettingsRepository.kt`, `GetSettingsUseCase.kt`, `SettingsViewModel.kt`, `SettingsScreen.kt`

### 2.3 Dynamic Color 开关
- Android 12+ 显示"动态取色"开关
- 开启后使用 Material You dynamicColorScheme
- **文件**: 同上 + `Theme.kt`

### 2.4 设置项文案修正
- "锁屏/小组件显示天数" → "小组件显示天数"

## 阶段3：UI 美化

### 3.1 Glance Widget 美化
- 任务卡片化+紧急度指示条+倒计时着色+任务计数
- **文件**: `TodosGlanceWidget.kt`

### 3.2 设置页美化
- 分组图标+导出导入进度指示+Snackbar反馈
- **文件**: `SettingsScreen.kt`

### 3.3 生物识别锁屏美化
- Fingerprint图标+圆形背景+友好文案
- **文件**: `BiometricGuardScreen.kt`

## 阶段4：技术升级

### 4.1 SDK/依赖更新
- compileSdk/targetSdk: 34 → 35
- AGP: 8.2.2 → 8.7.3
- Compose BOM: 2024.02.00 → 2024.12.01
- 主要依赖全面升级到最新稳定版
- **文件**: `app/build.gradle.kts`, `build.gradle.kts`

### 4.2 边到边显示优化
- themes.xml 透明状态栏/导航栏
- **文件**: `themes.xml`

## 修改文件清单

| 文件 | 变更类型 |
|------|----------|
| `MainActivity.kt` | 重写 |
| `BiometricGuardScreen.kt` | 重写 |
| `SettingsScreen.kt` | 重写 |
| `SettingsViewModel.kt` | 重写 |
| `SettingsRepository.kt` | 修改 |
| `GetSettingsUseCase.kt` | 修改 |
| `Color.kt` | 重写 |
| `Theme.kt` | 重写 |
| `TodosGlanceWidget.kt` | 重写 |
| `app/build.gradle.kts` | 修改 |
| `build.gradle.kts` | 修改 |
| `themes.xml` | 修改 |

## 已知遗留问题
- 语音输入按钮仍无实际功能
- 微信通知监听权限引导可优化
- 锁屏小组件 Android 12+ 不可用（系统限制）
