package com.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun Telemetry(state: MapState) {
    Column(Modifier.background(Color(0x77ffFFff))) {
        Text(state.toShortString())
    }
}
