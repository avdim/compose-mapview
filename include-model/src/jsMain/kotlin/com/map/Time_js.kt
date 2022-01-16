package com.map

actual inline fun timeMs(): Long = kotlin.js.Date.now().toLong()
