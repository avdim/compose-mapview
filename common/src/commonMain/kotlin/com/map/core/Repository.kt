package com.map.core

interface Repository<T> {
    fun get() : T
}
