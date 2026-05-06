package uz.esoft.dailytasks.ui.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import uz.esoft.dailytasks.MainDispatcherRule
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class EditTaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `new task - save calls repository and returns new id`() = runTest {
        val repo = FakeTaskRepository()
        val vm = EditTaskViewModel(
            taskId = 0L,
            taskRepository = repo,
        )

        vm.onTitleChange("Test task")
        vm.onDescriptionChange("Desc")
        assertTrue(vm.uiState.value.canSave)

        var savedId: Long? = null
        vm.save { id -> savedId = id }

        advanceUntilIdle()

        assertEquals(1L, savedId)
        assertEquals("Test task", repo.lastUpserted?.title)
    }
}

private class FakeTaskRepository : TaskRepository {

    var lastUpserted: Task? = null

    override fun observeTasksForDay(day: LocalDate): Flow<List<Task>> = flowOf(emptyList())
    override fun observeOverdueTasks(today: LocalDate): Flow<List<Task>> = flowOf(emptyList())
    override fun observeUnscheduledTasks(): Flow<List<Task>> = flowOf(emptyList())
    override fun observePlannedTasks(from: LocalDate): Flow<List<Task>> = flowOf(emptyList())

    override suspend fun getTaskById(id: Long): Task? = null

    override suspend fun upsert(task: Task): Long {
        lastUpserted = task
        return if (task.id == 0L) 1L else task.id
    }

    override suspend fun deleteById(id: Long) = Unit

    override suspend fun setCompleted(id: Long, completed: Boolean, completedAt: Instant) = Unit

    override fun observeNextPendingReminder(): Flow<Task?> = flowOf(null)

    override suspend fun setReminderAt(taskId: Long, remindAt: Instant?) = Unit

    override suspend fun clearReminder(taskId: Long) = Unit

    override suspend fun markReminderFired(taskId: Long, firedAt: Instant) = Unit

    override suspend fun snoozeReminder(taskId: Long, minutes: Long) = Unit

    override fun observeCompletedCountBetween(start: Instant, end: Instant): Flow<Int> = flowOf(0)

    override fun observePlannedCountBetween(startDay: LocalDate, endDay: LocalDate): Flow<Int> = flowOf(0)
}

