package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(task: Task): Long {
        return if (task.id == 0L) {
            taskRepository.insertTask(task)
        } else {
            taskRepository.updateTask(task)
            task.id
        }
    }

    suspend fun getDefaultRemindDays(): Int {
        return settingsRepository.defaultRemindDays.first()
    }
}
