package com.junelin.longtermtodos.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onManageCategories: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDaySelector by remember { mutableStateOf(false) }
    var daySelectorType by remember { mutableStateOf<DaySelectorType?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingGroup(title = "显示") {
                SettingItem(
                    title = "锁屏/小组件显示天数",
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

            SettingGroup(title = "自动提取") {
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

            SettingGroup(title = "安全") {
                SettingSwitchItem(
                    title = "生物识别锁",
                    subtitle = "进入应用时验证指纹或面容",
                    checked = uiState.biometricLock,
                    onCheckedChange = viewModel::setBiometricLock
                )
            }

            SettingGroup(title = "数据") {
                SettingItem(
                    title = "分类排序管理",
                    subtitle = "调整分类显示顺序",
                    onClick = onManageCategories
                )
                SettingItem(
                    title = "数据导出",
                    subtitle = "导出为 JSON 备份文件",
                    onClick = { /* TODO */ }
                )
                SettingItem(
                    title = "数据导入",
                    subtitle = "从 JSON 文件恢复数据",
                    onClick = { /* TODO */ }
                )
            }
        }
    }

    if (showDaySelector && daySelectorType != null) {
        DaySelectorDialog(
            title = when (daySelectorType) {
                DaySelectorType.WIDGET_DAYS -> "锁屏/小组件显示天数"
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
}

enum class DaySelectorType {
    WIDGET_DAYS, REMIND_DAYS
}

@Composable
private fun SettingGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
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
                            .clickable { onSelected(option) }
                            .padding(vertical = 12.dp),
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
                                style = MaterialTheme.typography.bodyLarge
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
