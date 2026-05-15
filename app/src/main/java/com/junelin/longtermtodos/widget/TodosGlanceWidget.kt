package com.junelin.longtermtodos.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
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
    androidx.glance.layout.Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(androidx.glance.GlanceTheme.colors.background)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "远期待办",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.glance.GlanceTheme.colors.onBackground
                )
            )
            Spacer(modifier = GlanceModifier.height(12.dp))

            if (tasks.isEmpty()) {
                Text(
                    text = "暂无近期待办",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = androidx.glance.GlanceTheme.colors.onBackground
                    )
                )
            } else {
                tasks.forEach { task ->
                    WidgetTaskItem(task)
                    Spacer(modifier = GlanceModifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun WidgetTaskItem(task: com.junelin.longtermtodos.data.model.Task) {
    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), task.dueDate)
    val daysText = when {
        daysUntil == 0L -> "今天"
        daysUntil == 1L -> "明天"
        daysUntil < 0 -> "已逾期"
        else -> "${daysUntil}天"
    }

    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = task.title,
            style = TextStyle(
                fontSize = 14.sp,
                color = androidx.glance.GlanceTheme.colors.onBackground
            ),
            modifier = GlanceModifier.defaultWeight()
        )
        Text(
            text = daysText,
            style = TextStyle(
                fontSize = 12.sp,
                color = androidx.glance.GlanceTheme.colors.primary
            )
        )
    }
}

class TodosWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodosGlanceWidget()
}
