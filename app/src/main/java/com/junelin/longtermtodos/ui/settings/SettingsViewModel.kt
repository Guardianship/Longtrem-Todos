package com.junelin.longtermtodos.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.model.Category
import com.junelin.longtermtodos.data.repository.CategoryRepository
import com.junelin.longtermtodos.domain.usecase.ExportDataUseCase
import com.junelin.longtermtodos.domain.usecase.GetSettingsUseCase
import com.junelin.longtermtodos.domain.usecase.ImportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val widgetDisplayDays: Int = 14,
    val defaultRemindDays: Int = 3,
    val autoExtractSms: Boolean = true,
    val autoExtractWechat: Boolean = true,
    val biometricLock: Boolean = false,
    val themeColor: String? = null,
    val darkMode: String = "system",
    val dynamicColor: Boolean = false,
    val categories: List<Category> = emptyList(),
    val message: String? = null,
    val exportJson: String? = null,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val categoryRepository: CategoryRepository,
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                getSettingsUseCase.widgetDisplayDays,
                getSettingsUseCase.defaultRemindDays,
                getSettingsUseCase.autoExtractSms,
                getSettingsUseCase.autoExtractWechat,
                getSettingsUseCase.biometricLock,
                getSettingsUseCase.themeColor,
                getSettingsUseCase.darkMode,
                getSettingsUseCase.dynamicColor,
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
                    darkMode = values[6] as String,
                    dynamicColor = values[7] as Boolean,
                    categories = values[8] as List<Category>
                )
            }.collect {
                _uiState.value = _uiState.value.copy(
                    widgetDisplayDays = it.widgetDisplayDays,
                    defaultRemindDays = it.defaultRemindDays,
                    autoExtractSms = it.autoExtractSms,
                    autoExtractWechat = it.autoExtractWechat,
                    biometricLock = it.biometricLock,
                    themeColor = it.themeColor,
                    darkMode = it.darkMode,
                    dynamicColor = it.dynamicColor,
                    categories = it.categories
                )
            }
        }
    }

    fun setWidgetDisplayDays(days: Int) {
        viewModelScope.launch { getSettingsUseCase.setWidgetDisplayDays(days) }
    }

    fun setDefaultRemindDays(days: Int) {
        viewModelScope.launch { getSettingsUseCase.setDefaultRemindDays(days) }
    }

    fun setAutoExtractSms(enabled: Boolean) {
        viewModelScope.launch { getSettingsUseCase.setAutoExtractSms(enabled) }
    }

    fun setAutoExtractWechat(enabled: Boolean) {
        viewModelScope.launch { getSettingsUseCase.setAutoExtractWechat(enabled) }
    }

    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch { getSettingsUseCase.setBiometricLock(enabled) }
    }

    fun setThemeColor(color: String) {
        viewModelScope.launch { getSettingsUseCase.setThemeColor(color) }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch { getSettingsUseCase.setDarkMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { getSettingsUseCase.setDynamicColor(enabled) }
    }

    fun updateCategorySort(categories: List<Category>) {
        viewModelScope.launch {
            categories.forEachIndexed { index, category ->
                categoryRepository.updateSortOrder(category.id, index)
            }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            try {
                val json = exportDataUseCase()
                _uiState.value = _uiState.value.copy(exportJson = json, isExporting = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isExporting = false)
                showMessage("导出失败: ${e.message}")
            }
        }
    }

    fun clearExportJson() {
        _uiState.value = _uiState.value.copy(exportJson = null)
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true)
            try {
                val result = importDataUseCase(uri)
                result.onSuccess { count ->
                    _uiState.value = _uiState.value.copy(isImporting = false)
                    showMessage("导入成功，共导入 $count 条待办")
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(isImporting = false)
                    showMessage("导入失败: ${e.message}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isImporting = false)
                showMessage("导入失败: ${e.message}")
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
