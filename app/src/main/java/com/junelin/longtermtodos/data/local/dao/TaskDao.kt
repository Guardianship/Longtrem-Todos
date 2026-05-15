package com.junelin.longtermtodos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.junelin.longtermtodos.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: Long)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getByIdFlow(taskId: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, createdAt DESC")
    fun getAllFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC, createdAt DESC")
    fun getAllActiveFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId AND isCompleted = 0 ORDER BY dueDate ASC, createdAt DESC")
    fun getByCategoryFlow(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND dueDate <= :maxDate ORDER BY dueDate ASC")
    fun getUpcomingFlow(maxDate: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND title LIKE '%' || :query || '%' ORDER BY dueDate ASC")
    fun searchFlow(query: String): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setCompleted(taskId: Long, completed: Boolean)

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC LIMIT :limit")
    suspend fun getUpcomingLimited(limit: Int): List<TaskEntity>
}
