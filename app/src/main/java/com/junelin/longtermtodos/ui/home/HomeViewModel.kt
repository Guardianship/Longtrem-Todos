package com.junelin.longtermtodos.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.TaskRepository
import com.junelin.longtermtodos.di.AppModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = AppModule.provideTaskRepository(application)
    private val categoryRepository = AppModule.provideCategoryRepository(application)

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    val categories: StateFlow<List<Category>> = MutableStateFlow(emptyList)

    val tasks = combine(
        _selectedCategoryId,
        _searchQuery,
        _isSearchActive
    ) { categoryId, query, isSearch ->
        Triple(categoryId, query, isSearch)
    }.flatMapLatest { (categoryId, query, isSearch) ->
        when {
            isSearch && query.isNotBlank() -> taskRepository.searchTasks(query)
            categoryId == null -> taskRepository.getAllActiveTasks()
            else -> taskRepository.getTasksByCategory(categoryId)
        }
    }.let { flow ->
        val state = MutableStateFlow<List<Task>>(emptyList())
        viewModelScope.launch {
            flow.collect { state.value = it }
        }
        state
    }

    init {
        val catState = MutableStateFlow<List<Category>>(emptyList())
        categories as MutableStateFlow
        (categories as MutableStateFlow).value = catState.value
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect {
                (categories as MutableStateFlow).value = it
            }
        }
    }

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
            taskRepository.setTaskCompleted(taskId, completed)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            _snackbarMessage.value = "待办已删除"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
