package com.map.core

import java.awt.image.BufferedImage

interface BitmapFilter {
    fun apply(bitmap: BufferedImage) : BufferedImage
}