package com.map

val TILE_SIZE = 512
val MAX_ZOOM = 22
val SCROLL_SENSITIVITY_DESKTOP = 0.05
val USE_FAKE_REPOSITORY_ON_DEKSTOP = false
val CACHE_DIR_NAME = "map-view-cache"

fun getSensitivity():Double = SCROLL_SENSITIVITY_DESKTOP
