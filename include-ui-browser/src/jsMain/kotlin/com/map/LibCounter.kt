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
public fun LibJSCounter(stateFlow: StateFlow<Int>, sendIntent: (Int) -> Unit) {
    val state: State<Int> = stateFlow.collectAsState()
    Div({ style { padding(25.px) } }) {
        Button(attrs = {
            onClick {
                sendIntent(1)
            }
        }) {
            Text("+++")
        }

        Span({ style { padding(15.px) } }) {
            Text("${state.value}")
        }

        Button(attrs = {
            onClick {
                sendIntent(-1)
            }
        }) {
            Text("---")
        }
    }
    Div {
        CanvasWithRect(stateFlow)
    }
}

@Composable
private fun CanvasWithRect(stateFlow: StateFlow<Int>) {
    val state = stateFlow.collectAsState()
    TagElement(
        elementBuilder = ElementBuilder.createBuilder("canvas"),
        applyAttrs = {
            val width = 200 + state.value * 10
//            attr("width", "${width}px")
            attr("width", "200px")
            attr("height", "200px")
            attr("workaround", state.value.toString())
        },
        content = {
            DomSideEffect(state.value) { element: Element ->
                val canvas = element as HTMLCanvasElement
                val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
                ctx.fillStyle = "green";
                val size = 20.0 + state.value * 5
                ctx.fillRect(0.0, 0.0, size, size)
            }
        }
    )
}
