package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getAllCategories()
    }

    suspend fun getById(categoryId: Long): Category? {
        return categoryRepository.getCategoryById(categoryId)
    }
}
