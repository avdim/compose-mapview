package com.map

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoPtTest {
    @Test
    fun testGeoToLatLonAndBack() {
        val pt = GeoPt(0.58, 0.29)
        val modified = createGeoPt(pt.latitude, pt.longitude)
        println("     pt: $pt")
        println("modified: $modified")
        assertTrue(abs(pt.x - modified.x) < 0.00001)
        assertTrue(abs(pt.y - modified.y) < 0.00001)
    }

    @Test
    fun testKotlinIslandLocation() {
        val pt = GeoPt(0.5826261444444444, 0.2904030074963381)
        val kotlinPt = createGeoPt(
            latitude = 59.999394,
            longitude = 29.745412,
        )
        println("      pt: $pt")
        println("kotlinPt: $kotlinPt")
        assertEquals(pt, kotlinPt)
    }

}
