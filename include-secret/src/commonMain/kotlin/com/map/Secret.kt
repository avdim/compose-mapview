package com.map

/**
 * In file: local.properties, set key:    mapTilerSecretKey=xXxXxXxXxXxXx

 * Here you can get this key: https://cloud.maptiler.com/maps/streets/  (register and look at url field ?key=...#)
 *
 * Ключ генерируется плагином, его значение задаётся в local.properties корневого проекта
 * Этот секретный API ключ используется только в sample приложениях.
 * Библиотека MapView принимает этот ключ как аргумент.
 * Подробнее в README.md
 */
const val MAPTILER_SECRET_KEY:String = GeneratedSecretConfig.GENERATED_MAPTILER_SECRET_KEY

