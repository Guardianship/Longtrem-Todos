package com.junelin.longtermtodos.ui.addtask

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AddTaskScreen(
    taskId: Long? = null,
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(if (taskId != null) "Edit Task - Phase 1 Placeholder" else "Add Task - Phase 1 Placeholder")
    }
}
