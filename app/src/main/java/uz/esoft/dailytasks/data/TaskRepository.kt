package uz.esoft.dailytasks.data

import kotlinx.coroutines.flow.Flow
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import java.time.LocalDate

interface TaskRepository {
    fun observeTasksForDay(day: LocalDate): Flow<List<Task>>
    fun observeOverdueTasks(today: LocalDate): Flow<List<Task>>
    fun observeUnscheduledTasks(): Flow<List<Task>>
    fun observePlannedTasks(from: LocalDate): Flow<List<Task>>

    suspend fun getTaskById(id: Long): Task?
    suspend fun upsert(task: Task): Long
    suspend fun deleteById(id: Long)

    suspend fun setCompleted(id: Long, completed: Boolean, completedAt: Instant = Instant.now())

    fun observeCompletedCountBetween(start: Instant, end: Instant): Flow<Int>
    fun observePlannedCountBetween(startDay: LocalDate, endDay: LocalDate): Flow<Int>
}

