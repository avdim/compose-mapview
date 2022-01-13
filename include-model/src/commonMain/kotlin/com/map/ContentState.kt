package com.map


object ContentState {
    private val store = createMapViewStore()
    val stateFlow = store.stateFlow
}
