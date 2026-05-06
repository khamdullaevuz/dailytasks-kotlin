package uz.esoft.dailytasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import java.time.LocalDate
import uz.esoft.dailytasks.data.remote.TasksApi
import uz.esoft.dailytasks.data.remote.toEntity

class OfflineTaskRepository(
    private val taskDao: TaskDao,
    private val tasksApi: TasksApi?,
) : TaskRepository {

    override fun observeTasksForDay(day: LocalDate): Flow<List<Task>> {
        return taskDao.observeTasksForDay(day.toEpochDay()).map { list -> list.map { it.toModel() } }
    }

    override fun observeOverdueTasks(today: LocalDate): Flow<List<Task>> {
        return taskDao.observeOverdueTasks(today.toEpochDay()).map { list -> list.map { it.toModel() } }
    }

    override fun observeUnscheduledTasks(): Flow<List<Task>> {
        return taskDao.observeUnscheduledTasks().map { list -> list.map { it.toModel() } }
    }

    override fun observePlannedTasks(from: LocalDate): Flow<List<Task>> {
        return taskDao.observePlannedTasks(from.toEpochDay()).map { list -> list.map { it.toModel() } }
    }

    override suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)?.toModel()

    override suspend fun upsert(task: Task): Long {
        val entity = task.toEntity()
        return if (entity.id == 0L) {
            taskDao.insert(entity)
        } else {
            taskDao.update(entity)
            entity.id
        }
    }

    override suspend fun deleteById(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun refreshFromRemote() {
        val api = tasksApi ?: return
        val remoteTasks = api.getTasks()
        taskDao.insertAll(remoteTasks.map { it.toEntity() })
    }

    override suspend fun setCompleted(id: Long, completed: Boolean, completedAt: Instant) {
        taskDao.setCompletedAt(id, completedAtEpochMs = if (completed) completedAt.toEpochMilli() else null)
    }

    override fun observeNextPendingReminder(): Flow<Task?> {
        return taskDao.observeNextPendingReminder().map { it?.toModel() }
    }

    override suspend fun setReminderAt(taskId: Long, remindAt: Instant?) {
        taskDao.setReminderAt(taskId, remindAtEpochMs = remindAt?.toEpochMilli())
    }

    override suspend fun clearReminder(taskId: Long) {
        taskDao.clearReminder(taskId)
    }

    override suspend fun markReminderFired(taskId: Long, firedAt: Instant) {
        taskDao.setReminderFiredAt(taskId, firedAtEpochMs = firedAt.toEpochMilli())
    }

    override suspend fun snoozeReminder(taskId: Long, minutes: Long) {
        val newTime = Instant.now().plusSeconds(minutes * 60)
        taskDao.setReminderAt(taskId, remindAtEpochMs = newTime.toEpochMilli())
    }

    override fun observeCompletedCountBetween(start: Instant, end: Instant): Flow<Int> {
        return taskDao.observeCompletedCountBetween(start.toEpochMilli(), end.toEpochMilli())
    }

    override fun observePlannedCountBetween(startDay: LocalDate, endDay: LocalDate): Flow<Int> {
        return taskDao.observePlannedCountBetween(startDay.toEpochDay(), endDay.toEpochDay())
    }
}

private fun TaskEntity.toModel(): Task {
    return Task(
        id = id,
        title = title,
        description = description.orEmpty(),
        dueDate = dueDateEpochDay?.let(LocalDate::ofEpochDay),
        createdAt = Instant.ofEpochMilli(createdAtEpochMs),
        completedAt = completedAtEpochMs?.let(Instant::ofEpochMilli),
        remindAt = remindAtEpochMs?.let(Instant::ofEpochMilli),
        reminderFiredAt = reminderFiredAtEpochMs?.let(Instant::ofEpochMilli),
    )
}

private fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title.trim(),
        description = description.trim().ifBlank { null },
        dueDateEpochDay = dueDate?.toEpochDay(),
        createdAtEpochMs = createdAt.toEpochMilli(),
        completedAtEpochMs = completedAt?.toEpochMilli(),
        remindAtEpochMs = remindAt?.toEpochMilli(),
        reminderFiredAtEpochMs = reminderFiredAt?.toEpochMilli(),
    )
}
