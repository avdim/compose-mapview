package com.map

object Config {
    val SIMULATE_NETWORK_PROBLEMS = false
    val SCALE_ONLY_WITH_CROP = false
    val CLICK_DURATION_MS: Long = 300
    val CLICK_AREA_RADIUS_PX: Int = 7
    val ZOOM_ON_CLICK = 0.8 * 40 //todo SCALE_ON_ZOOM как множитель
    val MAX_SCALE_ON_SINGLE_ZOOM_EVENT = 2.0 * 40
}

val TILE_SIZE = 512
val MIN_ZOOM = 0
val MAX_ZOOM = 22
val SCROLL_SENSITIVITY_DESKTOP = 0.05
val SCROLL_SENSITIVITY_BROWSER = 0.001
val CACHE_DIR_NAME = "map-view-cache"

fun getSensitivity():Double = SCROLL_SENSITIVITY_DESKTOP
