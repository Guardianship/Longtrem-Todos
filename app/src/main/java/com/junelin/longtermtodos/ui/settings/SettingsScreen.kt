package com.junelin.longtermtodos.ui.settings

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.junelin.longtermtodos.util.device.AutoStartUtils
import com.junelin.longtermtodos.util.device.BatteryOptimizationUtils
import com.junelin.longtermtodos.util.device.DeviceUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onManageCategories: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDaySelector by remember { mutableStateOf(false) }
    var daySelectorType by remember { mutableStateOf<DaySelectorType?>(null) }
    var showDarkModeSelector by remember { mutableStateOf(false) }
    var showThemeColorSelector by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            val json = uiState.exportJson
            if (json != null) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        os.write(json.toByteArray(Charsets.UTF_8))
                    }
                    viewModel.showMessage("导出成功")
                } catch (e: Exception) {
                    viewModel.showMessage("导出失败: ${e.message}")
                }
                viewModel.clearExportJson()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importData(it) }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.exportJson) {
        uiState.exportJson?.let {
            val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US)
                .format(java.util.Date())
            exportLauncher.launch("longterm_todos_backup_$timestamp.json")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "设置",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SettingCard(title = "显示", icon = Icons.Outlined.Widgets) {
                SettingItem(
                    title = "小组件显示天数",
                    subtitle = "${uiState.widgetDisplayDays} 天",
                    onClick = {
                        daySelectorType = DaySelectorType.WIDGET_DAYS
                        showDaySelector = true
                    }
                )
                SettingItem(
                    title = "默认提前提醒天数",
                    subtitle = "${uiState.defaultRemindDays} 天",
                    onClick = {
                        daySelectorType = DaySelectorType.REMIND_DAYS
                        showDaySelector = true
                    }
                )
            }

            SettingCard(title = "外观", icon = Icons.Default.Palette) {
                SettingItem(
                    title = "深色模式",
                    subtitle = when (uiState.darkMode) {
                        "light" -> "浅色"
                        "dark" -> "深色"
                        else -> "跟随系统"
                    },
                    onClick = { showDarkModeSelector = true }
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingSwitchItem(
                        title = "动态取色",
                        subtitle = "使用 Material You 配色方案",
                        checked = uiState.dynamicColor,
                        onCheckedChange = viewModel::setDynamicColor
                    )
                }
                SettingItem(
                    title = "主题色",
                    subtitle = when (uiState.themeColor) {
                        "amber" -> "暖琥珀"
                        "blue" -> "天空蓝"
                        "rose" -> "玫瑰粉"
                        else -> "默认"
                    },
                    onClick = { showThemeColorSelector = true }
                )
            }

            SettingCard(title = "自动提取", icon = Icons.Outlined.Message) {
                SettingSwitchItem(
                    title = "短信监听",
                    subtitle = "自动从短信中提取待办事件",
                    checked = uiState.autoExtractSms,
                    onCheckedChange = viewModel::setAutoExtractSms
                )
                SettingSwitchItem(
                    title = "微信通知监听",
                    subtitle = "自动从微信通知中提取待办事件",
                    checked = uiState.autoExtractWechat,
                    onCheckedChange = viewModel::setAutoExtractWechat
                )
            }

            SettingCard(title = "安全", icon = Icons.Outlined.Lock) {
                SettingSwitchItem(
                    title = "生物识别锁",
                    subtitle = "进入应用时验证指纹或面容",
                    checked = uiState.biometricLock,
                    onCheckedChange = viewModel::setBiometricLock
                )
            }

            if (DeviceUtils.isColorOS()) {
                SettingCard(title = "设备适配 (${DeviceUtils.getDeviceInfo()})", icon = Icons.Outlined.Notifications) {
                    val isIgnoring = BatteryOptimizationUtils.isIgnoringBatteryOptimizations(context)
                    SettingItem(
                        title = "忽略电池优化",
                        subtitle = if (isIgnoring) "已开启" else "点击开启，确保提醒正常运行",
                        onClick = {
                            if (!isIgnoring) {
                                BatteryOptimizationUtils.requestIgnoreBatteryOptimizations(
                                    context as android.app.Activity
                                )
                            }
                        }
                    )
                    SettingItem(
                        title = "自启动权限",
                        subtitle = "点击前往系统设置开启",
                        onClick = { AutoStartUtils.openAutoStartSettings(context) }
                    )
                }
            }

            SettingCard(title = "数据", icon = Icons.Outlined.FileDownload) {
                SettingItem(
                    title = "分类排序管理",
                    subtitle = "调整分类显示顺序",
                    onClick = onManageCategories
                )
                SettingItem(
                    title = "数据导出",
                    subtitle = "导出为 JSON 备份文件",
                    onClick = { viewModel.exportData() },
                    trailing = {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )
                SettingItem(
                    title = "数据导入",
                    subtitle = "从 JSON 文件恢复数据",
                    onClick = {
                        importLauncher.launch(arrayOf("application/json", "*/*"))
                    },
                    trailing = {
                        if (uiState.isImporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "远期待办 · LongTerm Todos v2.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDaySelector && daySelectorType != null) {
        DaySelectorDialog(
            title = when (daySelectorType) {
                DaySelectorType.WIDGET_DAYS -> "小组件显示天数"
                DaySelectorType.REMIND_DAYS -> "默认提前提醒天数"
                null -> ""
            },
            currentValue = when (daySelectorType) {
                DaySelectorType.WIDGET_DAYS -> uiState.widgetDisplayDays
                DaySelectorType.REMIND_DAYS -> uiState.defaultRemindDays
                null -> 14
            },
            options = when (daySelectorType) {
                DaySelectorType.WIDGET_DAYS -> listOf(7, 14, 30, 60, 90)
                DaySelectorType.REMIND_DAYS -> listOf(1, 3, 7, 14, 30)
                null -> emptyList()
            },
            onDismiss = { showDaySelector = false },
            onSelected = { value ->
                when (daySelectorType) {
                    DaySelectorType.WIDGET_DAYS -> viewModel.setWidgetDisplayDays(value)
                    DaySelectorType.REMIND_DAYS -> viewModel.setDefaultRemindDays(value)
                    null -> {}
                }
                showDaySelector = false
            }
        )
    }

    if (showDarkModeSelector) {
        AlertDialog(
            onDismissRequest = { showDarkModeSelector = false },
            title = { Text("深色模式") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DarkModeOption("跟随系统", "system", uiState.darkMode) {
                        viewModel.setDarkMode("system")
                        showDarkModeSelector = false
                    }
                    DarkModeOption("浅色", "light", uiState.darkMode) {
                        viewModel.setDarkMode("light")
                        showDarkModeSelector = false
                    }
                    DarkModeOption("深色", "dark", uiState.darkMode) {
                        viewModel.setDarkMode("dark")
                        showDarkModeSelector = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDarkModeSelector = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showThemeColorSelector) {
        AlertDialog(
            onDismissRequest = { showThemeColorSelector = false },
            title = { Text("主题色") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeColorOption("默认（深林绿）", "sage", uiState.themeColor) {
                        viewModel.setThemeColor("sage")
                        showThemeColorSelector = false
                    }
                    ThemeColorOption("暖琥珀", "amber", uiState.themeColor) {
                        viewModel.setThemeColor("amber")
                        showThemeColorSelector = false
                    }
                    ThemeColorOption("天空蓝", "blue", uiState.themeColor) {
                        viewModel.setThemeColor("blue")
                        showThemeColorSelector = false
                    }
                    ThemeColorOption("玫瑰粉", "rose", uiState.themeColor) {
                        viewModel.setThemeColor("rose")
                        showThemeColorSelector = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeColorSelector = false }) {
                    Text("取消")
                }
            }
        )
    }
}

enum class DaySelectorType {
    WIDGET_DAYS, REMIND_DAYS
}

@Composable
private fun DarkModeOption(
    label: String,
    value: String,
    currentValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (value == currentValue) {
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ThemeColorOption(
    label: String,
    value: String,
    currentValue: String?,
    onClick: () -> Unit
) {
    val isSelected = (currentValue ?: "sage") == value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DaySelectorDialog(
    title: String,
    currentValue: Int,
    options: List<Int>,
    onDismiss: () -> Unit,
    onSelected: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelected(option) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$option 天",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (option == currentValue) {
                            Text(
                                text = "✓",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
