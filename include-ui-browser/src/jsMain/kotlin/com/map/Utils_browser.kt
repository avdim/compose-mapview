package com.map

import org.w3c.dom.events.MouseEvent
import kotlin.math.ceil

fun MouseEvent.toPt(): Pt = Pt(ceil(x).toInt(), ceil(y).toInt())
fun TileImage.extract() = platformSpecificData
