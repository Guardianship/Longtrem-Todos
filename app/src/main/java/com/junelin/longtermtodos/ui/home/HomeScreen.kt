package com.junelin.longtermtodos.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.ui.common.EmptyState
import com.junelin.longtermtodos.ui.home.components.CategoryTabRow
import com.junelin.longtermtodos.ui.home.components.TaskCard
import com.junelin.longtermtodos.ui.theme.GradientEnd
import com.junelin.longtermtodos.ui.theme.GradientStart
import com.junelin.longtermtodos.ui.theme.UpcomingDistant
import com.junelin.longtermtodos.ui.theme.UpcomingSafe
import com.junelin.longtermtodos.ui.theme.UpcomingUrgent
import com.junelin.longtermtodos.ui.theme.UpcomingWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    onSettings: () -> Unit,
    onManageCategories: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "撤销",
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                // Undo not implemented
            }
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isSearchActive) {
                        Text(
                            "远期待办",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = { viewModel.setSearchActive(false) }) {
                            Icon(Icons.Default.Search, contentDescription = "返回")
                        }
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { viewModel.setSearchActive(true) }) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                        IconButton(onClick = onSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "设置")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "添加待办",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        placeholder = { Text("搜索待办标题") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Stats Overview
            if (!isSearchActive) {
                StatsOverview(stats = stats, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            // Category tabs
            CategoryTabRow(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = viewModel::selectCategory
            )

            // Task list
            if (tasks.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Inbox,
                    title = "暂无待办事项",
                    description = "点击右下角按钮添加你的第一个远期待办",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                TaskList(
                    tasks = tasks,
                    categories = categories,
                    onToggleComplete = { taskId, completed ->
                        viewModel.toggleTaskCompletion(taskId, !completed)
                    },
                    onDelete = { task ->
                        viewModel.deleteTask(task)
                    },
                    onTaskClick = { taskId ->
                        onEditTask(taskId)
                    }
                )
            }
        }
    }
}

@Composable
private fun StatsOverview(
    stats: HomeStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Outlined.CalendarToday,
            value = stats.total.toString(),
            label = "总待办",
            color = GradientStart,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Outlined.WarningAmber,
            value = stats.upcoming.toString(),
            label = "7天内",
            color = UpcomingWarning,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Outlined.CheckCircle,
            value = stats.completed.toString(),
            label = "已完成",
            color = UpcomingSafe,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.15f), color.copy(alpha = 0.05f))
                )
            )
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    categories: List<com.junelin.longtermtodos.data.model.Category>,
    onToggleComplete: (Long, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val categoryColorMap = categories.associateBy({ it.id }, { it.color })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            val colorString = categoryColorMap[task.categoryId] ?: "#3D6B4F"
            val color = try {
                Color(android.graphics.Color.parseColor(colorString))
            } catch (_: Exception) {
                MaterialTheme.colorScheme.primary
            }

            TaskCard(
                task = task,
                categoryColor = color,
                onToggleComplete = { onToggleComplete(task.id, task.isCompleted) },
                onDelete = { onDelete(task) },
                onClick = { onTaskClick(task.id) }
            )
        }
    }
}
