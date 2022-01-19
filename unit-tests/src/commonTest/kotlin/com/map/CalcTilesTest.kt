package com.map

import kotlin.test.Test
import kotlin.test.assertEquals

class CalcTilesTest {

    @Test
    fun testSingleTile() {
        val state = MapState<Unit>(512, 512, 1.0)
        val tiles = state.calcTiles()
        assertEquals(1, tiles.size)
    }

}

