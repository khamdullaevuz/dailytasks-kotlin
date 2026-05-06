package uz.esoft.dailytasks.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.esoft.dailytasks.ui.components.TaskListItem
import uz.esoft.dailytasks.ui.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel,
    onEditTask: (Long) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = modifier) {
        item {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Outlined.Menu, contentDescription = "Menyu")
                    }
                },
                title = { Text("Rejalashtirilgan vazifalar") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Menyu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ma'lumotlarni yangilash (mock API)") },
                            onClick = {
                                menuExpanded = false
                                viewModel.refreshFromRemote()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Eslatma: faqat ilova ichida") },
                            onClick = { menuExpanded = false },
                        )
                    }
                },
            )
        }

        if (state.planned.isEmpty()) {
            item {
                Text(
                    text = "Hozircha rejalashtirilgan vazifalar yo‘q.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        items(
            items = state.planned,
            key = { it.id },
        ) { task ->
            TaskListItem(
                task = task,
                onClick = onEditTask,
                onCheckedChange = viewModel::setCompleted,
                onSetReminderInMinutes = viewModel::setReminderIn,
                onClearReminder = viewModel::clearReminder,
                onDelete = viewModel::deleteTask,
            )
        }
    }
}
