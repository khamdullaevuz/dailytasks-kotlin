package uz.esoft.dailytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.esoft.dailytasks.ui.viewmodel.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier,
    ) {
        TopAppBar(
            title = { Text("Monitoring") },
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                title = "So‘nggi 7 kunda bajarilgan",
                value = state.completedLast7Days.toString(),
            )

            StatCard(
                title = "So‘nggi 7 kunda rejalashtirilgan",
                value = state.plannedLast7Days.toString(),
            )

            val percent = if (state.plannedLast7Days == 0) {
                0
            } else {
                (state.completedLast7Days * 100) / state.plannedLast7Days
            }

            StatCard(
                title = "Bajarilish foizi",
                value = "$percent%",
            )

            Text(
                text = "Eslatma: statistika rejalashtirilgan sanasi bo‘lgan vazifalar asosida hisoblanadi.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

