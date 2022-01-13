package com.map

interface Repository<T> {
    fun get() : T
}
