package com.map

object Config {
    val CLICK_DURATION_MS: Long = 350
    val CLICK_AREA_RADIUS_PX: Int = 3
}

val TILE_SIZE = 512
val MIN_ZOOM = 0
val MAX_ZOOM = 22
val SCROLL_SENSITIVITY_DESKTOP = 0.05
val SCROLL_SENSITIVITY_BROWSER = 0.001
val CACHE_DIR_NAME = "map-view-cache"

fun getSensitivity():Double = SCROLL_SENSITIVITY_DESKTOP
