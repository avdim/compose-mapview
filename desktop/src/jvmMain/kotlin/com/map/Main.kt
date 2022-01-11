package com.map

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.map.model.ContentState
import com.map.style.icAppRounded
import com.map.utils.getPreferredWindowSize
import com.map.view.AppUI

fun main() = application {
    val state = rememberWindowState()
    val content = remember {
        ContentState.applyContent(state)
    }

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
            AppUI(content)
        }
    }
}
