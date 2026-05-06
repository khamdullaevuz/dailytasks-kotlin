package uz.esoft.dailytasks.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import uz.esoft.dailytasks.ui.viewmodel.EditTaskViewModel

sealed class AppDestination(val route: String) {
    data object Today : AppDestination("today")
    data object Plan : AppDestination("plan")
    data object Stats : AppDestination("stats")

    data object EditTask : AppDestination("edit?${EditTaskViewModel.ARG_TASK_ID}={${EditTaskViewModel.ARG_TASK_ID}}") {
        fun createRoute(taskId: Long = 0L): String = "edit?${EditTaskViewModel.ARG_TASK_ID}=$taskId"

        val arguments = listOf(
            navArgument(EditTaskViewModel.ARG_TASK_ID) {
                type = NavType.LongType
                defaultValue = 0L
            },
        )
    }
}

