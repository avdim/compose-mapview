package com.map

interface ImageRepository {
    suspend fun getImage(tile: Tile): Picture
}

fun createImageRepository() = decorateWithInMemoryCache(decorateWithDiskCache(createDownloadImageRepository()))

expect fun createDownloadImageRepository():ImageRepository
expect fun decorateWithInMemoryCache(imageRepository: ImageRepository):ImageRepository
expect fun decorateWithDiskCache(imageRepository: ImageRepository):ImageRepository

val Tile.tileUrl get() = "https://api.maptiler.com/maps/streets/$zoom/$x/$y.png?key=$SECRET_API_KEY"
