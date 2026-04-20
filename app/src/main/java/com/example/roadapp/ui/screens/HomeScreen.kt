package com.example.roadapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.roadapp.RoutesList
import com.example.roadapp.model.Route
import com.example.roadapp.ui.components.PrimaryButton
import com.example.roadapp.viewmodel.RouteViewModel

@Composable
fun MainScreen(onRouteSelected: (String) -> Unit, viewModel: RouteViewModel) {
//    val routes by viewModel.currentRoutes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredRoutes by viewModel.filteredRoutes.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if(filteredRoutes.isEmpty()) {
            viewModel.loadFromGist()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("Wyszukaj trasę") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            )
        )

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
        RoutesList(
            data = filteredRoutes,
            onRouteSelected = { route ->
                onRouteSelected(route.name)
            }
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