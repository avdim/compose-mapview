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
//                latitude = 59.999394,
//                longitude = 29.745412,
//                startScale = 840.0,
            )
        }
    }
}
