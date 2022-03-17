package com.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

const val ANIMATE = false

fun main() = application {
    val icon = painterResource("images/ic_imageviewer_round.png")
    Window(
        onCloseRequest = ::exitApplication,
        title = "Map View",
        state = WindowState(
            position = WindowPosition(Alignment.TopStart),
            size = getPreferredWindowSize(1200, 600)
        ),
        icon = icon
    ) {
        if (ANIMATE) {
            AnimatedMapView()
        } else {
            MapView(
                modifier = Modifier.fillMaxSize(),
                mapTilerSecretKey = MAPTILER_SECRET_KEY,
                latitude = 59.999394,
                longitude = 29.745412,
                startScale = 1.0,
                onMapViewClick = { latitude, longitude ->
                    println("click on geo coordinates: (lat $latitude, lon $longitude)")
                    true
                }
            )
        }
    }
}

@Composable
fun AnimatedMapView() {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedScale: Float by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4200f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5_000
                2f at 500
                100f at 2000
                4100f at 4_500
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    val animatedMapState = derivedStateOf {
        MapState(
            latitude = 59.999394,
            longitude = 29.745412,
            scale = animatedScale.toDouble()
        )
    }
    MapView(
        modifier = Modifier.fillMaxSize(),
        mapTilerSecretKey = MAPTILER_SECRET_KEY,
        state = animatedMapState,
        onStateChange = {}
    )
}
