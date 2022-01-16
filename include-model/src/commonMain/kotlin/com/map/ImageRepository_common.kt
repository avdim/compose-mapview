package com.map

interface ImageRepository {
    suspend fun getImage(tile: Tile): Picture
}

val Tile.tileUrl get() = "https://api.maptiler.com/maps/streets/$zoom/$x/$y.png?key=$SECRET_API_KEY" //todo вынести в конфиг с json-ом
