package com.junelin.longtermtodos.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.junelin.longtermtodos.data.model.Category

@Composable
fun CategoryTabRow(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CategoryChip(
                label = "全部",
                icon = "",
                color = MaterialTheme.colorScheme.primary,
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )
        }

        items(categories, key = { it.id }) { category ->
            CategoryChip(
                label = category.name,
                icon = category.icon,
                color = parseColor(category.color),
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    icon: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = if (icon.isNotEmpty()) "$icon $label" else label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.15f),
            selectedLabelColor = color,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = if (selected) color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }
}
