package com.junelin.longtermtodos.data.repository

import com.junelin.longtermtodos.data.local.dao.TaskDao
import com.junelin.longtermtodos.data.local.entity.TaskEntity
import com.junelin.longtermtodos.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllActiveTasks(): Flow<List<Task>> =
        taskDao.getAllActiveFlow().map { list -> list.map { Task.fromEntity(it) } }

    fun getTasksByCategory(categoryId: Long): Flow<List<Task>> =
        taskDao.getByCategoryFlow(categoryId).map { list -> list.map { Task.fromEntity(it) } }

    fun getUpcomingTasks(days: Int): Flow<List<Task>> {
        val maxDate = LocalDate.now().plusDays(days.toLong()).toEpochDay()
        return taskDao.getUpcomingFlow(maxDate).map { list -> list.map { Task.fromEntity(it) } }
    }

    fun searchTasks(query: String): Flow<List<Task>> =
        taskDao.searchFlow(query).map { list -> list.map { Task.fromEntity(it) } }

    fun getTaskById(taskId: Long): Flow<Task?> =
        taskDao.getByIdFlow(taskId).map { it?.let { Task.fromEntity(it) } }

    suspend fun getTaskByIdSync(taskId: Long): Task? =
        taskDao.getById(taskId)?.let { Task.fromEntity(it) }

    suspend fun insertTask(task: Task): Long = taskDao.insert(task.toEntity())

    suspend fun updateTask(task: Task) = taskDao.update(task.toEntity())

    suspend fun deleteTask(task: Task) = taskDao.delete(task.toEntity())

    suspend fun deleteTaskById(taskId: Long) = taskDao.deleteById(taskId)

    suspend fun setTaskCompleted(taskId: Long, completed: Boolean) =
        taskDao.setCompleted(taskId, completed)

    suspend fun getUpcomingLimited(limit: Int): List<Task> =
        taskDao.getUpcomingLimited(limit).map { Task.fromEntity(it) }
}
