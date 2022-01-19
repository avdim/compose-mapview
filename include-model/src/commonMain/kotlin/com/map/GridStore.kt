package com.map

import kotlinx.coroutines.CoroutineScope
import kotlin.math.max

sealed interface SideEffectGrid {
    class LoadTile(val displayTile: DisplayTile, val tile: Tile) : SideEffectGrid
}

sealed interface IntentGrid {
    class NewTiles(val grid: TilesGrid) : IntentGrid
    class TileLoaded(val tile: ImageTile) : IntentGrid
}

//todo move to model
fun CoroutineScope.createGridStore(
    effectHandler: (store: Store<ImageTilesGrid, IntentGrid>, SideEffectGrid) -> Unit
) = createStoreWithSideEffect(
    ImageTilesGrid(emptyMap()),
    effectHandler = effectHandler
) { state, intent: IntentGrid ->
    when (intent) {
        is IntentGrid.NewTiles -> {
            state.copy(
                matrix = intent.grid.matrix.map { it.first to tilesHashMap.searchCropAndPut(it.second) }.toMap()
            ).addSideEffects(
                intent.grid.matrix.map {
                    SideEffectGrid.LoadTile(it.first, it.second)
                }
            )
        }
        is IntentGrid.TileLoaded -> {
            if (state.matrix.containsKey(intent.tile.display)) {
                val previous = state.matrix[intent.tile.display]
                if (previous == null || previous.isBadQuality) {
                    if (previous != null) {
                        if(state.matrix.size > 64) {
                            println("state.matrix.size: ${state.matrix.size}")
                        }
                    }
                    state.copy(
                        matrix = state.matrix.toMutableMap().apply {
                            put(intent.tile.display, intent.tile.image)
                        }
                    )
                } else {
                    state
                }
            } else {
                state
            }.noSideEffects()
        }
    }
}

val tilesHashMap: MutableMap<Tile, GpuOptimizedImage> = createConcurrentMap()//todo global mutable state

fun MutableMap<Tile, GpuOptimizedImage>.searchCropAndPut(tile1: Tile): GpuOptimizedImage? {
    //todo unit tests
    val img1 = get(tile1)
    if (img1 != null) {
        return img1
    }
    var zoom = tile1.zoom
    var x = tile1.x
    var y = tile1.y
    while (zoom > 0) {
        zoom--
        x /= 2
        y /= 2
        val tile2 = Tile(zoom, x, y)
        val img2 = get(tile2)
        if (img2 != null) {
            val deltaZoom = tile1.zoom - tile2.zoom
            val i = tile1.x - (x shl deltaZoom)
            val j = tile1.y - (y shl deltaZoom)
            val size = max(TILE_SIZE ushr deltaZoom, 1)
            val cropImg = img2.cropAndRestoreSize(i * size, j * size, size)
            put(tile1, cropImg)
            return cropImg
        }
    }
    return null
}
