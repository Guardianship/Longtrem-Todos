package com.junelin.longtermtodos.ui.addtask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.domain.usecase.GetCategoriesUseCase
import com.junelin.longtermtodos.domain.usecase.GetTasksUseCase
import com.junelin.longtermtodos.domain.usecase.SaveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddTaskUiState(
    val title: String = "",
    val note: String = "",
    val categoryId: Long? = null,
    val dueDate: LocalDate? = null,
    val isLunarDate: Boolean = false,
    val remindBeforeDays: Int = 3,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val taskId: Long? = savedStateHandle.get<Long>("taskId")?.takeIf { it != 0L }

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState

    val categories: StateFlow<List<com.junelin.longtermtodos.data.model.Category>> = getCategoriesUseCase()
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
            val defaultDays = saveTaskUseCase.getDefaultRemindDays()
            _uiState.value = _uiState.value.copy(remindBeforeDays = defaultDays)
        }
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            val task = getTasksUseCase.getById(id)
            task?.let {
                _uiState.value = _uiState.value.copy(
                    title = it.title,
                    note = it.note ?: "",
                    categoryId = it.categoryId,
                    dueDate = it.dueDate,
                    isLunarDate = it.isLunarDate,
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

    fun onLunarToggle(isLunar: Boolean) {
        _uiState.value = _uiState.value.copy(isLunarDate = isLunar)
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
                isLunarDate = state.isLunarDate,
                remindBeforeDays = state.remindBeforeDays
            )
            saveTaskUseCase(task)
            _uiState.value = state.copy(isLoading = false, isSaved = true)
        }
    }
}
