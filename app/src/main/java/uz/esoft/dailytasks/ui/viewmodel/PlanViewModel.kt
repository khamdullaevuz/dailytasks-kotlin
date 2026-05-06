package uz.esoft.dailytasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import java.time.LocalDate

data class PlanUiState(
    val fromDate: LocalDate = LocalDate.now(),
    val planned: List<Task> = emptyList(),
)

class PlanViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val fromDate = LocalDate.now()

    val uiState: StateFlow<PlanUiState> = taskRepository
        .observePlannedTasks(fromDate)
        .map { planned ->
            PlanUiState(
                fromDate = fromDate,
                planned = planned,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlanUiState(fromDate = fromDate),
        )

    fun setCompleted(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.setCompleted(taskId, completed)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteById(taskId)
        }
    }

    fun setReminderIn(taskId: Long, minutes: Long) {
        viewModelScope.launch {
            val remindAt = Instant.now().plusSeconds(minutes * 60)
            taskRepository.setReminderAt(taskId, remindAt)
        }
    }

    fun clearReminder(taskId: Long) {
        viewModelScope.launch {
            taskRepository.clearReminder(taskId)
        }
    }
}

