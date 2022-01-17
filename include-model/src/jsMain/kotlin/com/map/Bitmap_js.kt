package com.map

import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob

fun GpuOptimizedImage.getImageBitmap() = platformSpecificData as ImageBitmap
