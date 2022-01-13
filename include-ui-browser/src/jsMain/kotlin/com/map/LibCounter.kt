package com.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text

@JsExport
@Composable
public fun LibCounter() {
    var count: Int by mutableStateOf(0)

    Div({ style { padding(25.px) } }) {
        Button(attrs = {
            onClick {
                count = count + 1
            }
        }) {
            Text("+++")
        }

        Span({ style { padding(15.px) } }) {
            Text("$count")
        }

        Button(attrs = {
            onClick {
                count = count - 1
            }
        }) {
            Text("---")
        }
    }
}
