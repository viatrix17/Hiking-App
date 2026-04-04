package com.example.roadapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadapp.model.Route
import com.example.roadapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteViewModel : ViewModel() {

    private var allBikeRoutesFromApi: List<Route> = emptyList()
    private var allHikingRoutesFromApi: List<Route> = emptyList()

    private val _currentRoutes = MutableStateFlow<List<Route>>(emptyList())
    val currentRoutes: StateFlow<List<Route>> = _currentRoutes.asStateFlow()

    fun loadFromGist() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // IO thread
                val response = RetrofitInstance.api.getAllPosts()

                val bikeRoutes = response
                    .filter { it.userId == 1 }
                    .map { Route(it.title, it.description) }

                val hikingRoutes = response
                    .filter { it.userId == 2 }
                    .map { Route(it.title, it.description) }

                // main thread
                withContext(Dispatchers.Main) {
                    allBikeRoutesFromApi = bikeRoutes
                    allHikingRoutesFromApi = hikingRoutes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectBikeRoutes() {
        _currentRoutes.value = allBikeRoutesFromApi
    }

    fun selectHikingRoutes() {
        _currentRoutes.value = allHikingRoutesFromApi
    }

    fun getRouteByName(name: String): Route? {
        return (allBikeRoutesFromApi + allHikingRoutesFromApi).find { it.name == name }
    }
}