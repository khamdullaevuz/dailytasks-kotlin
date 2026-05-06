package uz.esoft.dailytasks.di

import android.content.Context
import uz.esoft.dailytasks.data.DailyTasksDatabase
import uz.esoft.dailytasks.data.OfflineTaskRepository
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.data.remote.RetrofitProvider
import uz.esoft.dailytasks.reminder.ReminderEngine

class AppContainer(context: Context) {
    private val database: DailyTasksDatabase = DailyTasksDatabase.getDatabase(context)

    private val tasksApi = RetrofitProvider.createMockTasksApi()

    val taskRepository: TaskRepository = OfflineTaskRepository(
        taskDao = database.taskDao(),
        tasksApi = tasksApi,
    )

    /** In-app only reminders (no background services). */
    val reminderEngine: ReminderEngine = ReminderEngine(taskRepository)
}

