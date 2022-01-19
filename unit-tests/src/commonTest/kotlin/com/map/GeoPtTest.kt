package com.map

import kotlin.test.Test
import kotlin.test.assertEquals

class GeoPtTest {
    @Test
    fun testGeoToLatLonAndBack() {
        val pt = GeoPt(0.58, 0.29)
        val modified = createGeoPt(pt.latitude, pt.longitude)
        println("     pt: $pt")
        println("modified: $modified")
        assertEquals(pt, modified)
    }

    @Test
    fun testKotlinIslandLocation() {
        val pt = GeoPt(0.58, 0.29)
        val kotlinPt = createGeoPt(
            latitude = 59.999394,
            longitude = 29.745412,
        )
        println("      pt: $pt")
        println("kotlinPt: $kotlinPt")
    }

}
