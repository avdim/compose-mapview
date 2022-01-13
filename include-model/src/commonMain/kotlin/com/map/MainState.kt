package com.map

import kotlinx.coroutines.launch

data class MainState(
    val pictures: List<Picture>
)

sealed interface Intent {
    class AddPictures(val pictures: List<Picture>) : Intent
}

fun createMapViewStore(): Store<MainState, Intent> =
    createStore(MainState(listOf())) { state: MainState, intent: Intent ->
        when (intent) {
            is Intent.AddPictures -> {
                state.copy(pictures = intent.pictures)
            }
        }
    }.also { store ->
        getNetworkScope().launch {
            val imageList: List<String> = listOf(
                "https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/1.jpg",
                "https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/2.jpg"
            )

            // https://api.maptiler.com/maps/streets/1/0/0.png?key=
            // https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=
            val pictureList: List<Picture> = loadImages(imageList)
            store.send(Intent.AddPictures(pictureList))
        }
    }

/**
 * sample in js http://jsfiddle.net/84P9r/
 */
data class MapState(
    /**
     * 0.0 - (min zoom) .. 1.0 (max zoom)
     */
    val zoom: Double = 0.1,
    /**
     * Latitude -90(South) .. 90(North)
     */
    val lat: Double = 0.0,
    /**
     * Longitude -180(Left) .. 180(Right)
     */
    val lon: Double = 0.0
)
