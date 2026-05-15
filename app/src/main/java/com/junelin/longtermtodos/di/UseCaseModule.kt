package com.junelin.longtermtodos.di

import android.content.Context
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import com.junelin.longtermtodos.domain.usecase.DeleteTaskUseCase
import com.junelin.longtermtodos.domain.usecase.ExportDataUseCase
import com.junelin.longtermtodos.domain.usecase.GetCategoriesUseCase
import com.junelin.longtermtodos.domain.usecase.GetSettingsUseCase
import com.junelin.longtermtodos.domain.usecase.GetTasksUseCase
import com.junelin.longtermtodos.domain.usecase.ImportDataUseCase
import com.junelin.longtermtodos.domain.usecase.SaveTaskUseCase
import com.junelin.longtermtodos.domain.usecase.ToggleTaskCompletionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetTasksUseCase(taskRepository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(taskRepository)
    }

    @Provides
    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository): GetCategoriesUseCase {
        return GetCategoriesUseCase(categoryRepository)
    }

    @Provides
    fun provideSaveTaskUseCase(
        taskRepository: TaskRepository,
        settingsRepository: SettingsRepository
    ): SaveTaskUseCase {
        return SaveTaskUseCase(taskRepository, settingsRepository)
    }

    @Provides
    fun provideDeleteTaskUseCase(taskRepository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(taskRepository)
    }

    @Provides
    fun provideToggleTaskCompletionUseCase(taskRepository: TaskRepository): ToggleTaskCompletionUseCase {
        return ToggleTaskCompletionUseCase(taskRepository)
    }

    @Provides
    fun provideGetSettingsUseCase(settingsRepository: SettingsRepository): GetSettingsUseCase {
        return GetSettingsUseCase(settingsRepository)
    }

    @Provides
    fun provideExportDataUseCase(
        @ApplicationContext context: Context,
        taskRepository: TaskRepository,
        categoryRepository: CategoryRepository
    ): ExportDataUseCase {
        return ExportDataUseCase(context, taskRepository, categoryRepository)
    }

    @Provides
    fun provideImportDataUseCase(
        @ApplicationContext context: Context,
        taskRepository: TaskRepository,
        categoryRepository: CategoryRepository
    ): ImportDataUseCase {
        return ImportDataUseCase(context, taskRepository, categoryRepository)
    }
}
