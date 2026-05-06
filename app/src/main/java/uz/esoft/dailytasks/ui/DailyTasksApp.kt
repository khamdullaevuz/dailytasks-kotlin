package uz.esoft.dailytasks.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uz.esoft.dailytasks.ui.navigation.AppDestination
import uz.esoft.dailytasks.ui.navigation.appNavGraph

private data class BottomItem(
    val destination: AppDestination,
    val label: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun DailyTasksApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val bottomItems = listOf(
        BottomItem(
            destination = AppDestination.Today,
            label = "Bugun",
            icon = { Icon(Icons.Outlined.Today, contentDescription = null) },
        ),
        BottomItem(
            destination = AppDestination.Plan,
            label = "Reja",
            icon = { Icon(Icons.AutoMirrored.Outlined.EventNote, contentDescription = null) },
        ),
        BottomItem(
            destination = AppDestination.Stats,
            label = "Statistika",
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
        ),
    )

    val showBottomBar = bottomItems.any { it.destination.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.destination.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate(AppDestination.EditTask.createRoute()) },
                ) {
                    Text("+")
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Today.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            appNavGraph(navController)
        }
    }
}

