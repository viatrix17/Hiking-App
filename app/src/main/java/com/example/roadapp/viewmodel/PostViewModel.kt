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

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    fun loadPosts() {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getAllPosts()
                _posts.value = result
            } catch (e: Exception) {
            }
        }
    }
}
