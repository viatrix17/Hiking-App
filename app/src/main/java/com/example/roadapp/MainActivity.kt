package com.example.roadapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.roadapp.ui.theme.RoadAppTheme
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.roadapp.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoadAppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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
                                containerColor = Color(0xFF692d19),
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
                            MainScreen(
                                viewModel = viewModel,
                                onRouteSelected = { routeName ->
                                    val encodedName = Uri.encode(routeName)
                                    navController.navigate("details/$encodedName")
                                })
                        }
                        composable("details/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: ""

                            val route = viewModel.getRouteByName(name)

                            DetailsScreen(
                                name = name,
                                description = route?.description ?: "Brak opisu",
                                onBack = { navController.popBackStack() }
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
fun DetailsScreen(name: String, description: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Row (modifier = Modifier.padding(8.dp)) {
            Text(name, style = MaterialTheme.typography.headlineMedium)
        }
        Text(description, style = MaterialTheme.typography.bodyMedium)

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
        modifier = modifier.fillMaxWidth().padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
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
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Blue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Wstecz"
        )
    }
}
//

// TOP BAR MA POKAZYWAC NAJPIERW ZE TRASY GORSKIE A POTEM NAZWE PODSTRONY I OBOK TEGO PRZYCISK
//@Composable
//fun MainTabletScreen(onRouteSelected: (String) -> Unit, viewModel: RouteViewModel) {
//
//}

//@Composable
//fun DetailsTabletScreen(name: String, description: String, onBack: () -> Unit) {
//    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
//        Button(onClick = onBack) {
//            Text("Powrót do menu")
//        }
//
//        Text("Szczegóły trasy", style = MaterialTheme.typography.labelLarge)
//        Text(name, style = MaterialTheme.typography.headlineMedium)
//        Text(description, style = MaterialTheme.typography.bodyMedium)
//
//    }
//}

//@Composable
//fun CounterScreen(viewModel: StoperViewModel = viewModel()) {
//    // Pobranie aktualnej wartości ze StateFlow
//    val count by viewModel.count.collectAsState()
//    Column {
//        // Wyświetlenie wartości licznika
//        Text(text = "Licznik: $count")
//        // Przycisk zwiększający licznik
//        Button(onClick = { viewModel.increment() }) {
//            Text("Dodaj")
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoadAppTheme {
    }
}

