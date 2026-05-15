package com.junelin.longtermtodos.data.repository

import com.junelin.longtermtodos.data.local.dao.CategoryDao
import com.junelin.longtermtodos.data.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllFlow().map { list -> list.map { Category.fromEntity(it) } }

    suspend fun getAllCategoriesSync(): List<Category> =
        categoryDao.getAll().map { Category.fromEntity(it) }

    suspend fun getCategoryById(categoryId: Long): Category? =
        categoryDao.getById(categoryId)?.let { Category.fromEntity(it) }

    suspend fun insertCategory(category: Category): Long = categoryDao.insert(category.toEntity())

    suspend fun updateCategory(category: Category) = categoryDao.update(category.toEntity())

    suspend fun deleteCategory(categoryId: Long): Boolean {
        return categoryDao.deleteById(categoryId) > 0
    }

    suspend fun updateSortOrder(categoryId: Long, sortOrder: Int) =
        categoryDao.updateSortOrder(categoryId, sortOrder)
}
