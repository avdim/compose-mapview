package com.map.mvi

import com.map.model.Picture

data class MainState(
    val pictures: List<Picture>
)

sealed interface Intent {
    class AddPictures(val pictures: List<Picture>) : Intent
}

