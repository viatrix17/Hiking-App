package com.example.roadapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.roadapp.ui.screens.DetailsScreen
import com.example.roadapp.ui.screens.MainScreen
import com.example.roadapp.ui.screens.TabletMainScreen
import com.example.roadapp.viewmodel.RouteViewModel
import com.example.roadapp.viewmodel.TimerViewModel
import android.net.Uri
import com.example.roadapp.ui.screens.WelcomeScreen

@Composable
fun RoadAppNavHost(
    navController: NavHostController,
    isTablet: Boolean,
    viewModel: RouteViewModel,
    timerViewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = modifier
    ) {
        composable("welcome") {
            WelcomeScreen(onNavigateToHome = {
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            })
        }
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
            // Pobieramy trasę z ViewModelu
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