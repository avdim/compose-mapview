package com.map.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import example.imageviewer.ResString
import com.map.utils.cacheImagePath
import com.map.view.showPopUpMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object ContentState {
    lateinit var windowState: WindowState
    val scope = CoroutineScope(Dispatchers.IO)

    fun applyContent(state: WindowState): ContentState {
        windowState = state
        initData()

        return this
    }

    private val _miniatures = mutableStateOf(Miniatures())
    fun getMiniatures(): State<Miniatures> {
        return _miniatures
    }

    // application content initialization
    private fun initData() {
        val directory = File(cacheImagePath)
        if (!directory.exists()) {
            directory.mkdir()
        }

        scope.launch(Dispatchers.IO) {
            val imageList: List<String> = listOf(
                "https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/1.jpg"
                ,"https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/2.jpg"
            )

            // https://api.maptiler.com/maps/streets/1/0/0.png?key=
            // https://api.maptiler.com/maps/streets/{z}/{x}/{y}.png?key=
            val pictureList = loadImages(cacheImagePath, imageList)
            if (pictureList.isEmpty()) {
                showPopUpMessage(
                    ResString.repoEmpty
                )
            } else {
                val picture = loadFullImage(imageList[0])
                _miniatures.value = pictureList
            }
        }
    }

}
