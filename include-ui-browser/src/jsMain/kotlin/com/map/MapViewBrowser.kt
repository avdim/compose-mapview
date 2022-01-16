package com.map

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import kotlin.math.ceil


@JsExport
@Composable
public fun MapViewBrowser(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    var isMouseDown by remember { mutableStateOf(false) }
    var previousMousePos by remember { mutableStateOf<Pt?>(null) }
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
                    onZoom(it.deltaY * SCROLL_SENSITIVITY_BROWSER)
                }
                canvas.addEventListener(type = "mousedown", callback = {
                    isMouseDown = true
                })
                document.addEventListener(type = "mouseup", callback = {
                    isMouseDown = false
                })
                canvas.onmousemove = {
                    if (isMouseDown) {
                        val previous = previousMousePos
                        val next = Pt(ceil(it.x).toInt(), ceil(it.y).toInt())
                        if (previous != null) {
                            val dx = (next.x - previous.x).toInt()
                            val dy = (next.y - previous.y).toInt()
                            if (dx != 0 || dy != 0) {
                                onMove(dx, dy)
                            }
                        }
                        previousMousePos = next
                    } else {
                        previousMousePos = null
                    }
                }
                canvas.ondrag = {
                    val dx:Double = it.asDynamic().movementX
                    val dy:Double = it.asDynamic().movementY
                    onMove(ceil(dx).toInt(), ceil(dy).toInt())
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
                        ctx.drawImage(
                            image = t.pic.image.imageBitmap,
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
