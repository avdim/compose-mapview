package com.map.mvi

import com.map.model.Picture

data class State(
    val pictures: List<Picture>
)

sealed interface Intent {
    class AddPictures(val pictures: List<Picture>) : Intent
}

fun createMapViewStore() = createStore(State(listOf())) { state: State, intent: Intent ->
    when (intent) {
        is Intent.AddPictures -> {
            state.copy(pictures = intent.pictures)
        }
    }
}
