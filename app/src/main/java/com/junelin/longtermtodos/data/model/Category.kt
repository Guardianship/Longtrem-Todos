package com.junelin.longtermtodos.data.model

import com.junelin.longtermtodos.data.local.entity.CategoryEntity

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String,
    val sortOrder: Int = 0,
    val isPreset: Boolean = false
) {
    fun toEntity(): CategoryEntity = CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color,
        sortOrder = sortOrder,
        isPreset = isPreset
    )

    companion object {
        fun fromEntity(entity: CategoryEntity): Category = Category(
            id = entity.id,
            name = entity.name,
            icon = entity.icon,
            color = entity.color,
            sortOrder = entity.sortOrder,
            isPreset = entity.isPreset
        )
    }
}
