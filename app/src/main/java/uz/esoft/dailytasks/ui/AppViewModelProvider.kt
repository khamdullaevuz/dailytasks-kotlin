package uz.esoft.dailytasks.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import uz.esoft.dailytasks.DailyTasksApplication
import uz.esoft.dailytasks.ui.viewmodel.PlanViewModel
import uz.esoft.dailytasks.ui.viewmodel.StatsViewModel
import uz.esoft.dailytasks.ui.viewmodel.TodayViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DailyTasksApplication
            TodayViewModel(application.container.taskRepository)
        }
        initializer {
            val application =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DailyTasksApplication
            PlanViewModel(application.container.taskRepository)
        }
        initializer {
            val application =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DailyTasksApplication
            StatsViewModel(application.container.taskRepository)
        }
    }
}

