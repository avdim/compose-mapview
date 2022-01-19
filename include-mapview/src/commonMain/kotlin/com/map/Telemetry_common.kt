package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max

/**
 * Для тестового отображения состояния MapState
 */
@Composable
internal expect fun Telemetry(stateFlow: StateFlow<MapState<*>>)
