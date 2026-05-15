package com.junelin.longtermtodos.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.junelin.longtermtodos.data.local.entity.ExtractionStatus
import com.junelin.longtermtodos.data.model.ExtractedEvent
import com.junelin.longtermtodos.data.model.Task
import com.junelin.longtermtodos.data.local.dao.ExtractedEventDao
import com.junelin.longtermtodos.data.repository.TaskRepository
import com.junelin.longtermtodos.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExtractedEventViewModel(application: Application) : AndroidViewModel(application) {

    private val extractedEventDao = AppModule.provideDatabase(application).extractedEventDao()
    private val taskRepository = AppModule.provideTaskRepository(application)

    private val _pendingEvents = MutableStateFlow<List<ExtractedEvent>>(emptyList())
    val pendingEvents: StateFlow<List<ExtractedEvent>> = _pendingEvents

    private val _currentEvent = MutableStateFlow<ExtractedEvent?>(null)
    val currentEvent: StateFlow<ExtractedEvent?> = _currentEvent

    init {
        viewModelScope.launch {
            extractedEventDao.getPendingFlow().collect { entities ->
                _pendingEvents.value = entities.map { ExtractedEvent.fromEntity(it) }
            }
        }
    }

    fun showNextEvent() {
        _currentEvent.value = _pendingEvents.value.firstOrNull()
    }

    fun dismissCurrentEvent() {
        _currentEvent.value = null
    }

    fun acceptEvent(event: ExtractedEvent) {
        viewModelScope.launch {
            extractedEventDao.updateStatus(event.id, ExtractionStatus.ACCEPTED.name)
            val task = Task(
                title = event.extractedTitle,
                categoryId = event.inferredCategoryId ?: 1,
                dueDate = event.extractedDate ?: java.time.LocalDate.now().plusDays(7),
                source = when (event.source) {
                    com.junelin.longtermtodos.data.local.entity.EventSource.SMS ->
                        com.junelin.longtermtodos.data.local.entity.TaskSource.AUTO_SMS
                    com.junelin.longtermtodos.data.local.entity.EventSource.WECHAT ->
                        com.junelin.longtermtodos.data.local.entity.TaskSource.AUTO_WECHAT
                }
            )
            taskRepository.insertTask(task)
            dismissCurrentEvent()
        }
    }

    fun editEvent(event: ExtractedEvent, newTitle: String, newDate: java.time.LocalDate, categoryId: Long) {
        viewModelScope.launch {
            extractedEventDao.updateStatus(event.id, ExtractionStatus.EDITED.name)
            val task = Task(
                title = newTitle,
                categoryId = categoryId,
                dueDate = newDate,
                source = when (event.source) {
                    com.junelin.longtermtodos.data.local.entity.EventSource.SMS ->
                        com.junelin.longtermtodos.data.local.entity.TaskSource.AUTO_SMS
                    com.junelin.longtermtodos.data.local.entity.EventSource.WECHAT ->
                        com.junelin.longtermtodos.data.local.entity.TaskSource.AUTO_WECHAT
                }
            )
            taskRepository.insertTask(task)
            dismissCurrentEvent()
        }
    }

    fun ignoreEvent(event: ExtractedEvent) {
        viewModelScope.launch {
            extractedEventDao.updateStatus(event.id, ExtractionStatus.IGNORED.name)
            dismissCurrentEvent()
        }
    }
}
