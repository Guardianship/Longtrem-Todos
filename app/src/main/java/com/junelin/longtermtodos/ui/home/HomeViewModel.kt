package com.junelin.longtermtodos.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.domain.usecase.DeleteTaskUseCase
import com.junelin.longtermtodos.domain.usecase.GetCategoriesUseCase
import com.junelin.longtermtodos.domain.usecase.GetTasksUseCase
import com.junelin.longtermtodos.domain.usecase.ToggleTaskCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTasksUseCase: GetTasksUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(
        _selectedCategoryId,
        _searchQuery,
        _isSearchActive
    ) { categoryId, query, isSearch ->
        Triple(categoryId, query, isSearch)
    }.flatMapLatest { (categoryId, query, isSearch) ->
        val effectiveQuery = if (isSearch) query else null
        getTasksUseCase(categoryId = categoryId, query = effectiveQuery)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<HomeStats> = combine(tasks, categories) { taskList, catList ->
        val upcoming = taskList.count { !it.isCompleted && it.daysUntil in 0..7 }
        val overdue = taskList.count { !it.isCompleted && it.daysUntil < 0 }
        HomeStats(
            total = taskList.size,
            upcoming = upcoming,
            overdue = overdue,
            completed = taskList.count { it.isCompleted }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeStats())

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun toggleTaskCompletion(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            toggleTaskCompletionUseCase(taskId, completed)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
            _snackbarMessage.value = "待办已删除"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}

data class HomeStats(
    val total: Int = 0,
    val upcoming: Int = 0,
    val overdue: Int = 0,
    val completed: Int = 0
)
