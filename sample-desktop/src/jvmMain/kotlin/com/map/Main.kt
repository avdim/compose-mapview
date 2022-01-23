package com.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dimension
import java.awt.Toolkit

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
        val constScale = 1.0
        MapView(
            modifier = Modifier.fillMaxSize(),
            mapTilerSecretKey = MAPTILER_SECRET_KEY,
            latitude = 59.999394,
            longitude = 29.745412,
            startScale = if(ANIMATE) animatedScale.toDouble() else constScale,
            onMapViewClick = { latitude, longitude ->
                println("click on geo coordinates: (lat $latitude, lon $longitude)")
                true
            }
        )
    }
}

fun getPreferredWindowSize(desiredWidth: Int, desiredHeight: Int): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val preferredWidth: Int = (screenSize.width * 0.8f).toInt()
    val preferredHeight: Int = (screenSize.height * 0.8f).toInt()
    val width: Int = if (desiredWidth < preferredWidth) desiredWidth else preferredWidth
    val height: Int = if (desiredHeight < preferredHeight) desiredHeight else preferredHeight
    return DpSize(width.dp, height.dp)
}
