package uz.esoft.dailytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.esoft.dailytasks.model.Task
import uz.esoft.dailytasks.ui.components.TaskListItem
import uz.esoft.dailytasks.ui.viewmodel.TodayViewModel
import java.time.format.DateTimeFormatter

private val headerDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier,
    ) {
        item {
            TopAppBar(
                title = {
                    Text(
                        text = "Bugun: ${headerDateFormatter.format(state.date)}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        IconButton(onClick = { viewModel.setDate(state.date.minusDays(1)) }) {
                            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Oldingi kun")
                        }
                        IconButton(onClick = { viewModel.setDate(state.date.plusDays(1)) }) {
                            Icon(Icons.Outlined.ChevronRight, contentDescription = "Keyingi kun")
                        }
                    }
                },
            )
        }

        section(
            title = "Muddati o‘tgan",
            tasks = state.overdue,
            onEditTask = onEditTask,
            onCheckedChange = viewModel::setCompleted,
            onDelete = viewModel::deleteTask,
        )

        section(
            title = "Bugungi vazifalar",
            tasks = state.today,
            onEditTask = onEditTask,
            onCheckedChange = viewModel::setCompleted,
            onDelete = viewModel::deleteTask,
        )

        section(
            title = "Rejasiz",
            tasks = state.unscheduled,
            onEditTask = onEditTask,
            onCheckedChange = viewModel::setCompleted,
            onDelete = viewModel::deleteTask,
        )

        item {
            Text(
                text = "Maslahat: pastdagi (+) tugmasi orqali vazifa qo‘shing.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.section(
    title: String,
    tasks: List<Task>,
    onEditTask: (Long) -> Unit,
    onCheckedChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
) {
    if (tasks.isEmpty()) return

    item {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 4.dp),
            style = MaterialTheme.typography.titleSmall,
        )
    }

    items(
        items = tasks,
        key = { it.id },
    ) { task ->
        TaskListItem(
            task = task,
            onClick = onEditTask,
            onCheckedChange = onCheckedChange,
            onDelete = onDelete,
        )
    }
}

