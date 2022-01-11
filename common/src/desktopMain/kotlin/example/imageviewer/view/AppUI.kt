package example.imageviewer.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import example.imageviewer.model.AppState
import example.imageviewer.model.ScreenType
import example.imageviewer.model.ContentState
import example.imageviewer.style.Gray

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
