package com.map

fun Double.toShortString(significantDigits: Int = 5): String {
    var multiplier: Long = 1
    repeat(significantDigits) {
        multiplier *= 10
    }
    val result = (this * multiplier).toLong().toDouble() / multiplier
    return result.toString()
}
