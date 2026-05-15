package com.junelin.longtermtodos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.junelin.longtermtodos.data.local.entity.ExtractedEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtractedEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: ExtractedEventEntity): Long

    @Update
    suspend fun update(event: ExtractedEventEntity)

    @Delete
    suspend fun delete(event: ExtractedEventEntity)

    @Query("DELETE FROM extracted_events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)

    @Query("SELECT * FROM extracted_events WHERE id = :eventId")
    suspend fun getById(eventId: Long): ExtractedEventEntity?

    @Query("SELECT * FROM extracted_events WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingFlow(): Flow<List<ExtractedEventEntity>>

    @Query("SELECT * FROM extracted_events WHERE status = 'PENDING' ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestPending(): ExtractedEventEntity?

    @Query("UPDATE extracted_events SET status = :status WHERE id = :eventId")
    suspend fun updateStatus(eventId: Long, status: String)

    @Query("DELETE FROM extracted_events WHERE status != 'PENDING'")
    suspend fun clearProcessed()
}
