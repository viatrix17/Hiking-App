package com.example.roadapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.roadapp.model.Route
import com.example.roadapp.ui.components.PrimaryButton
import com.example.roadapp.ui.components.SimpleVerticalScrollbar
import com.example.roadapp.viewmodel.RouteViewModel
import com.example.roadapp.viewmodel.TimerViewModel

@Composable
fun MainScreen(
    onRouteSelected: (String) -> Unit,
    viewModel: RouteViewModel,
    timerViewModel: TimerViewModel) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredRoutes by viewModel.filteredRoutes.collectAsState()
    val listState = rememberLazyGridState()

    var isListVisible by rememberSaveable { mutableStateOf(true) }
    var activeFilterType by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedRoute by remember { mutableStateOf<Route?>(null) }


    LaunchedEffect(Unit) {
        if(filteredRoutes.isEmpty()) {
            viewModel.loadFromGist()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            PhoneLayout(
                searchQuery = searchQuery,
                filteredRoutes = filteredRoutes,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                isListVisible = isListVisible,
                onToggleBike = {
                    if (isListVisible && activeFilterType == "bike") isListVisible = false
                    else {
                        viewModel.selectBikeRoutes(); activeFilterType = "bike"; isListVisible =
                            true
                    }
                },
                onToggleHiking = {
                    if (isListVisible && activeFilterType == "hiking") isListVisible = false
                    else {
                        viewModel.selectHikingRoutes(); activeFilterType = "hiking"; isListVisible =
                            true
                    }
                },
                onRouteSelected = { route ->
                    onRouteSelected(route.name)
                },
                listState = listState
            )
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.4f)) {
                    TabletLayout(
                        searchQuery = searchQuery,
                        filteredRoutes = filteredRoutes,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        isListVisible = isListVisible,
                        onToggleBike = {
                            if (isListVisible && activeFilterType == "bike") isListVisible = false
                            else {
                                viewModel.selectBikeRoutes(); activeFilterType =
                                    "bike"; isListVisible =
                                    true
                            }
                        },
                        onToggleHiking = {
                            if (isListVisible && activeFilterType == "hiking") isListVisible = false
                            else {
                                viewModel.selectHikingRoutes(); activeFilterType =
                                    "hiking"; isListVisible =
                                    true
                            }
                        },
                        onRouteSelected = { route ->
                            selectedRoute = route
                        },
                        listState = listState
                    )
                }

                Box(modifier = Modifier.weight(0.6f)) {
                    if (selectedRoute != null) {
                        DetailsScreen(
                            name = selectedRoute!!.name,
                            description = selectedRoute!!.description ?: "Brak opisu",
                            id = selectedRoute!!.id ?: 0,
                            onBack = { selectedRoute = null },
                            viewModel = timerViewModel,
                            isTablet = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhoneLayout(
    searchQuery: String,
    filteredRoutes: List<Route>,
    onSearchQueryChange: (String) -> Unit,
    isListVisible: Boolean,
    onToggleBike: () -> Unit,
    onToggleHiking: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    listState: LazyGridState = rememberLazyGridState()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onSearchQueryChange(it) },
            label = { Text("Wyszukaj trasę") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                onClick = onToggleBike,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            PrimaryButton(
                "Trasy piesze",
                onClick = onToggleHiking,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()

            )
        }

        if (isListVisible) {
            Box(modifier = Modifier.fillMaxSize()) {
                RoutesList(
                    data = filteredRoutes,
                    onRouteSelected = onRouteSelected,
                    listState = listState
                )
                SimpleVerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(2.dp),
                    listState = listState
                )
            }
        }
    }
}


@Composable
fun TabletLayout(
    searchQuery: String,
    filteredRoutes: List<Route>,
    onSearchQueryChange: (String) -> Unit,
    isListVisible: Boolean,
    onToggleBike: () -> Unit,
    onToggleHiking: () -> Unit,
    onRouteSelected: (Route) -> Unit,
    listState: LazyGridState = rememberLazyGridState()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
        ) {
            PrimaryButton("Trasy rowerowe", onClick = onToggleBike)
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton("Trasy piesze", onClick = onToggleHiking)
        }

        VerticalDivider(thickness = 1.dp, color = Color.LightGray)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onSearchQueryChange(it) },
                label = { Text("Wyszukaj trasę") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isListVisible) {
                Box(modifier = Modifier.fillMaxSize()) {
                    RoutesList(
                        data = filteredRoutes,
                        onRouteSelected = onRouteSelected,
                        listState = listState
                    )
                    SimpleVerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(2.dp),
                        listState = listState
                    )
                }
            }
        }
    }
}
