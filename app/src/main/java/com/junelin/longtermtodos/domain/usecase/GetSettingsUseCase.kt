package com.junelin.longtermtodos.domain.usecase

import com.junelin.longtermtodos.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    val widgetDisplayDays: Flow<Int> = settingsRepository.widgetDisplayDays
    val defaultRemindDays: Flow<Int> = settingsRepository.defaultRemindDays
    val autoExtractSms: Flow<Boolean> = settingsRepository.autoExtractSms
    val autoExtractWechat: Flow<Boolean> = settingsRepository.autoExtractWechat
    val biometricLock: Flow<Boolean> = settingsRepository.biometricLock
    val themeColor: Flow<String?> = settingsRepository.themeColor
    val darkMode: Flow<String> = settingsRepository.darkMode
    val dynamicColor: Flow<Boolean> = settingsRepository.dynamicColor

    suspend fun setWidgetDisplayDays(days: Int) = settingsRepository.setWidgetDisplayDays(days)
    suspend fun setDefaultRemindDays(days: Int) = settingsRepository.setDefaultRemindDays(days)
    suspend fun setAutoExtractSms(enabled: Boolean) = settingsRepository.setAutoExtractSms(enabled)
    suspend fun setAutoExtractWechat(enabled: Boolean) = settingsRepository.setAutoExtractWechat(enabled)
    suspend fun setBiometricLock(enabled: Boolean) = settingsRepository.setBiometricLock(enabled)
    suspend fun setThemeColor(color: String) = settingsRepository.setThemeColor(color)
    suspend fun setDarkMode(mode: String) = settingsRepository.setDarkMode(mode)
    suspend fun setDynamicColor(enabled: Boolean) = settingsRepository.setDynamicColor(enabled)
}
