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
import androidx.room.migration.Migration

@Database(
    entities = [TaskEntity::class, CategoryEntity::class, ExtractedEventEntity::class],
    version = 2,
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN isLunarDate INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "longterm_todos.db"
                )
                    .addCallback(DatabaseCallback())
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // 直接使用 SQL 插入预置分类，避免依赖 INSTANCE（此时还未赋值）
            db.execSQL("INSERT INTO categories (id, name, icon, color, sortOrder, isPreset) VALUES (1, '生日', '🎂', '#FF8A80', 0, 1)")
            db.execSQL("INSERT INTO categories (id, name, icon, color, sortOrder, isPreset) VALUES (2, '汽车', '🚗', '#80CBC4', 1, 1)")
            db.execSQL("INSERT INTO categories (id, name, icon, color, sortOrder, isPreset) VALUES (3, '事务', '📋', '#90CAF9', 2, 1)")
            db.execSQL("INSERT INTO categories (id, name, icon, color, sortOrder, isPreset) VALUES (4, '衣食住行', '🏠', '#CE93D8', 3, 1)")
        }
    }
}
