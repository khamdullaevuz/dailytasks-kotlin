package uz.esoft.dailytasks.data.remote

import uz.esoft.dailytasks.data.TaskEntity
import java.time.Instant

/**
 * Mock API DTO. Epoch values are used to keep parsing minimal.
 */
data class TaskDto(
    val id: Long,
    val title: String,
    val description: String?,
    val dueDateEpochDay: Long?,
    val createdAtEpochMs: Long?,
    val completedAtEpochMs: Long?,
    val remindAtEpochMs: Long?,
    val reminderFiredAtEpochMs: Long?,
)

fun TaskDto.toEntity(now: Instant = Instant.now()): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dueDateEpochDay = dueDateEpochDay,
        createdAtEpochMs = createdAtEpochMs ?: now.toEpochMilli(),
        completedAtEpochMs = completedAtEpochMs,
        remindAtEpochMs = remindAtEpochMs,
        reminderFiredAtEpochMs = reminderFiredAtEpochMs,
    )
}
