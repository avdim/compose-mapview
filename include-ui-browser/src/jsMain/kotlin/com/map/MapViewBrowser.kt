package com.map

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.MouseEvent
import kotlin.math.ceil


@JsExport
@Composable
public fun MapViewBrowser(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onZoomAnimate: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    var previousTouchPos by remember { mutableStateOf<Pt?>(null) }
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

                val activeListeners: MutableList<ListenerData> = mutableListOf()
                fun <T : Event> regListener(target: EventTarget, type: String, lambda: (T) -> Unit) {
                    val callback = EventListener {
                        lambda(it as T)
                    }
                    target.addEventListener(type, callback = callback)
                    activeListeners.add(ListenerData(target, type, callback))
                }
                regListener<MouseEvent>(canvas, "mousedown") {
                    isMouseDown = true
                }
                regListener<MouseEvent>(document, "mouseup") {
                    isMouseDown = false
                }
                regListener<MouseEvent>(canvas, "mousemove") { it ->
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
                regListener<TouchEvent>(canvas, "touchmove") {
                    if (it.changedTouches.length > 0) {
                        val touch: Touch = it.changedTouches[0]!!
                        val previous = previousTouchPos
                        val next = Pt(touch.screenX, touch.screenY)
                        if (previous != null) {
                            val dx = next.x - previous.x
                            val dy = next.y - previous.y
                            if (dx != 0 || dy != 0) {
                                onMove(dx, dy)
                            }
                        }
                        previousTouchPos = next
                    }
                }
                regListener<TouchEvent>(document, "touchend") {
                    previousTouchPos = null
                }
                regListener<Event>(canvas, "click") {
                    onZoomAnimate(2.0)
                }
                onDispose {
                    //clear event handlers
                    activeListeners.forEach {
                        it.target.removeEventListener(it.type, it.callback)
                    }
                    println("MapView disposed")
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

private class ListenerData(val target: EventTarget, val type: String, val callback: EventListener)
