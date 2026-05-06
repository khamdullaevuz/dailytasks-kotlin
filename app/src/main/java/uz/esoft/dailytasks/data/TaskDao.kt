package uz.esoft.dailytasks.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAtEpochMs IS NULL
          AND dueDateEpochDay = :epochDay
        ORDER BY createdAtEpochMs DESC
        """,
    )
    fun observeTasksForDay(epochDay: Long): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAtEpochMs IS NULL
          AND dueDateEpochDay IS NULL
        ORDER BY createdAtEpochMs DESC
        """,
    )
    fun observeUnscheduledTasks(): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAtEpochMs IS NULL
          AND dueDateEpochDay < :todayEpochDay
        ORDER BY dueDateEpochDay ASC
        """,
    )
    fun observeOverdueTasks(todayEpochDay: Long): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAtEpochMs IS NULL
          AND dueDateEpochDay >= :fromEpochDay
        ORDER BY dueDateEpochDay ASC
        """,
    )
    fun observePlannedTasks(fromEpochDay: Long): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE id = :id
        LIMIT 1
        """,
    )
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET completedAtEpochMs = :completedAtEpochMs WHERE id = :id")
    suspend fun setCompletedAt(id: Long, completedAtEpochMs: Long?)

    @Query(
        """
        SELECT * FROM tasks
        WHERE completedAtEpochMs IS NULL
          AND remindAtEpochMs IS NOT NULL
          AND reminderFiredAtEpochMs IS NULL
        ORDER BY remindAtEpochMs ASC
        LIMIT 1
        """,
    )
    fun observeNextPendingReminder(): Flow<TaskEntity?>

    @Query("UPDATE tasks SET reminderFiredAtEpochMs = :firedAtEpochMs WHERE id = :id")
    suspend fun setReminderFiredAt(id: Long, firedAtEpochMs: Long?)

    @Query("UPDATE tasks SET remindAtEpochMs = :remindAtEpochMs, reminderFiredAtEpochMs = NULL WHERE id = :id")
    suspend fun setReminderAt(id: Long, remindAtEpochMs: Long?)

    @Query("UPDATE tasks SET remindAtEpochMs = NULL, reminderFiredAtEpochMs = NULL WHERE id = :id")
    suspend fun clearReminder(id: Long)

    @Query(
        """
        SELECT COUNT(*) FROM tasks
        WHERE completedAtEpochMs IS NOT NULL
          AND completedAtEpochMs BETWEEN :startEpochMs AND :endEpochMs
        """,
    )
    fun observeCompletedCountBetween(startEpochMs: Long, endEpochMs: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM tasks
        WHERE dueDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
        """,
    )
    fun observePlannedCountBetween(startEpochDay: Long, endEpochDay: Long): Flow<Int>
}

