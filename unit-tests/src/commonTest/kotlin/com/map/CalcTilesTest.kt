package com.map

import kotlin.test.Test
import kotlin.test.assertEquals

class CalcTilesTest {

    @Test
    fun testSingleTile() {
        val state = MapState<Unit>(512, 512)
        val tiles = state.calcTiles()
        assertEquals(1, tiles.size)
    }

    @Test
    fun test2x2TilesWithZoom() {
        val state = MapState<Unit>(1024, 1024)
        val tiles = state.calcTiles()
        assertEquals(4, tiles.size)
    }

}
