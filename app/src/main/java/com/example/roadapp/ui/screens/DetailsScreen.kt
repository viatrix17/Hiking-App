package com.example.roadapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.roadapp.model.RouteHistoryRecord
import com.example.roadapp.util.getRouteImageId
import com.example.roadapp.model.Timer
import com.example.roadapp.ui.components.AppIconButton
import com.example.roadapp.ui.components.PrimaryButton
import com.example.roadapp.ui.components.SimpleListScrollbar
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

    val listState = rememberLazyListState()

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
        android.util.Log.d("DEBUG_VM", "Tablet!!!")
        TabletDetailsLayout(
            name = name, description = description, id = id,
            timer = currentTimer, history = history,
            isThisRunning = isThisRunning, isAnyTimerRunning = isAnyTimerRunning,
            onStart = onStart, onStop = onStop, onReset = onReset, onSave = onSave,
            listState = listState
        )
    } else if (isLandscape) {
        android.util.Log.d("DEBUG_VM", "Obrót!!!")
        LandscapeMobileDetailsLayout(
            name = name, description = description, id = id,
            timer = currentTimer, history = history,
            isThisRunning = isThisRunning, isAnyTimerRunning = isAnyTimerRunning,
            onStart = onStart, onStop = onStop, onReset = onReset, onSave = onSave,
            onBack = onBack
        )
    } else {
        android.util.Log.d("DEBUG_VM", "Brak obrotu!!!")
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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)) {

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

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
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
                    color = MaterialTheme.colorScheme.onBackground)
                Icon(
                    imageVector = if (isHistoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            if (isHistoryExpanded) {
                android.util.Log.e("DEBUG_VM", "HALO")
                if (history.isEmpty()) {
                    android.util.Log.e("DEBUG_VM", "BRAK")
                    Text(
                        text = "Brak historii dla tej trasy",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    android.util.Log.e("DEBUG_VM", "JEST")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        history.forEach { record ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeMobileDetailsLayout(
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
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = getRouteImageId(id)),
                contentDescription = "Zdjęcie trasy ${name}",
                modifier = Modifier
                    .height(250.dp)
                    .weight(0.5f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(0.5f)) {
                Text(text = name, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppIconButton(
                                onClick = { if (isThisRunning) onStop() else onStart() }, // Używamy callbacków!
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
                            Spacer(modifier = Modifier.width(16.dp))
                            AppIconButton(
                                onClick = { isHistoryExpanded = true }, // Otwiera BottomSheet
                                icon = Icons.Default.ViewList,
                                contentDescription = "Pokaż historię"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            if (!isThisRunning && (timer.hours > 0 || timer.minutes > 0 || timer.seconds > 0)) {
                                Spacer(modifier = Modifier.height(16.dp))
                                AppIconButton(
                                    onClick = onSave,
                                    icon = Icons.Default.Save,
                                    contentDescription = "Zapisz",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (isHistoryExpanded) {
        ModalBottomSheet(
            onDismissRequest = { isHistoryExpanded = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Historia trasy", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                history.forEach { record ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = record.formattedDate)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "${record.timer.hours}:${record.timer.minutes}:${record.timer.seconds}")
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
    listState: LazyListState = rememberLazyListState(),
) {
    var isHistoryExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = getRouteImageId(id)),
            contentDescription = "Zdjęcie trasy ${name}",
            modifier = Modifier.fillMaxWidth().height(250.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = name, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    history.forEach { record ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
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

