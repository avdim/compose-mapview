package com.map

import org.w3c.dom.Image

actual typealias AbstractImageData = Image

actual fun Picture.scale(width: Int, height: Int): Picture {
    return this //todo scale
}
