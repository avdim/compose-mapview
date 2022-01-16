package com.map

import kotlin.test.Test

class CalcTilesTest {

    @Test
    fun testZoom1() {
        val state = MapState(100, 100, 1.0)
        val tiles = state.calcTiles()
        println(tiles.toString())
    }

}

