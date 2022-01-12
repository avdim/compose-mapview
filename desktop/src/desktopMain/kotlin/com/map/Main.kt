package com.map

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.map.style.icAppRounded
import com.map.utils.getPreferredWindowSize
import com.map.view.MainUiWithState

fun main() = application {
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
        MainUiWithState()
    }
}