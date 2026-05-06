package uz.esoft.dailytasks.model

import java.time.Instant
import java.time.LocalDate

/**
 * Domain model used by the UI.
 */
data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val createdAt: Instant,
    val completedAt: Instant?,
    val remindAt: Instant?,
    val reminderFiredAt: Instant?,
) {
    val isCompleted: Boolean get() = completedAt != null

    val hasReminder: Boolean get() = remindAt != null
}

