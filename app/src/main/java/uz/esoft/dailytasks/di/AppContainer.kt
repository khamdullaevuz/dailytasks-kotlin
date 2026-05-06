package uz.esoft.dailytasks.di

import android.content.Context
import uz.esoft.dailytasks.data.DailyTasksDatabase
import uz.esoft.dailytasks.data.OfflineTaskRepository
import uz.esoft.dailytasks.data.TaskRepository
import uz.esoft.dailytasks.reminder.ReminderEngine

class AppContainer(context: Context) {
    private val database: DailyTasksDatabase = DailyTasksDatabase.getDatabase(context)

    val taskRepository: TaskRepository = OfflineTaskRepository(database.taskDao())

    /** In-app only reminders (no background services). */
    val reminderEngine: ReminderEngine = ReminderEngine(taskRepository)
}

