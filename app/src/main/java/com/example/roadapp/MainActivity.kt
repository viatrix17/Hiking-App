package com.example.roadapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.roadapp.ui.theme.RoadAppTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.roadapp.data.RoadAppDatabase
import com.example.roadapp.ui.components.ReturnButton
import com.example.roadapp.ui.navigation.RoadAppNavHost
import com.example.roadapp.util.PreferencesManager
import com.example.roadapp.viewmodel.RouteViewModel
import com.example.roadapp.viewmodel.TimerViewModel
import com.example.roadapp.viewmodel.TimerViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = RoadAppDatabase.getInstance(applicationContext)
        val dao = database.routeTimeDao()
        val factory = TimerViewModelFactory(dao)

        enableEdgeToEdge()
        setContent {
            val viewModel : RouteViewModel = viewModel()
            val timerViewModel: TimerViewModel = viewModel(factory = TimerViewModelFactory(dao))
            val isDarkTheme by PreferencesManager.getDarkMode(this).collectAsState(initial = false)

            RoadAppTheme(darkTheme = isDarkTheme) {
                val configuration = LocalConfiguration.current
                val isTablet = configuration.smallestScreenWidthDp >= 600

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val topBarTitle = when {
                    currentRoute == "home" -> "Trasy górskie w Polsce"

                    currentRoute?.startsWith("details") == true -> "Szczegóły trasy"

                    else -> "Trasy górskie"
                }
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        if (currentRoute != "welcome") {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(text = topBarTitle)
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                navigationIcon = {
                                    if (currentRoute != "home") {
                                        ReturnButton(
                                            onClick = { navController.popBackStack() },
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        scope.launch {
                                            PreferencesManager.saveDarkMode(context, !isDarkTheme)
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                            contentDescription = if (isDarkTheme) "Włącz tryb jasny" else "Włącz tryb ciemny",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }

                            )
                        }
                    }
                ) { innerPadding ->
                    RoadAppNavHost(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        isTablet = isTablet,
                        viewModel = viewModel,
                        timerViewModel = timerViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoadAppTheme {
    }
}

