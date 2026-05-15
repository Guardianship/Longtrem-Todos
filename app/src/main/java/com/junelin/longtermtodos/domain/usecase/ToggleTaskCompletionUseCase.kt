package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.repository.TaskRepository
import javax.inject.Inject

class ToggleTaskCompletionUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: Long, completed: Boolean) {
        taskRepository.setTaskCompleted(taskId, completed)
    }
}
