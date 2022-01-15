package com.map

import kotlin.test.Test
import kotlin.test.assertEquals

class MapStateTest {
    @Test
    fun testCoordinateConversion() {
        val mapState = MapState(width = 100, height = 100)
        val initGeo = GeoPt(0.5, 0.5)
        val displayPt = mapState.geoToDisplay(initGeo)
        assertEquals(Pt(50, 50), displayPt)
        val convertedGeo = mapState.displayLengthToGeo(displayPt)
        assertEquals(initGeo, convertedGeo)
    }

    @Test
    fun testMapSize() {
        val state = MapState(10, 20)
        println(state)
    }
}
