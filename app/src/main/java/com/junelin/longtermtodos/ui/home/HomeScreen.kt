package com.junelin.longtermtodos.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onAddTask: () -> Unit = {},
    onEditTask: (Long) -> Unit = {},
    onSettings: () -> Unit = {},
    onManageCategories: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Home Screen - Phase 1 Placeholder")
    }
}
