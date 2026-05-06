package uz.esoft.dailytasks.reminder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.model.Task
import java.time.Instant
import kotlin.math.max

/**
 * In-app only reminder engine.
 *
 * IMPORTANT: It does NOT use WorkManager/AlarmManager/services, so it will fire reminders only
 * while the app process is alive (typically while the app is in foreground).
 */
class ReminderEngine(
    private val taskRepository: TaskRepository,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _events = MutableSharedFlow<ReminderEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ReminderEvent> = _events.asSharedFlow()

    private var observeJob: Job? = null

    fun start() {
        if (observeJob != null) return

        observeJob = scope.launch {
            taskRepository.observeNextPendingReminder().collectLatest { task: Task? ->
                if (task == null) return@collectLatest
                val remindAt = task.remindAt ?: return@collectLatest

                val nowMs = Instant.now().toEpochMilli()
                val targetMs = remindAt.toEpochMilli()
                val waitMs = max(0L, targetMs - nowMs)

                if (waitMs > 0L) {
                    delay(waitMs)
                }

                // Mark fired immediately to avoid re-emitting if UI is slow.
                taskRepository.markReminderFired(task.id)

                _events.tryEmit(
                    ReminderEvent(
                        taskId = task.id,
                        title = task.title,
                    ),
                )
            }
        }
    }

    fun stop() {
        observeJob?.cancel()
        observeJob = null
    }

    fun close() {
        stop()
        scope.cancel()
    }
}

data class ReminderEvent(
    val taskId: Long,
    val title: String,
)

