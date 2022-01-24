package com.map

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Text

@Composable
internal actual fun Telemetry(state: MapState) {
    Br {  }
    Text(state.toShortString())
}
