package com.map

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement


@JsExport
@Composable
public fun MapViewBrowser(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    val state by stateFlow.collectAsState()
    TagElement(
        elementBuilder = ElementBuilder.createBuilder("canvas"),
        applyAttrs = {
            attr("width", "${width}px")
            attr("height", "${height}px")
            attr("workaround", state.hashCode().toString())
        },
        content = {
            DisposableRefEffect { element: Element ->
                val canvas = element as HTMLCanvasElement
                canvas.onwheel = {
                    it.preventDefault()//cancel page scrolling
                    onZoom(it.deltaY / 10)
                }
                canvas.onmousemove = {
                    onMove((it.offsetX + 0.999).toInt(), (it.offsetY + 0.999).toInt())
//                    mouseMoveX(it.clientX)
                }
                onDispose {
                    //clear mouse handlers
                    canvas.onmousemove = {}
                    canvas.onwheel = {}
                    println("canvas dispose")
                }
            }
            DomSideEffect(state) { element: Element ->
                val canvas = element as HTMLCanvasElement
                val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
                ctx.fillStyle = "green"//todo
                state.matrix.forEach {
                    it.forEach { t ->
                        val image = t.pic.image.imageBitmap
                        console.log("image", image)
                        js("debugger;")
                        ctx.drawImage(
                            image = image,
                            dx = t.display.x.toDouble(),
                            dy = t.display.y.toDouble(),
                            dw = t.display.size.toDouble(),
                            dh = t.display.size.toDouble()
                        )
                    }
                }
                ctx.rect(0.0, 0.0, width.toDouble(), height.toDouble())//todo
            }
        }
    )
}
