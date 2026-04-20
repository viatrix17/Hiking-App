package com.example.roadapp.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Animation
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.roadapp.R
import com.example.roadapp.ui.theme.DarkBrown
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit) {
    var isLogoVisible by remember { mutableStateOf(false) }
    var isTextVisible by remember { mutableStateOf(false) }

    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
        }
        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 1500)
            )
        }
        logoAlpha.animateTo(targetValue = 1f, animationSpec = tween(1000))
        textAlpha.animateTo(targetValue = 1f, animationSpec = tween(1000))

        delay(2000)
        onNavigateToHome()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_light_mode),
            contentDescription = "Rysunek gór",
            modifier = Modifier
                .scale(scale.value)
                .rotate(rotation.value)
                .alpha(logoAlpha.value)
                .fillMaxWidth(0.5f)
                .wrapContentHeight()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "HIKING APP",
            modifier = Modifier.alpha(textAlpha.value),
            style = MaterialTheme.typography.headlineMedium,
            color = DarkBrown
        )
    }
}