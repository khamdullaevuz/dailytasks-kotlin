package uz.esoft.dailytasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import java.time.LocalDate

data class TodayUiState(
    val date: LocalDate = LocalDate.now(),
    val overdue: List<Task> = emptyList(),
    val today: List<Task> = emptyList(),
    val unscheduled: List<Task> = emptyList(),
)

class TodayViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TodayUiState> = selectedDate
        .flatMapLatest { date ->
            combine(
                taskRepository.observeOverdueTasks(date),
                taskRepository.observeTasksForDay(date),
                taskRepository.observeUnscheduledTasks(),
            ) { overdue, today, unscheduled ->
                TodayUiState(
                    date = date,
                    overdue = overdue,
                    today = today,
                    unscheduled = unscheduled,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TodayUiState(),
        )

    fun setDate(date: LocalDate) {
        selectedDate.value = date
    }

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

