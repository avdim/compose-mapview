package com.map.model

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.map.common.R
import com.map.model.Miniatures
import com.map.model.loadImages
import com.map.utils.clearCache
import com.map.utils.isInternetAvailable
import com.map.view.showPopUpMessage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object ContentState {

    private lateinit var context: Context

    fun applyContent(context: Context): ContentState {
        ContentState.context = context
        initData()
        return this
    }

    private val executor: ExecutorService by lazy { Executors.newFixedThreadPool(2) }

    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    fun getOrientation(): Int {
        return context.resources.configuration.orientation
    }

    private val isAppReady = mutableStateOf(false)
    fun isAppReady(): Boolean {
        return isAppReady.value
    }

    private val isContentReady = mutableStateOf(false)
    fun isContentReady(): Boolean {
        return isContentReady.value
    }

    fun getString(id: Int): String {
        return context.getString(id)
    }

    private val _miniatures = mutableStateOf(Miniatures())
    fun getMiniatures(): State<Miniatures> {
        return _miniatures
    }

    // application content initialization
    private fun initData() {
        val directory = context.cacheDir.absolutePath

        executor.execute {
            val imageList =  listOf(
                "https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/1.jpg"
                ,"https://raw.githubusercontent.com/JetBrains/compose-jb/master/artwork/imageviewerrepo/2.jpg"
            )

            val pictureList = loadImages(directory, imageList)

            val picture = loadFullImage(imageList[0])

            handler.post {
                _miniatures.value = pictureList
                onContentReady()
            }
        }
    }

    private fun onContentReady() {
        isContentReady.value = true
        isAppReady.value = true
    }

}
