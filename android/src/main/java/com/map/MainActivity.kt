package com.map

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.map.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapView(
                modifier = Modifier.fillMaxSize(),
                mapTilerSecretKey = MAPTILER_SECRET_KEY,
            )
        }
    }
}
