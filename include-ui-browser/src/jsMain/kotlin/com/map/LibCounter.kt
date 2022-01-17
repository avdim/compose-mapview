package com.map

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.dom.clear
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement

@Composable
public fun LibJSCounter(stateFlow: StateFlow<Double>, sendIntent: (Double) -> Unit) {
    val state: State<Double> = stateFlow.collectAsState()
    Div({ style { padding(25.px) } }) {
        Button(attrs = {
            onClick {
                sendIntent(1.0)
            }
        }) {
            Text("+++")
        }

        Span({ style { padding(15.px) } }) {
            Text("${state.value}")
        }

        Button(attrs = {
            onClick {
                sendIntent(-1.0)
            }
        }) {
            Text("---")
        }
    }
    Div {
        CanvasWithRect(stateFlow) {
            sendIntent(it)
        }
    }
}

@Composable
private fun CanvasWithRect(stateFlow: StateFlow<Double>, onWheel:(Double)->Unit) {
    val state = stateFlow.collectAsState()
    TagElement(
        elementBuilder = ElementBuilder.createBuilder("canvas"),
        applyAttrs = {
            attr("width", "200px")
            attr("height", "200px")
            attr("workaround", state.value.toString())
        },
        content = {
            DisposableRefEffect { element: Element ->
                val canvas = element as HTMLCanvasElement
                canvas.onwheel = {
                    it.preventDefault()//cancel page scrolling
                    onWheel(it.deltaY / 10)
                }
                canvas.onmousemove = {
//                    mouseMoveX(it.clientX)
                    it.y
                }
                onDispose {
                    //clear mouse handlers
                    canvas.onmousemove = {}
                    canvas.onwheel = {}
                    println("canvas dispose")
                }
            }
            DomSideEffect(state.value) { element: Element ->
                val canvas = element as HTMLCanvasElement
                val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
                ctx.fillStyle = "green";
                ctx.fillRect(state.value.toDouble(), 0.0, 20.0, 20.0)
            }
        }
    )
}
