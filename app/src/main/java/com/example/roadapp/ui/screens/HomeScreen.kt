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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.roadapp.model.Route
import com.example.roadapp.ui.components.PrimaryButton
import com.example.roadapp.ui.components.SimpleVerticalScrollbar
import com.example.roadapp.viewmodel.RouteViewModel
import com.example.roadapp.viewmodel.TimerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
                    if (isListVisible && activeFilterType == "bike") {
                        viewModel.selectAllRoutes()
                        activeFilterType = null
                    }
                    else {
                        viewModel.selectBikeRoutes(); activeFilterType = "bike";
                    }
                    isListVisible = true
                },
                onToggleHiking = {
                    if (isListVisible && activeFilterType == "hiking") {
                        viewModel.selectAllRoutes()
                        activeFilterType = null
                    }
                    else {
                        viewModel.selectHikingRoutes(); activeFilterType = "hiking";
                    }
                    isListVisible = true
                },
                onRouteSelected = { route ->
                    onRouteSelected(route.name)
                },
                listState = listState
            )
        } else {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(0.4f)) {
                    TabletLayout(
                        searchQuery = searchQuery,
                        filteredRoutes = filteredRoutes,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        isListVisible = isListVisible,
                        onToggleBike = {
                            if (isListVisible && activeFilterType == "bike") {
                                viewModel.selectAllRoutes()
                                activeFilterType = null
                            }
                            else {
                                viewModel.selectBikeRoutes(); activeFilterType =
                                    "bike";
                            }
                            isListVisible = true

                        },
                        onToggleHiking = {
                            if (isListVisible && activeFilterType == "hiking") {
                                viewModel.selectAllRoutes()
                                activeFilterType = null
                            }
                            else {
                                viewModel.selectHikingRoutes(); activeFilterType =
                                    "hiking";
                            }
                            isListVisible = true
                        },
                        onRouteSelected = { route ->
                            selectedRoute = if (selectedRoute == route) null else route
                        },
                        listState = listState,
                        drawerState = drawerState,
                        scope = scope,
                        activeFilterType = activeFilterType
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
    listState: LazyGridState = rememberLazyGridState(),
    drawerState: DrawerState,
    scope: CoroutineScope,
    activeFilterType: String?
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(200.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = MaterialTheme.colorScheme.primary,
                drawerContentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Column(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
                    Text(
                        "Wybierz trasę",
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    NavigationDrawerItem(
                        label = { Text("Trasy rowerowe") },
                        selected = (activeFilterType == "bike"),
                        onClick = {
                            onToggleBike()
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            unselectedContainerColor = MaterialTheme.colorScheme.primary,                        // Tło niewybranego
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        label = { Text("Trasy piesze") },
                        selected = (activeFilterType == "hiking"),
                        onClick = {
                            onToggleHiking()
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            unselectedContainerColor = MaterialTheme.colorScheme.primary,                        // Tło niewybranego
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text("Wyszukaj trasę") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }

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