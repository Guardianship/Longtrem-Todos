package com.junelin.longtermtodos.di

import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun taskRepository(): TaskRepository
    fun settingsRepository(): SettingsRepository
}
