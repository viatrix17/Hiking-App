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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.roadapp.ui.theme.RoadAppTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.roadapp.data.RoadAppDatabase
import com.example.roadapp.model.Route
import com.example.roadapp.model.Timer
import com.example.roadapp.ui.components.AppIconButton
import com.example.roadapp.ui.components.PrimaryButton
import com.example.roadapp.ui.components.ReturnButton
import com.example.roadapp.ui.navigation.RoadAppNavHost
import com.example.roadapp.ui.theme.BrickOrange
import com.example.roadapp.ui.theme.DarkBrown
import com.example.roadapp.viewmodel.RouteViewModel
import com.example.roadapp.viewmodel.TimerViewModel
import com.example.roadapp.viewmodel.TimerViewModelFactory


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
            var isDarkTheme by remember { mutableStateOf(false) }

            RoadAppTheme(darkTheme = isDarkTheme) {
                val configuration = LocalConfiguration.current
                val isTablet = configuration.screenWidthDp >= 600

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val topBarTitle = when {
                    currentRoute == "home" -> "Trasy górskie w Polsce"

                    currentRoute?.startsWith("details") == true -> "Szczegóły trasy"

                    else -> "Trasy górskie"
                }
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
                                    IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
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

