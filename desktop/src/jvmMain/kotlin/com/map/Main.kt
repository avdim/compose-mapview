package com.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.map.model.ContentState
import com.map.style.Gray
import com.map.style.icAppRounded
import com.map.utils.getPreferredWindowSize
import com.map.view.ScrollableArea

fun main() = application {
    val content = ContentState
    val icon = icAppRounded()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Map View",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(800, 1000)
        ),
        icon = icon
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Gray
            ) {
                Column {
                    ScrollableArea(content)
                }
            }
        }
    }
}
