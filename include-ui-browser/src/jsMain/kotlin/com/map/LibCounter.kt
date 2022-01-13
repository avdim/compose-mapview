package com.map

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement

@JsExport
@Composable
public fun LibJSCounter() {
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
    Div {
        CanvasWithRect(count)
    }
}

@Composable
private fun CanvasWithRect(level: Int) {
    TagElement(
        elementBuilder = ElementBuilder.createBuilder("canvas"),
        applyAttrs = {
            attr("width", "200px")
            attr("height", "200px")
        },
        content = {
            DomSideEffect(level) { element: Element ->
                val canvas = element as HTMLCanvasElement
                val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
                ctx.fillStyle = "green";
                val size = 20.0 + level * 5
                ctx.fillRect(0.0, 0.0, size, size)
            }
        }
    )
}
