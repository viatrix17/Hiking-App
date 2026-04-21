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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.roadapp.model.RouteHistoryRecord
import com.example.roadapp.util.getRouteImageId
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
    isTablet: Boolean,
) {
    val timerMap by viewModel.timerStates.collectAsState()
    val activeRouteName by viewModel.activeRouteName.collectAsState()
    val history by viewModel.getRouteTimes(name).collectAsState(initial = emptyList())

    val currentTimer = timerMap[name] ?: Timer(routeName = name)
    val isAnyTimerRunning = activeRouteName != null
    val isThisRunning = (activeRouteName == name)
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val hasUnsavedTimers = timerMap.any { (routeName, timer) ->
        routeName != name && (timer.hours > 0 || timer.minutes > 0 || timer.seconds > 0)
    }

    val onStart = {
        if (hasUnsavedTimers) showConfirmationDialog = true
        else viewModel.startTimer(name)
    }

    val onStop = { viewModel.stopTimer(name) }
    val onReset = { viewModel.resetTimer(name) }
    val onSave = { viewModel.saveTime(currentTimer.copy(routeName = name)) }

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
            dismissButton = { TextButton(onClick = { showConfirmationDialog = false }) { Text("Anuluj") } }
        )
    }

    if (isTablet) {
        TabletDetailsLayout(
            name = name, description = description, id = id,
            timer = currentTimer, history = history,
            isThisRunning = isThisRunning, isAnyTimerRunning = isAnyTimerRunning,
            onStart = onStart, onStop = onStop, onReset = onReset, onSave = onSave
        )
    } else {
        MobileDetailsLayout(
            name = name, description = description, id = id,
            timer = currentTimer, history = history,
            isThisRunning = isThisRunning, isAnyTimerRunning = isAnyTimerRunning,
            onStart = onStart, onStop = onStop, onReset = onReset, onSave = onSave,
            onBack = onBack
        )
    }

}


@Composable
fun MobileDetailsLayout(
    name: String,
    description: String,
    id: Int,
    timer: Timer,
    history: List<RouteHistoryRecord>,
    isThisRunning: Boolean,
    isAnyTimerRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var isHistoryExpanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = getRouteImageId(id)),
            contentDescription = "Zdjęcie trasy ${name}",
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d:%02d", timer.hours, timer.minutes, timer.seconds),
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        AppIconButton(
                            onClick = { if (isThisRunning) onStop() else onStart() }, // Używamy callbacków!
                            icon = if (isThisRunning) Icons.Default.Stop else Icons.Default.Timer,
                            contentDescription = if (isThisRunning) "Zatrzymaj" else "Uruchom",
                            enabled = isThisRunning || !isAnyTimerRunning
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        AppIconButton(
                            onClick = onReset, // Używamy callbacka!
                            icon = Icons.Default.SettingsBackupRestore,
                            contentDescription = "Zresetuj timer",
                            enabled = !isAnyTimerRunning
                        )
                    }

                    if (!isThisRunning && (timer.hours > 0 || timer.minutes > 0 || timer.seconds > 0)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Zapisz",
                            onClick = onSave, // Używamy callbacka!
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

@Composable
fun TabletDetailsLayout(
    name: String,
    description: String,
    id: Int,
    timer: Timer,
    history: List<RouteHistoryRecord>,
    isThisRunning: Boolean,
    isAnyTimerRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
) {
    var isHistoryExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format(
                        "%02d:%02d:%02d",
                        timer.hours,
                        timer.minutes,
                        timer.seconds
                    ),
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppIconButton(
                        onClick = { if (isThisRunning) onStop() else onStart() },
                        icon = if (isThisRunning) Icons.Default.Stop else Icons.Default.Timer,
                        contentDescription = if (isThisRunning) "Zatrzymaj" else "Uruchom",
                        enabled = isThisRunning || !isAnyTimerRunning
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    AppIconButton(
                        onClick = onReset,
                        icon = Icons.Default.SettingsBackupRestore,
                        contentDescription = "Zresetuj timer",
                        enabled = !isAnyTimerRunning
                    )
                }

                if (!isThisRunning && (timer.hours > 0 || timer.minutes > 0 || timer.seconds > 0)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Zapisz",
                        onClick = onSave,
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
                color = MaterialTheme.colorScheme.onBackground
            )
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
