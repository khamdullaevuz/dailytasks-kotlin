package uz.esoft.dailytasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import java.time.LocalDate

data class EditTaskUiState(
    val taskId: Long = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate? = null,
    val remindAt: Instant? = null,
    val reminderFiredAt: Instant? = null,
    val isLoading: Boolean = false,
) {
    val canSave: Boolean get() = title.isNotBlank()
}

class EditTaskViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState(taskId = taskId, isLoading = taskId != 0L))
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    private var loadedTask: Task? = null

    init {
        if (taskId != 0L) {
            viewModelScope.launch {
                val task = taskRepository.getTaskById(taskId)
                loadedTask = task
                _uiState.update {
                    it.copy(
                        taskId = task?.id ?: 0L,
                        title = task?.title.orEmpty(),
                        description = task?.description.orEmpty(),
                        dueDate = task?.dueDate,
                        remindAt = task?.remindAt,
                        reminderFiredAt = task?.reminderFiredAt,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onDueDateChange(value: LocalDate?) {
        _uiState.update { it.copy(dueDate = value) }
    }

    fun setReminderIn(minutes: Long) {
        val newTime = Instant.now().plusSeconds(minutes * 60)
        _uiState.update { it.copy(remindAt = newTime, reminderFiredAt = null) }
    }

    fun clearReminder() {
        _uiState.update { it.copy(remindAt = null, reminderFiredAt = null) }
    }

    fun save(onSaved: (Long) -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            val existing = loadedTask
            val task = Task(
                id = state.taskId,
                title = state.title,
                description = state.description,
                dueDate = state.dueDate,
                createdAt = existing?.createdAt ?: Instant.now(),
                completedAt = existing?.completedAt,
                remindAt = state.remindAt,
                reminderFiredAt = state.reminderFiredAt,
            )
            val id = taskRepository.upsert(task)
            onSaved(id)
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = _uiState.value.taskId
        if (id == 0L) return

        viewModelScope.launch {
            taskRepository.deleteById(id)
            onDeleted()
        }
    }

    companion object {
        const val ARG_TASK_ID = "taskId"

        fun provideFactory(
            taskRepository: TaskRepository,
            taskId: Long,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(EditTaskViewModel::class.java)) {
                        return EditTaskViewModel(
                            taskId = taskId,
                            taskRepository = taskRepository,
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }
}

