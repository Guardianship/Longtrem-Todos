package com.junelin.longtermtodos.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.junelin.longtermtodos.di.WidgetEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TodosGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.taskRepository()
        val settings = entryPoint.settingsRepository()
        val days = settings.widgetDisplayDays.first()
        val tasks = repository.getUpcomingTasks(days).first().take(5)

        provideContent {
            WidgetContent(tasks = tasks)
        }
    }
}

@Composable
private fun WidgetContent(tasks: List<com.junelin.longtermtodos.data.model.Task>) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .cornerRadius(16.dp)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "远期待办",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                if (tasks.isNotEmpty()) {
                    Text(
                        text = "${tasks.size}项",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = GlanceTheme.colors.onSurfaceVariant
                        )
                    )
                }
            }
            Spacer(modifier = GlanceModifier.height(12.dp))

            if (tasks.isEmpty()) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无近期待办",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = GlanceTheme.colors.onSurfaceVariant
                        )
                    )
                }
            } else {
                tasks.forEachIndexed { index, task ->
                    WidgetTaskItem(task)
                    if (index < tasks.size - 1) {
                        Spacer(modifier = GlanceModifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetTaskItem(task: com.junelin.longtermtodos.data.model.Task) {
    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), task.dueDate)
    val (daysText, daysColor) = when {
        daysUntil < 0 -> "逾期" to GlanceTheme.colors.error
        daysUntil == 0L -> "今天" to GlanceTheme.colors.error
        daysUntil == 1L -> "明天" to GlanceTheme.colors.primary
        daysUntil <= 7 -> "${daysUntil}天" to GlanceTheme.colors.primary
        else -> "${daysUntil}天" to GlanceTheme.colors.onSurfaceVariant
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(8.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .size(width = 4.dp, height = 16.dp)
                .background(
                    when {
                        daysUntil < 0 -> GlanceTheme.colors.error
                        daysUntil <= 3 -> GlanceTheme.colors.primary
                        else -> GlanceTheme.colors.onSurfaceVariant
                    }
                )
                .cornerRadius(2.dp)
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = task.title,
            style = TextStyle(
                fontSize = 13.sp,
                color = GlanceTheme.colors.onBackground
            ),
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = daysText,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = daysColor
            )
        )
    }
}

class TodosWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodosGlanceWidget()
}
