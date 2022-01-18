package com.map

object Config {
    val SIMULATE_NETWORK_PROBLEMS = false
    val TRY_SCALE_WITH_CROP = false
    val CLICK_DURATION_MS: Long = 300
    val CLICK_AREA_RADIUS_PX: Int = 7
}

val TILE_SIZE = 512
val MIN_ZOOM = 0
val MAX_ZOOM = 22
val SCROLL_SENSITIVITY_DESKTOP = 0.05
val SCROLL_SENSITIVITY_BROWSER = 0.001
val CACHE_DIR_NAME = "map-view-cache"

fun getSensitivity():Double = SCROLL_SENSITIVITY_DESKTOP
