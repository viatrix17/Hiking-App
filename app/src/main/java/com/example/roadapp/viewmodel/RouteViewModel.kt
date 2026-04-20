package com.example.roadapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadapp.model.Route
import com.example.roadapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteViewModel : ViewModel() {

    private var allBikeRoutesFromApi: List<Route> = emptyList()
    private var allHikingRoutesFromApi: List<Route> = emptyList()

    private val _allRoutes = MutableStateFlow<List<Route>>(emptyList())
    private val _currentRoutes = MutableStateFlow<List<Route>>(emptyList())
    val currentRoutes: StateFlow<List<Route>> = _currentRoutes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredRoutes = combine(_currentRoutes, _searchQuery, _allRoutes) { current, query, all ->
        if (query.isNotEmpty()) {
            all.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            current
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
    fun loadFromGist() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // IO thread
                val response = RetrofitInstance.api.getAllPosts()

                val bikeRoutes = response
                    .filter { it.userId == 1 }
                    .map { Route(it.id,it.title, it.description) }

                val hikingRoutes = response
                    .filter { it.userId == 2 }
                    .map { Route(it.id, it.title, it.description) }

                // main thread
                withContext(Dispatchers.Main) {
                    allBikeRoutesFromApi = bikeRoutes
                    allHikingRoutesFromApi = hikingRoutes
                    _allRoutes.value = bikeRoutes + hikingRoutes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectBikeRoutes() {
        _currentRoutes.value = allBikeRoutesFromApi
        _searchQuery.value = ""
    }

    fun selectHikingRoutes() {
        _currentRoutes.value = allHikingRoutesFromApi
        _searchQuery.value = ""
    }

    fun getRouteByName(name: String): Route? {
        return (allBikeRoutesFromApi + allHikingRoutesFromApi).find { it.name == name }
    }
}