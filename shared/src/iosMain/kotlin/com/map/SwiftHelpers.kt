package com.map

fun sideEffectAsLoadTile(effect: MapSideEffect):MapSideEffect.LoadTile? {
    return when(effect) {
        is MapSideEffect.LoadTile -> effect
        else -> null
    }
}
