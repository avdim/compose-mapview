package com.map

interface ImageRepository {
    suspend fun getImage(tile: Tile): Picture
}

fun createImageRepository() = decorateWithInMemoryCache(decorateWithDiskCache(createDownloadImageRepository()))

expect fun createDownloadImageRepository():ImageRepository
expect fun decorateWithInMemoryCache(imageRepository: ImageRepository):ImageRepository
expect fun decorateWithDiskCache(imageRepository: ImageRepository):ImageRepository
