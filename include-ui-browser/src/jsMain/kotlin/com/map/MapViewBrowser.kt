package com.map

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.MouseEvent


@Composable
public fun MapViewBrowser(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid<TileImage>>,
    onZoom: (Pt?, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    var previousTouchPos by remember { mutableStateOf<Pt?>(null) }
    var isMouseDown by remember { mutableStateOf(false) }
    var previousMouseDownTime by remember { mutableStateOf(0L) }
    var previousMouseMoveDownPos by remember { mutableStateOf<Pt?>(null) }
    var previousMouseMovePos by remember { mutableStateOf(Pt(width / 2, height / 2)) }
    var previousMouseDownPos by remember { mutableStateOf<Pt?>(null) }
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
                    onZoom(previousMouseMovePos, -it.deltaY * SCROLL_SENSITIVITY_BROWSER)
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
                    previousMouseDownPos = it.toPt()
                    previousMouseDownTime = timeMs()
                }
                regListener<MouseEvent>(document, "mouseup") {
                    isMouseDown = false
                    if (timeMs() - previousMouseDownTime < Config.CLICK_DURATION_MS) {
                        val clickDistance = previousMouseDownPos?.distanceTo(it.toPt())
                        if (clickDistance != null && clickDistance < Config.CLICK_AREA_RADIUS_PX) {
                            onClick(it.toPt())
                        }
                    }
                }
                regListener<MouseEvent>(canvas, "mousemove") {
                    previousMouseMovePos = it.toPt()
                    if (isMouseDown) {
                        val previous = previousMouseMoveDownPos
                        val next = it.toPt()
                        if (previous != null) {
                            val dx = (next.x - previous.x).toInt()
                            val dy = (next.y - previous.y).toInt()
                            if (dx != 0 || dy != 0) {
                                onMove(dx, dy)
                            }
                        }
                        previousMouseMoveDownPos = next
                    } else {
                        previousMouseMoveDownPos = null
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
                state.matrix.forEach { (t, img) ->
                    if (img != null) {
                        ctx.drawImage(
                            image = img.extract(),
                            dx = t.x.toDouble(),
                            dy = t.y.toDouble(),
                            dw = t.size.toDouble(),
                            dh = t.size.toDouble(),
                            sx = img.offsetX.toDouble(),
                            sy = img.offsetY.toDouble(),
                            sw = img.cropSize.toDouble(),
                            sh = img.cropSize.toDouble()
                        )
                    }
                }
            }
        }
    )
}

private class ListenerData(val target: EventTarget, val type: String, val callback: EventListener)
