package com.junelin.longtermtodos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.junelin.longtermtodos.data.local.converter.DateConverters
import com.junelin.longtermtodos.data.local.dao.CategoryDao
import com.junelin.longtermtodos.data.local.dao.ExtractedEventDao
import com.junelin.longtermtodos.data.local.dao.TaskDao
import com.junelin.longtermtodos.data.local.entity.CategoryEntity
import com.junelin.longtermtodos.data.local.entity.ExtractedEventEntity
import com.junelin.longtermtodos.data.local.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class, CategoryEntity::class, ExtractedEventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun extractedEventDao(): ExtractedEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "longterm_todos.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populatePresetCategories(database.categoryDao())
                }
            }
        }

        private suspend fun populatePresetCategories(categoryDao: CategoryDao) {
            val presets = listOf(
                CategoryEntity(
                    id = 1,
                    name = "生日",
                    icon = "🎂",
                    color = "#FF8A80",
                    sortOrder = 0,
                    isPreset = true
                ),
                CategoryEntity(
                    id = 2,
                    name = "汽车",
                    icon = "🚗",
                    color = "#80CBC4",
                    sortOrder = 1,
                    isPreset = true
                ),
                CategoryEntity(
                    id = 3,
                    name = "事务",
                    icon = "📋",
                    color = "#90CAF9",
                    sortOrder = 2,
                    isPreset = true
                ),
                CategoryEntity(
                    id = 4,
                    name = "衣食住行",
                    icon = "🏠",
                    color = "#CE93D8",
                    sortOrder = 3,
                    isPreset = true
                )
            )
            categoryDao.insertAll(presets)
        }
    }
}
