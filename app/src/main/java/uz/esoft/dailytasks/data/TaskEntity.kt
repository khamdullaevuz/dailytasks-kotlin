package uz.esoft.dailytasks.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["dueDateEpochDay"]),
        Index(value = ["completedAtEpochMs"]),
    ],
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    /** Nullable -> task is not planned for a specific day yet. */
    val dueDateEpochDay: Long?,
    val createdAtEpochMs: Long,
    /** Nullable -> task is not completed yet. */
    val completedAtEpochMs: Long?,
)

