package com.map

import platform.CoreGraphics.*

fun sideEffectAsLoadTile(effect: MapSideEffect):MapSideEffect.LoadTile? {
    return when(effect) {
        is MapSideEffect.LoadTile -> effect
        else -> null
    }
}

fun createIntentTileLoaded(tile:Tile, imageIos:ImageIos /*CGImage*/) = MapIntent.TileImageLoaded<TileImage>(
    tile = tile,
    image = TileImage(
        platformSpecificData = imageIos,
    )
)
