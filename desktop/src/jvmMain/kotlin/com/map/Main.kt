package com.map

import com.map.*
import java.awt.Toolkit
import java.awt.Dimension
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

fun main() = application {
    val icon = painterResource("images/ic_imageviewer_round.png")

    Window(
        onCloseRequest = ::exitApplication,
        title = "Map View",
        state = WindowState(
//            position = WindowPosition.Aligned(Alignment.Center),
            position = WindowPosition(Alignment.TopStart),
            size = getPreferredWindowSize(1200, 600)
        ),
        icon = icon
    ) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            mapTilerSecretKey = MAPTILER_SECRET_KEY,
            latitude = 59.999394,
            longitude = 29.745412,
            startScale = 300.0,
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
