package com.example.roadapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadapp.model.Post
import com.example.roadapp.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    // Stan przechowujący listę obiektów Post z Twojego modelu
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    fun loadPosts() {
        // Uruchomienie klatki (coroutine), aby nie blokować interfejsu użytkownika
        viewModelScope.launch {
            try {
                // Wywołanie Twojego API zdefiniowanego w RetrofitInstance
                val result = RetrofitInstance.api.getAllPosts()
                _posts.value = result
            } catch (e: Exception) {
                // Tutaj warto dodać obsługę błędów, np. brak internetu
            }
        }
    }
}

//// Klasa ViewModel zarządzająca stanem licznika
//class StoperViewModel : ViewModel() {
//    // Prywatny, zmienialny strumień stanu
//    // MutableStateFlow przechowuje aktualną wartość i emituje jej zmiany
//    private val _count = MutableStateFlow(0)
//    // Publiczny strumień tylko do odczytu
//    // UI może obserwować wartość, ale nie może jej zmieniać
//    // asStateFlow() ukrywa możliwość modyfikacji
//    val count: StateFlow<Int> = _count.asStateFlow()
//    fun increment() {
//        // Zwiększenie wartości przechowywanej w StateFlow
//        // Po zmianie wartości obserwatorzy (np. Compose) zostaną powiadomieni
//        _count.value++
//    }
//}