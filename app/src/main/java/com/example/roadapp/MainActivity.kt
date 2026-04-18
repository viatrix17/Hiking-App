package com.example.roadapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.roadapp.ui.theme.RoadAppTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.roadapp.model.Route
import com.example.roadapp.model.Timer
import com.example.roadapp.ui.theme.BrickOrange
import com.example.roadapp.ui.theme.DarkBrown
import com.example.roadapp.viewmodel.RouteViewModel
import com.example.roadapp.viewmodel.TimerViewModel


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val configuration = LocalConfiguration.current
            val isTablet = configuration.screenWidthDp >= 600
            RoadAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val timerViewModel: TimerViewModel = viewModel()

                val topBarTitle = when {
                    currentRoute == "home" -> "Trasy górskie w Polsce"

                    currentRoute?.startsWith("details") == true -> "Szczegóły trasy"

                    else -> "Trasy górskie"
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = topBarTitle,
                                    color = Color.White )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = DarkBrown,
                                titleContentColor = Color.White,
                            ),
                            navigationIcon = {
                                if (currentRoute != "home") {
                                    ReturnButton(
                                        onClick = {navController.popBackStack()},
                                        modifier = Modifier.padding(8.dp))
                                }
                            }

                        )
                    }
                ) { innerPadding ->
                    val viewModel : RouteViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("home") {
                            if (isTablet) {
                                TabletMainScreen(viewModel = viewModel)
                            } else {
                                MainScreen(
                                    viewModel = viewModel,
                                    onRouteSelected = { routeName ->
                                        val encodedName = Uri.encode(routeName)
                                        navController.navigate("details/$encodedName")
                                    }
                                )
                            }
                        }
                        composable("details/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: ""

                            val route = viewModel.getRouteByName(name)

                            DetailsScreen(
                                name = name,
                                description = route?.description ?: "Brak opisu",
                                onBack = { navController.popBackStack() },
                                viewModel = timerViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(onRouteSelected: (String) -> Unit, viewModel: RouteViewModel) {
    val routes by viewModel.currentRoutes.collectAsState()
    LaunchedEffect(Unit) {
        if(routes.isEmpty()) {
            viewModel.loadFromGist()
        }
    }
    Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                PrimaryButton(
                    "Trasy rowerowe",
                    onClick = { viewModel.selectBikeRoutes()},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                PrimaryButton(
                    "Trasy piesze",
                    onClick = { viewModel.selectHikingRoutes()},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()

                )
            }
            RoutesList(
                data = routes,
                onRouteSelected = { route ->
                    onRouteSelected(route.name)
                }
            )
        }
}
@Composable
fun DetailsScreen(
    name: String,
    description: String,
    onBack: () -> Unit,
    viewModel: TimerViewModel,
    formatTimestamp: (Long) -> Unit,
    formatMillisToTime: (Long) -> Unit
) {

    val timerMap by viewModel.timerStates.collectAsState()
    val activeRouteName by viewModel.activeRouteName.collectAsState()

    val history by viewModel.getRouteTimes(name).collectAsState(initial = emptyList())
    var isHistoryExpanded by remember { mutableStateOf(false) }

    val currentTimer = timerMap[name] ?: Timer(routeName = name)
    val isAnyTimerRunning = activeRouteName != null
    val isThisRunning = (activeRouteName == name)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
        Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = String.format("%02d:%02d:%02d", currentTimer.hours, currentTimer.minutes, currentTimer.seconds),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(modifier = Modifier.padding(all=8.dp))
        {
            AppIconButton(
                onClick = {
                    if (isThisRunning) viewModel.stopTimer(name)
                    else viewModel.startTimer(name)
                },
                icon = if (isThisRunning) Icons.Default.Stop else Icons.Default.Timer,
                contentDescription = if (isThisRunning) "Zatrzymaj" else "Uruchom",
                enabled = isThisRunning || !isAnyTimerRunning
            )
            AppIconButton(
                onClick = { viewModel.resetTimer(name) },
                icon = Icons.Default.SettingsBackupRestore,
                contentDescription = "Zresetuj timer",
                enabled = !isAnyTimerRunning
            )
            if (!isThisRunning && (currentTimer.hours > 0 || currentTimer.minutes > 0 || currentTimer.seconds > 0)) {
                PrimaryButton(
                    text = "Zapisz",
                    onClick = {  },
                    icon = Icons.Default.Save,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = { !isHistoryExpanded = isHistoryExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isHistoryExpanded) "Ukryj historię" else "Pokaż historię")
            Icon(
                imageVector = if (isHistoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
        if (isHistoryExpanded) {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(history) { record ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatTimestamp(record.timestamp)) //
                        Text(formatMillisToTime(record.durationInMillis)) //
                    }
                }
            }
        }
    }
}

@Composable
fun TabletMainScreen(viewModel: RouteViewModel) {
    val routes by viewModel.currentRoutes.collectAsState()
    var selectedRoute by remember { mutableStateOf<Route?>(null) }

    LaunchedEffect(Unit) {
        if(routes.isEmpty()) {
            viewModel.loadFromGist()
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(350.dp)
                .fillMaxHeight()
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                PrimaryButton(
                    "Trasy rowerowe",
                    onClick = { viewModel.selectBikeRoutes() },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                PrimaryButton(
                    "Trasy piesze",
                    onClick = { viewModel.selectHikingRoutes() },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                RoutesList(
                    data = routes,
                    onRouteSelected = { route ->
                        selectedRoute = route
                    }
                )
            }
        }

        VerticalDivider(thickness = 1.dp, color = Color.LightGray)

//        Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(32.dp)) {
//            val current = selectedRoute
//            if (current != null) {
//                DetailsScreen(
//                    name = current.name,
//                    description = current.description,
//                    onBack = {}
//                )
//            } else {
//                Text(
//                    text = "Wybierz trasę z listy",
//                    modifier = Modifier.align(Alignment.Center),
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = Color.Gray
//                )
//            }
//        }
    }
}
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrickOrange,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun ReturnButton (
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Wstecz",
            tint = Color.White
        )
    }
}

@Composable 
fun AppIconButton (
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    )
    {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) Color.Black
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoadAppTheme {
    }
}

