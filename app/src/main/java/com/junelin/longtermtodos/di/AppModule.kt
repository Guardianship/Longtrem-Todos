package com.junelin.longtermtodos.di

import android.content.Context
import com.junelin.longtermtodos.data.local.AppDatabase
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.data.repository.TaskRepository

object AppModule {

    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            AppDatabase.getDatabase(context).also { database = it }
        }
    }

    fun provideTaskRepository(context: Context): TaskRepository {
        return TaskRepository(provideDatabase(context).taskDao())
    }

    fun provideCategoryRepository(context: Context): CategoryRepository {
        return CategoryRepository(provideDatabase(context).categoryDao())
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepository(context)
    }
}
