package com.map

import kotlin.test.Test

class CalcTilesTest {

    @Test
    fun testZoom1() {
        val state1 = MapState(100, 100, 1.0)

    }

    @Test
    fun testZoom2() {
        //todo
//        calcTiles()
        val state1 = MapState(600, 600, 2.0, topLeft = GeoPt(0.2, 0.1255))
        val state2 = state1.copy(topLeft = GeoPt(0.2, 0.1245))
        val tiles1 = state1.calcTiles()
        val tiles2 = state2.calcTiles()
        println("debug")
    }
}

