@file:OptIn(ExperimentalComposeUiApi::class)

package com.map.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.map.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun MapViewAndroidDesktop(
    modifier: Modifier,
    isInTouchMode: Boolean,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Pt?, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit,
    updateSize: (width: Int, height: Int) -> Unit
) {
    var previousMoveDownPos by remember { mutableStateOf<Offset?>(null) }
    var previousPressTime by remember { mutableStateOf(0L) }
    var previousPressPos by remember { mutableStateOf<Offset?>(null) }
    val state by stateFlow.collectAsState()

    fun Modifier.applyPointerInput() = pointerInput(Unit) {
        while (true) {
            val event = awaitPointerEventScope {
                awaitPointerEvent()
            }
            val current = event.changes.firstOrNull()?.position
            if (event.type == PointerEventType.Scroll) {
                val scrollY: Float? = event.changes.firstOrNull()?.scrollDelta?.y
                if (scrollY != null && scrollY != 0f) {
                    onZoom(current?.toPt(), -scrollY * getSensitivity())
                }
            }
            when (event.type) {
                PointerEventType.Move -> {
                    if (event.buttons.isPrimaryPressed || isInTouchMode) {
                        val previous = previousMoveDownPos
                        if (previous != null && current != null) {
                            val dx = (current.x - previous.x).toInt()
                            val dy = (current.y - previous.y).toInt()
                            if (dx != 0 || dy != 0) {
                                onMove(dx, dy)
                            }
                        }
                        previousMoveDownPos = current
                    } else {
                        previousMoveDownPos = null
                    }
                }
                PointerEventType.Press -> {
                    previousPressTime = timeMs()
                    previousPressPos = current
                    previousMoveDownPos = current
                }
                PointerEventType.Release -> {
                    if (!isInTouchMode) {
                        if (timeMs() - previousPressTime < Config.CLICK_DURATION_MS) {
                            val previous = previousPressPos
                            if (current != null && previous != null) {
                                if (current.distanceTo(previous) < Config.CLICK_AREA_RADIUS_PX) {
                                    onClick(current.toPt())
                                }
                            }
                        }
                    }
                    previousPressTime = timeMs()
                    previousMoveDownPos = null
                }
            }
        }
    }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        previousMoveDownPos = null
        onMove(offsetChange.x.roundToInt(), offsetChange.y.roundToInt())
        onZoom(null, zoomChange.toDouble() - 1)
    }

    fun Modifier.applyTouchScreenHandlers(): Modifier {
        return transformable(
            transformableState
        ).pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    if (timeMs() - previousPressTime < Config.CLICK_DURATION_MS) {
                        val previous = previousPressPos
                        if (previous != null && previous.distanceTo(it) < Config.CLICK_AREA_RADIUS_PX) {
                            onClick(it.toPt())
                        }
                    }
                    previousPressTime = timeMs()
                    previousMoveDownPos = null
                }
            )
        }
    }

    Canvas(
        modifier.applyPointerInput()
            .run {
                if (isInTouchMode) {
                    applyTouchScreenHandlers()
                } else {
                    this
                }
            }
    ) {
        updateSize(size.width.toInt(), size.height.toInt())
        clipRect() {
            state.matrix.forEach { t ->
                val size = IntSize(t.display.size, t.display.size)
                val position = IntOffset(t.display.x, t.display.y)
                drawImage(t.image.get(), dstOffset = position, dstSize = size)
            }
        }
        drawPath(path = Path().apply {
            addRect(Rect(0f, 0f, size.width, size.height))
        }, color = Color.Red, style = Stroke(4f))
    }
//    ScrollableArea(state)
}

@Composable
fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Blue)
            .fillMaxSize()
    )
}

fun Offset.toPt(): Pt = Pt(ceil(x).roundToInt(), ceil(y).roundToInt())
fun Offset.distanceTo(other: Offset): Double {
    val dx = other.x - x
    val dy = other.y - y
    return sqrt(dx * dx + dy * dy).toDouble()
}
