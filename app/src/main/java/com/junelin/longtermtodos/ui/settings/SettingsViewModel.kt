package com.junelin.longtermtodos.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.data.repository.SettingsRepository
import com.junelin.longtermtodos.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SettingsUiState(
    val widgetDisplayDays: Int = 14,
    val defaultRemindDays: Int = 3,
    val autoExtractSms: Boolean = true,
    val autoExtractWechat: Boolean = true,
    val biometricLock: Boolean = false,
    val themeColor: String? = null,
    val categories: List<Category> = emptyList(),
    val message: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = AppModule.provideSettingsRepository(application)
    private val categoryRepository = AppModule.provideCategoryRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.widgetDisplayDays,
                settingsRepository.defaultRemindDays,
                settingsRepository.autoExtractSms,
                settingsRepository.autoExtractWechat,
                settingsRepository.biometricLock,
                settingsRepository.themeColor,
                categoryRepository.getAllCategories()
            ) { values ->
                @Suppress("UNCHECKED_CAST")
                SettingsUiState(
                    widgetDisplayDays = values[0] as Int,
                    defaultRemindDays = values[1] as Int,
                    autoExtractSms = values[2] as Boolean,
                    autoExtractWechat = values[3] as Boolean,
                    biometricLock = values[4] as Boolean,
                    themeColor = values[5] as String?,
                    categories = values[6] as List<Category>
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setWidgetDisplayDays(days: Int) {
        viewModelScope.launch {
            settingsRepository.setWidgetDisplayDays(days)
        }
    }

    fun setDefaultRemindDays(days: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultRemindDays(days)
        }
    }

    fun setAutoExtractSms(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoExtractSms(enabled)
        }
    }

    fun setAutoExtractWechat(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoExtractWechat(enabled)
        }
    }

    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricLock(enabled)
        }
    }

    fun setThemeColor(color: String) {
        viewModelScope.launch {
            settingsRepository.setThemeColor(color)
        }
    }

    fun updateCategorySort(categories: List<Category>) {
        viewModelScope.launch {
            categories.forEachIndexed { index, category ->
                categoryRepository.updateSortOrder(category.id, index)
            }
        }
    }

    fun showMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
