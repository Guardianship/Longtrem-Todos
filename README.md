# 远期待办 · LongTerm Todos

一款专注于"未来很久要做的事"的 Android 提醒工具。不是今明两天的琐事，而是生日、车辆年检、证件续期、旅行准备、大额采购等需要提前规划的事项。

## 核心理念

- **远期限**：关注 7 天、14 天、甚至几个月后的事项
- **轻量直观**：信息密度高，操作路径短
- **自动提取**：从短信、微信通知中智能识别待办事件
- **随时可见**：锁屏常驻通知 + 桌面小组件，无需解锁即可查看

## 功能特性

### 📋 待办管理
- 按分类（生日、汽车、事务、衣食住行）组织待办
- 水平可滚动分类标签栏，快速过滤
- 左滑删除、复选框标记完成
- 实时搜索标题

### 🤖 自动提取
- **短信提取**：读取短信内容，正则匹配日期和关键词
- **微信通知提取**：通过 NotificationListenerService 监听微信消息
- **智能推断**：根据关键词自动推断分类（生日→生日类，保险→汽车类）
- **弹窗确认**：检测到事件后弹出底部弹窗，用户可选择创建/编辑/忽略
- 所有处理均在本地完成，不联网

### 🔔 提醒系统
- AlarmManager 精确闹钟，到期前 N 天上午 9:00 提醒
- 设备重启后自动恢复所有闹钟
- 已完成事项自动取消后续提醒

### 📱 常驻通知 & 小组件
- 高优先级常驻通知（锁屏可见），显示最近待办
- Glance 桌面小组件，显示未来 N 天内最近的 1~5 条待办
- 支持自定义显示天数（7/14/30/60/90 天）

### ⚙️ 设置
- 锁屏/小组件显示天数、默认提醒天数
- 短信/微信监听独立开关
- 生物识别锁（指纹/面容）
- 数据导出/导入（JSON 格式）

## 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 开发语言 |
| Jetpack Compose | UI 框架 |
| Material 3 | 设计系统 |
| Compose Navigation | 页面导航 |
| Room | 本地数据库 |
| DataStore | 偏好设置 |
| AlarmManager | 精确提醒 |
| WorkManager | 小组件定期刷新 |
| Glance | 桌面小组件 |
| BiometricPrompt | 生物识别 |

## 项目结构

```
app/src/main/java/com/junelin/longtermtodos/
├── data/
│   ├── local/          # Room 数据库、Entity、DAO
│   ├── model/          # Domain Model
│   └── repository/     # 数据仓库
├── ui/
│   ├── home/           # 主界面
│   ├── addtask/        # 添加/编辑任务
│   ├── settings/       # 设置
│   ├── category/       # 分类管理
│   └── theme/          # Material 3 主题
├── service/            # 前台服务、通知监听、短信读取
├── receiver/           # 闹钟广播、开机重启
├── widget/             # Glance 小组件
├── extractor/          # 自动提取引擎
├── reminder/           # 提醒管理
├── biometric/          # 生物识别
├── export/             # 数据导入导出
└── util/               # 工具类
```

## 构建说明

```bash
# 克隆项目
git clone <repo-url>
cd LongTermTodos

# 构建 APK
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## 权限说明

| 权限 | 用途 |
|------|------|
| `READ_SMS` | 自动从短信提取待办事件 |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | 监听微信通知提取事件 |
| `POST_NOTIFICATIONS` | 发送提醒通知和常驻通知 |
| `FOREGROUND_SERVICE` | 保持常驻通知服务运行 |
| `SCHEDULE_EXACT_ALARM` | 设置精确到期提醒 |
| `RECEIVE_BOOT_COMPLETED` | 设备重启后恢复闹钟 |
| `USE_BIOMETRIC` | 生物识别锁 |
| `RECORD_AUDIO` | 语音输入标题 |

## Git 提交记录

项目按 13 个 Phase 分阶段开发，每阶段均有独立提交：

1. `init:` 项目初始化与基础架构搭建
2. `feat:` Room 数据库、DAO、Repository 与预置分类
3. `feat:` ViewModel 与 UI 状态管理
4. `feat:` 主界面 Compose UI（分类标签、任务列表、搜索、左滑删除）
5. `feat:` 添加/编辑任务界面（DatePicker、分类选择、语音输入）
6. `feat:` 分类管理（增删改、排序、图标颜色自定义）
7. `feat:` 自动提取、提醒通知、常驻通知、小组件、设置、生物识别、数据导出
8. `refactor:` 测试优化、字符串提取、最终收尾

## 许可证

MIT License
