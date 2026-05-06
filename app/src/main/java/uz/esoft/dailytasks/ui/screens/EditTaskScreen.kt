package uz.esoft.dailytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.esoft.dailytasks.ui.viewmodel.EditTaskViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    viewModel: EditTaskViewModel,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    var showDateDialog by remember { mutableStateOf(false) }
    val zoneId = ZoneId.systemDefault()

    Column(modifier = modifier) {
        TopAppBar(
            title = { Text(if (state.taskId == 0L) "Vazifa qo‘shish" else "Vazifani tahrirlash") },
            actions = {
                if (state.taskId != 0L) {
                    IconButton(
                        onClick = { viewModel.delete { onFinished() } },
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "O‘chirish")
                    }
                }
                IconButton(
                    onClick = { viewModel.save { onFinished() } },
                    enabled = state.canSave,
                ) {
                    Icon(Icons.Outlined.Save, contentDescription = "Saqlash")
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nomi") },
                singleLine = true,
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tavsif") },
                minLines = 3,
            )

            val dueDateLabel = state.dueDate?.let(dateFormatter::format) ?: "Tanlanmagan"
            OutlinedButton(
                onClick = { showDateDialog = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Sana: $dueDateLabel")
            }

            Text(
                text = "Vazifani bajarilgandan so‘ng ro‘yxatda belgilash (checkbox) orqali monitoring qilinadi.",
                style = MaterialTheme.typography.bodySmall,
            )

            // In-app reminder controls (no background services)
            val remindLabel = state.remindAt?.let { instant ->
                val local = instant.atZone(zoneId).toLocalDateTime()
                "${local.toLocalDate()} ${local.toLocalTime().withSecond(0).withNano(0)}"
            } ?: "Yo‘q"

            Text(
                text = "Eslatma (faqat ilova ichida): $remindLabel",
                style = MaterialTheme.typography.titleSmall,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = { viewModel.setReminderIn(10) }) { Text("10 daq") }
                Button(onClick = { viewModel.setReminderIn(30) }) { Text("30 daq") }
                Button(onClick = { viewModel.setReminderIn(60) }) { Text("1 soat") }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearReminder() },
                    enabled = state.remindAt != null,
                ) {
                    Text("Eslatmani o‘chirish")
                }
                Spacer(Modifier.width(4.dp))
            }

            Text(
                text = "Eslatma ilova yopiq bo‘lsa ishlamaydi (servis/WorkManager ishlatilmagan).",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    if (showDateDialog) {
        val initial = state.dueDate
            ?.atStartOfDay(zoneId)
            ?.toInstant()
            ?.toEpochMilli()

        val pickerState = rememberDatePickerState(initialSelectedDateMillis = initial)

        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = pickerState.selectedDateMillis
                        val localDate = selectedMillis?.let { millisToLocalDate(it, zoneId) }
                        viewModel.onDueDateChange(localDate)
                        showDateDialog = false
                    },
                ) { Text("Tanlash") }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) { Text("Bekor qilish") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private fun millisToLocalDate(millis: Long, zoneId: ZoneId): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
}

