package com.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.map.view.AppUI
import com.map.model.ContentState
import com.map.model.ImageRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = ContentState
        setContent {
            AppUI(content)
        }
    }
}
