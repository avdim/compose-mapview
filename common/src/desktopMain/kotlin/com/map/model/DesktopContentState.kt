package com.map.model

import com.map.mvi.createMapViewStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object ContentState {
    val scope = CoroutineScope(Dispatchers.IO)
    private val store = createMapViewStore()
    val stateFlow = store.stateFlow
}
