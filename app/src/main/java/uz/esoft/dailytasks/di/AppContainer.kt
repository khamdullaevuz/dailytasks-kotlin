package uz.esoft.dailytasks.di

import android.content.Context
import uz.esoft.dailytasks.data.DailyTasksDatabase
import uz.esoft.dailytasks.data.OfflineTaskRepository
import uz.esoft.dailytasks.data.TaskRepository

class AppContainer(context: Context) {
    private val database: DailyTasksDatabase = DailyTasksDatabase.getDatabase(context)

    val taskRepository: TaskRepository = OfflineTaskRepository(database.taskDao())
}

