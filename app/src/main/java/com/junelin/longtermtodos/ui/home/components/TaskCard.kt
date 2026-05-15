@file:OptIn(ExperimentalMaterial3Api::class)

package com.junelin.longtermtodos.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.junelin.longtermtodos.data.local.entity.TaskSource
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.util.LunarCalendar

@Composable
fun TaskCard(
    task: Task,
    categoryColor: Color,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                label = "dismiss_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        },
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Left colored indicator bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(categoryColor)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleComplete() }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (task.isCompleted)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            // Date display
                            val dateText = if (task.isLunarDate) {
                                LunarCalendar.solarToLunar(task.dueDate).format()
                            } else {
                                task.formattedDueDate
                            }

                            Text(
                                text = dateText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (task.isOverdue)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = task.displayDaysLeft,
                                style = MaterialTheme.typography.labelSmall,
                                color = when {
                                    task.isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
                                    task.isOverdue -> MaterialTheme.colorScheme.error
                                    task.daysUntil <= 3 -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )

                            if (task.isLunarDate) {
                                TagChip(text = "农历", containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            }

                            if (task.source != TaskSource.MANUAL) {
                                SourceTag(source = task.source)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String, containerColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(containerColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun SourceTag(source: TaskSource) {
    val (text, containerColor) = when (source) {
        TaskSource.AUTO_SMS -> "短信" to Color(0xFFE3F2FD)
        TaskSource.AUTO_WECHAT -> "微信" to Color(0xFFE8F5E9)
        else -> return
    }
    val textColor = when (source) {
        TaskSource.AUTO_SMS -> Color(0xFF1565C0)
        TaskSource.AUTO_WECHAT -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(containerColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
