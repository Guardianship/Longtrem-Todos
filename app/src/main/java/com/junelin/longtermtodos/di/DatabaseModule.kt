package com.junelin.longtermtodos.di

import android.content.Context
import androidx.room.Room
import com.junelin.longtermtodos.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "longterm_todos.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase) = database.taskDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.categoryDao()

    @Provides
    fun provideExtractedEventDao(database: AppDatabase) = database.extractedEventDao()
}
