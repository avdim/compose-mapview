package com.map.model

import com.map.mvi.createMapViewStore


object ContentState {
    private val store = createMapViewStore()
    val stateFlow = store.stateFlow
}
