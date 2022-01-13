package com.map

import kotlinx.coroutines.launch

data class MainState(
    val pictures: List<Picture>
)

sealed interface Intent {
    class AddPictures(val pictures: List<Picture>) : Intent
}

fun createMapViewStore(): Store<MainState, Intent> = createStore(MainState(listOf())) { state: MainState, intent: Intent ->
    when (intent) {
        is Intent.AddPictures -> {
            state.copy(pictures = intent.pictures)
        }
    }
}.also { store ->
    getNetworkScope().launch {
        val imageList: List<String> = listOf(
            "https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/1.jpg"
            ,"https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/2.jpg"
        )

        // https://api.maptiler.com/maps/streets/1/0/0.png?key=
        // https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=
        val pictureList: List<Picture> = loadImages(imageList)
        store.send(Intent.AddPictures(pictureList))
    }
}
