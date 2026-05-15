package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.deleteTask(task)
    }

    suspend operator fun invoke(taskId: Long) {
        taskRepository.deleteTaskById(taskId)
    }
}
