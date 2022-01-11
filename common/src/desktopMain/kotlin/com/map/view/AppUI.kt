package com.map.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import com.map.model.ContentState
import com.map.style.Gray

@Composable
fun AppUI(content: ContentState) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Gray
    ) {
        MainScreen(content)
    }

}

fun showPopUpMessage(text: String) {
    //todo delete
}
