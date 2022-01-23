package com.map

import platform.CoreGraphics.*

fun sideEffectAsLoadTile(effect: MapSideEffect): MapSideEffect.LoadTile? {
    return when (effect) {
        is MapSideEffect.LoadTile -> effect
        else -> null
    }
}

fun createIntentTileLoaded(tile: Tile, imageIos: ImageIos /*CGImage*/) = MapIntent.TileImageLoaded<TileImage>(
    tile = tile,
    image = TileImage(
        platformSpecificData = imageIos,
    )
)

fun createIntentMove(x: Int, y: Int) = MapIntent.Input.Move<TileImage>(Pt(x, y))

fun createTileUrl(tile: Tile): String {
    return Config.createTileUrl(tile.zoom, tile.x, tile.y, mapTilerSecretKey = MAPTILER_SECRET_KEY)
}

fun extract(tileImage: TileImage) = tileImage.platformSpecificData
