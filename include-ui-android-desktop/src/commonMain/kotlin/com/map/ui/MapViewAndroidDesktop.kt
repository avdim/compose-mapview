@file:OptIn(ExperimentalComposeUiApi::class)

package com.map.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.map.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun MapViewAndroidDesktop(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Pt, Double) -> Unit,
    onClick: (Pt) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    var previousMoveDownPos by remember { mutableStateOf<Offset?>(null) }
    var previousPressTime by remember { mutableStateOf(0L) }
    var previousPressPos by remember { mutableStateOf<Offset?>(null) }
    val state by stateFlow.collectAsState()
    Canvas(
        Modifier.size(width.dp, height.dp)
            .pointerInput(Unit) {
                while (true) {
                    val event = awaitPointerEventScope {
                        awaitPointerEvent()
                    }
                    val current = event.changes.firstOrNull()?.position
                    if (event.type == PointerEventType.Scroll) {
                        val scrollY: Float? = event.changes.firstOrNull()?.scrollDelta?.y
                        if (scrollY != null && scrollY != 0f) {
                            onZoom(current?.toPt() ?: Pt(width / 2, height / 2), scrollY * getSensitivity())
                        }
                    } else if (event.type == PointerEventType.Move) {
                        if (event.buttons.isPrimaryPressed) {
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
                    if (event.type == PointerEventType.Press) {
                        previousPressTime = timeMs()
                        previousPressPos = current
                    }
                    if (event.type == PointerEventType.Release) {
                        if (timeMs() - previousPressTime < Config.CLICK_DURATION_MS) {
                            val previous = previousPressPos
                            if (current != null && previous != null) {
                                val dx = current.x - previous.x
                                val dy = current.y - previous.y
                                val distance = sqrt(dx * dx + dy * dy)
                                if (distance < Config.CLICK_AREA_RADIUS_PX) {
                                    onClick(current.toPt())
                                }
                            }
                        }
                    }
                }
            }
    ) {
        state.matrix.forEach {
            it.forEach { t ->
                val topLeft = Offset(t.display.x.toFloat(), t.display.y.toFloat())
                val dstSize = IntSize(t.display.size, t.display.size)
                val dstOffset = IntOffset(t.display.x, t.display.y)
                drawImage(t.pic.toImageBitmap(), dstOffset = dstOffset, dstSize = dstSize)
            }
        }
        drawPath(path = Path().apply {
            addRect(Rect(0f, 0f, width.toFloat(), height.toFloat()))
        }, color = Color.Red, style = Stroke(2f))
    }
//    ScrollableArea(state)
}

@Composable
fun ScrollableArea() {
    data class MainState(
        val pictures: List<Picture>
    )

    val state: MainState = MainState(listOf())
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(end = 8.dp)
    ) {
        val stateVertical = rememberScrollState(0)
        Column(modifier = Modifier.verticalScroll(stateVertical)) {
            var index = 1
            Column {
                for (picture in state.pictures) {
                    Miniature(
                        picture = picture
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    index++
                }
            }
        }
//        VerticalScrollbar(// Desktop
//            adapter = rememberScrollbarAdapter(stateVertical),
//            modifier = Modifier.align(Alignment.CenterEnd)
//                .fillMaxHeight()
//        )
    }
}

@Composable
fun Miniature(
    picture: Picture
) {
    val infoButtonHover = remember { mutableStateOf(false) }
    Card(
        backgroundColor = MiniatureColor,
        modifier = Modifier.padding(start = 10.dp, end = 18.dp).height(150.dp)
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(modifier = Modifier.padding(end = 30.dp)) {
            Clickable(
                onClick = {

                }
            ) {
                Image(
                    picture.toImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.height(70.dp)
                        .width(90.dp)
                        .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 1.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

fun Offset.toPt(): Pt = Pt(ceil(x).roundToInt(), ceil(y).roundToInt())
