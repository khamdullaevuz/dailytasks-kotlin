package uz.esoft.dailytasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import uz.esoft.dailytasks.data.TaskRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class StatsUiState(
    val completedLast7Days: Int = 0,
    val plannedLast7Days: Int = 0,
)

class StatsViewModel(
    taskRepository: TaskRepository,
) : ViewModel() {

    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val today: LocalDate = LocalDate.now(zoneId)

    private val startDay: LocalDate = today.minusDays(6)
    private val endDay: LocalDate = today

    private val startInstant: Instant = startDay.atStartOfDay(zoneId).toInstant()
    private val endInstant: Instant = endDay.plusDays(1).atStartOfDay(zoneId).toInstant().minusMillis(1)

    val uiState: StateFlow<StatsUiState> = combine(
        taskRepository.observeCompletedCountBetween(startInstant, endInstant),
        taskRepository.observePlannedCountBetween(startDay, endDay),
    ) { completed, planned ->
        StatsUiState(
            completedLast7Days = completed,
            plannedLast7Days = planned,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsUiState(),
    )
}

