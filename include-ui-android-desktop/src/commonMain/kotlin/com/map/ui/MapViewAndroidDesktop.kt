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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.map.*
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.ceil

@Composable
fun MapViewAndroidDesktop(
    width: Int,
    height: Int,
    stateFlow: StateFlow<ImageTilesGrid>,
    onZoom: (Double) -> Unit,
    onMove: (Int, Int) -> Unit
) {
    var previousMousePos by remember { mutableStateOf<Offset?>(null) }
    val state by stateFlow.collectAsState()
    Canvas(
        Modifier.size(width.dp, height.dp)
            .pointerInput(Unit) {
                while (true) {
                    val event = awaitPointerEventScope {
                        awaitPointerEvent()
                    }
                    if (event.type == PointerEventType.Scroll) {
                        val scrollX = event.changes.firstOrNull()?.scrollDelta?.x
                        val scrollY: Float? = event.changes.firstOrNull()?.scrollDelta?.y
                        if (scrollY != null && scrollY != 0f) {
                            onZoom(scrollY * getSensitivity())
                        }
                    } else if (event.type == PointerEventType.Move) {
                        if (event.buttons.isPrimaryPressed) {
                            val previous = previousMousePos
                            val next = event.changes.firstOrNull()?.position
                            if (previous != null && next != null) {
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

