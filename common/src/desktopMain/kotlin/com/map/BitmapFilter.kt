package com.map

import java.awt.image.BufferedImage

interface BitmapFilter {
    fun apply(bitmap: BufferedImage) : BufferedImage
}