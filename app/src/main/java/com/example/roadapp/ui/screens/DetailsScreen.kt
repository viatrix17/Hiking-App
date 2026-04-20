package com.example.roadapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.roadapp.getRouteImageId
import com.example.roadapp.model.Timer
import com.example.roadapp.ui.components.AppIconButton
import com.example.roadapp.ui.components.PrimaryButton
import com.example.roadapp.viewmodel.TimerViewModel
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun DetailsScreen(
    name: String,
    description: String,
    id: Int,
    onBack: () -> Unit,
    viewModel: TimerViewModel,
) {

    val timerMap by viewModel.timerStates.collectAsState()
    val activeRouteName by viewModel.activeRouteName.collectAsState()

    val history by viewModel.getRouteTimes(name).collectAsState(initial = emptyList())
    var isHistoryExpanded by remember { mutableStateOf(false) }

    val currentTimer = timerMap[name] ?: Timer(routeName = name)
    val isAnyTimerRunning = activeRouteName != null
    val isThisRunning = (activeRouteName == name)

    var showConfirmationDialog by remember { mutableStateOf(false) }

    val hasUnsavedTimers = timerMap.any { (routeName, timer) ->
        routeName != name && (timer.hours > 0 || timer.minutes > 0 || timer.seconds > 0)
    }

    fun handleStartClick() {
        if (hasUnsavedTimers) {
            showConfirmationDialog = true
        } else {
            viewModel.startTimer(name)
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Utrata danych") },
            text = { Text("Posiadasz niezapisany czas w innej trasie. Jeśli zaczniesz tutaj, dane z poprzedniej trasy zostaną usunięte. Czy chcesz kontynuować?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.discardUnsavedTimers(exceptRouteName = name)
                    viewModel.startTimer(name)
                    showConfirmationDialog = false
                }) { Text("Tak, zacznij nowy") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) { Text("Anuluj") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = getRouteImageId(id)),
            contentDescription = "Zdjęcie trasy ${name}",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        {

            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%02d:%02d:%02d", currentTimer.hours, currentTimer.minutes, currentTimer.seconds),
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tylko ikony w rzędzie
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconButton(
                            onClick = { if (isThisRunning) viewModel.stopTimer(name) else handleStartClick() },
                            icon = if (isThisRunning) Icons.Default.Stop else Icons.Default.Timer,
                            contentDescription = if (isThisRunning) "Zatrzymaj" else "Uruchom",
                            enabled = isThisRunning || !isAnyTimerRunning
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        AppIconButton(
                            onClick = { viewModel.resetTimer(name) },
                            icon = Icons.Default.SettingsBackupRestore,
                            contentDescription = "Zresetuj timer",
                            enabled = !isAnyTimerRunning
                        )
                    }

                    // Przycisk "Zapisz" jest teraz pod spodem, w kolumnie
                    if (!isThisRunning && (currentTimer.hours > 0 || currentTimer.minutes > 0 || currentTimer.seconds > 0)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Zapisz",
                            onClick = {
                                val timerToSave = currentTimer.copy(routeName = name)
                                viewModel.saveTime(timerToSave)
                            },
                            icon = Icons.Default.Save,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = { isHistoryExpanded = !isHistoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
            Text(
                if (isHistoryExpanded) "Ukryj historię" else "Pokaż historię",
                color = MaterialTheme.colorScheme.onBackground)
            Icon(
                imageVector = if (isHistoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
        if (isHistoryExpanded) {
            if (history.isEmpty()) {
                Text(
                    text = "Brak historii dla tej trasy",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(history) { record ->
                        Row {
                            Text(text = record.formattedDate)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "${record.timer.hours}:${record.timer.minutes}:${record.timer.seconds}")
                        }
                    }
                }
            }
            }
        }
    }
}

