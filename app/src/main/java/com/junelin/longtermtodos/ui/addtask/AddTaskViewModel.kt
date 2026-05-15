package com.junelin.longtermtodos.ui.addtask

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import com.junelin.longtermtodos.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val categoryId: Long? = null,
    val dueDate: LocalDate? = null,
    val remindBeforeDays: Int = 3,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class AddTaskViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val taskId: Long? = savedStateHandle.get<Long>("taskId").takeIf { it != 0L }

    private val taskRepository = AppModule.provideTaskRepository(application)
    private val categoryRepository = AppModule.provideCategoryRepository(application)
    private val settingsRepository = AppModule.provideSettingsRepository(application)

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            categories.collect {
                if (_uiState.value.categoryId == null && it.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(categoryId = it.first().id)
                }
            }
        }
        viewModelScope.launch {
            val defaultDays = settingsRepository.defaultRemindDays.first()
            _uiState.value = _uiState.value.copy(remindBeforeDays = defaultDays)
        }
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskByIdSync(id)
            task?.let {
                _uiState.value = _uiState.value.copy(
                    title = it.title,
                    note = it.note ?: "",
                    categoryId = it.categoryId,
                    dueDate = it.dueDate,
                    remindBeforeDays = it.remindBeforeDays
                )
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title, error = null)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun onCategoryChange(categoryId: Long) {
        _uiState.value = _uiState.value.copy(categoryId = categoryId)
    }

    fun onDueDateChange(date: LocalDate?) {
        _uiState.value = _uiState.value.copy(dueDate = date)
    }

    fun onRemindDaysChange(days: Int) {
        _uiState.value = _uiState.value.copy(remindBeforeDays = days)
    }

    fun saveTask() {
        val state = _uiState.value
        when {
            state.title.isBlank() -> {
                _uiState.value = state.copy(error = "请输入标题")
                return
            }
            state.dueDate == null -> {
                _uiState.value = state.copy(error = "请选择到期日期")
                return
            }
            state.categoryId == null -> {
                _uiState.value = state.copy(error = "请选择分类")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            val task = Task(
                id = taskId ?: 0,
                title = state.title.trim(),
                note = state.note.trim().takeIf { it.isNotEmpty() },
                categoryId = state.categoryId!!,
                dueDate = state.dueDate!!,
                remindBeforeDays = state.remindBeforeDays
            )
            if (taskId != null) {
                taskRepository.updateTask(task)
            } else {
                taskRepository.insertTask(task)
            }
            _uiState.value = state.copy(isLoading = false, isSaved = true)
        }
    }
}
