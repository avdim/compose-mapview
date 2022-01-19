package com.map

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow

@Composable
internal actual fun Telemetry(stateFlow: StateFlow<MapState<*>>) {
    val state by stateFlow.collectAsState()
    Column {
        Text(state.toShortString())
    }
}
