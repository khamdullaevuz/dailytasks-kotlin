package uz.esoft.dailytasks.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.coroutines.launch
import uz.esoft.dailytasks.DailyTasksApplication
import uz.esoft.dailytasks.reminder.ReminderEvent
import uz.esoft.dailytasks.ui.navigation.AppDestination
import uz.esoft.dailytasks.ui.navigation.appNavGraph
import androidx.compose.material3.rememberDrawerState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp

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

    val context = LocalContext.current.applicationContext as DailyTasksApplication
    val taskRepository = context.container.taskRepository
    val reminderEngine = context.container.reminderEngine

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, reminderEngine) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> reminderEngine.start()
                Lifecycle.Event.ON_STOP -> reminderEngine.stop()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var reminderQueue by remember { mutableStateOf(listOf<ReminderEvent>()) }
    val scope = rememberCoroutineScope()

    DisposableEffect(reminderEngine) {
        val job = scope.launch {
            reminderEngine.events.collect { event ->
                reminderQueue = reminderQueue + event
            }
        }
        onDispose { job.cancel() }
    }

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

    val showMainActions = bottomItems.any { it.destination.route == currentDestination?.route }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val openDrawer: () -> Unit = {
        scope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        text = "DailyTasks",
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Menyu",
                    )
                }

                bottomItems.forEach { item ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == item.destination.route } == true

                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = item.icon,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    )
                }
            }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                if (showMainActions) {
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
                appNavGraph(
                    navController = navController,
                    onOpenDrawer = openDrawer,
                )
            }
        }
    }

    // In-app reminder dialog (works only while app is open; no background services).
    val activeReminder = reminderQueue.firstOrNull()
    if (activeReminder != null) {
        AlertDialog(
            onDismissRequest = { reminderQueue = reminderQueue.drop(1) },
            title = { Text("Eslatma") },
            text = { Text("Vaqti keldi: ${activeReminder.title}") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reminderQueue = reminderQueue.drop(1)
                        navController.navigate(AppDestination.EditTask.createRoute(activeReminder.taskId))
                    },
                ) {
                    Text("Ochish")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch { taskRepository.snoozeReminder(activeReminder.taskId, minutes = 5) }
                        reminderQueue = reminderQueue.drop(1)
                    },
                ) {
                    Text("5 daq. kechiktirish")
                }
            },
        )
    }
}

