package uz.esoft.dailytasks.ui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import uz.esoft.dailytasks.DailyTasksApplication
import uz.esoft.dailytasks.ui.AppViewModelProvider
import uz.esoft.dailytasks.ui.screens.EditTaskScreen
import uz.esoft.dailytasks.ui.screens.PlanScreen
import uz.esoft.dailytasks.ui.screens.StatsScreen
import uz.esoft.dailytasks.ui.screens.TodayScreen
import uz.esoft.dailytasks.ui.viewmodel.EditTaskViewModel
import uz.esoft.dailytasks.ui.viewmodel.PlanViewModel
import uz.esoft.dailytasks.ui.viewmodel.StatsViewModel
import uz.esoft.dailytasks.ui.viewmodel.TodayViewModel

fun NavGraphBuilder.appNavGraph(
    navController: NavController,
    onOpenDrawer: () -> Unit,
) {
    composable(route = AppDestination.Today.route) {
        val vm: TodayViewModel = viewModel(factory = AppViewModelProvider.Factory)
        TodayScreen(
            viewModel = vm,
            onEditTask = { id -> navController.navigate(AppDestination.EditTask.createRoute(id)) },
            onOpenDrawer = onOpenDrawer,
        )
    }

    composable(route = AppDestination.Plan.route) {
        val vm: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
        PlanScreen(
            viewModel = vm,
            onEditTask = { id -> navController.navigate(AppDestination.EditTask.createRoute(id)) },
            onOpenDrawer = onOpenDrawer,
        )
    }

    composable(route = AppDestination.Stats.route) {
        val vm: StatsViewModel = viewModel(factory = AppViewModelProvider.Factory)
        StatsScreen(
            viewModel = vm,
            onOpenDrawer = onOpenDrawer,
        )
    }

    composable(
        route = AppDestination.EditTask.route,
        arguments = AppDestination.EditTask.arguments,
    ) {
        val context = LocalContext.current.applicationContext as DailyTasksApplication
        val taskId = it.arguments?.getLong(EditTaskViewModel.ARG_TASK_ID) ?: 0L
        val vm: EditTaskViewModel = viewModel(
            factory = EditTaskViewModel.provideFactory(
                taskRepository = context.container.taskRepository,
                taskId = taskId,
            ),
        )
        EditTaskScreen(
            viewModel = vm,
            onFinished = { navController.popBackStack() },
        )
    }
}

