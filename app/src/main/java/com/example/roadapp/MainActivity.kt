package com.example.roadapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
                                viewModel = timerViewModel
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
                    modifier = Modifier.weight(1f))
                PrimaryButton(
                    "Trasy piesze",
                    onClick = { viewModel.selectHikingRoutes()},
                    modifier = Modifier.weight(1f))
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
fun DetailsScreen(name: String, description: String, onBack: () -> Unit, viewModel: TimerViewModel) {

    val timer by viewModel.timerState.collectAsState()
    val isRunning by remember(timer) { derivedStateOf { timer.isRunning } }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(name, style = MaterialTheme.typography.headlineMedium)
        }
        Text(description, style = MaterialTheme.typography.bodyMedium)
        Row(modifier = Modifier.padding(all=8.dp))
        {
            if (!isRunning) {
                AppIconButton(
                    onClick = { viewModel.startTimer() },
                    icon = Icons.Default.Timer,
                    contentDescription = "Uruchom Timer"
                )
            } else {
                AppIconButton(
                    onClick = { viewModel.stopTimer() },
                    icon = Icons.Default.Stop,
                    contentDescription = "Uruchom Timer"
                )
            }
            AppIconButton(
                onClick = { viewModel.resetTimer() },
                icon = Icons.Default.SettingsBackupRestore,
                contentDescription = "Zresetuj timer"
            )
        }
        Text(
            text = String.format("%02d:%02d:%02d", timer.hours, timer.minutes, timer.seconds),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
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
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    "Trasy piesze",
                    onClick = { viewModel.selectHikingRoutes() },
                    modifier = Modifier.weight(1f)
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
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrickOrange,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
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
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
    )
    {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoadAppTheme {
    }
}

