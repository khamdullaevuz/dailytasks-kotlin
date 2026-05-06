package uz.esoft.dailytasks.data

import androidx.room.Dao
import androidx.room.Insert
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

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE tasks SET completedAtEpochMs = :completedAtEpochMs WHERE id = :id")
    suspend fun setCompletedAt(id: Long, completedAtEpochMs: Long?)

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

