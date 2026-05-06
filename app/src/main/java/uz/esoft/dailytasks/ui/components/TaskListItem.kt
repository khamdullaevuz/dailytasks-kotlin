package uz.esoft.dailytasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.esoft.dailytasks.model.Task
import java.time.format.DateTimeFormatter

private val dueDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@Composable
fun TaskListItem(
    task: Task,
    onClick: (Long) -> Unit,
    onCheckedChange: (Long, Boolean) -> Unit,
    onSetReminderInMinutes: (Long, Long) -> Unit,
    onClearReminder: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(task.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { checked -> onCheckedChange(task.id, checked) },
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            val subtitle = buildString {
                if (task.dueDate != null) {
                    append("Sana: ")
                    append(dueDateFormatter.format(task.dueDate))
                }
                if (task.description.isNotBlank()) {
                    if (isNotEmpty()) append(" • ")
                    append(task.description)
                }
            }

            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.width(4.dp))

        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Outlined.MoreVert, contentDescription = "Menyu")
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Tahrirlash") },
                onClick = {
                    menuExpanded = false
                    onClick(task.id)
                },
            )

            DropdownMenuItem(
                text = { Text("Eslatma: 10 daq") },
                onClick = {
                    menuExpanded = false
                    onSetReminderInMinutes(task.id, 10)
                },
            )

            DropdownMenuItem(
                text = { Text("Eslatma: 30 daq") },
                onClick = {
                    menuExpanded = false
                    onSetReminderInMinutes(task.id, 30)
                },
            )

            if (task.hasReminder) {
                DropdownMenuItem(
                    text = { Text("Eslatmani o‘chirish") },
                    onClick = {
                        menuExpanded = false
                        onClearReminder(task.id)
                    },
                )
            }

            DropdownMenuItem(
                text = { Text("O‘chirish") },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    onDelete(task.id)
                },
            )
        }
    }
}

