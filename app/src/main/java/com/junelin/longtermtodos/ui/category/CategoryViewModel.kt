package com.junelin.longtermtodos.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository = AppModule.provideCategoryRepository(application)

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun addCategory(name: String, icon: String, color: String) {
        viewModelScope.launch {
            val newCategory = Category(
                name = name,
                icon = icon,
                color = color,
                sortOrder = categories.value.size
            )
            categoryRepository.insertCategory(newCategory)
            _message.value = "分类 \"$name\" 已添加"
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
            _message.value = "分类已更新"
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            if (category.isPreset) {
                _message.value = "预置分类不可删除"
                return@launch
            }
            val success = categoryRepository.deleteCategory(category.id)
            if (success) {
                _message.value = "分类 \"${category.name}\" 已删除"
            } else {
                _message.value = "删除失败"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
