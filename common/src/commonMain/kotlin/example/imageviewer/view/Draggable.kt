package example.imageviewer.view

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import example.imageviewer.core.EventLocker
import example.imageviewer.style.Transparent

@Composable
fun Draggable(
    dragHandler: DragHandler = remember { DragHandler() },
    modifier: Modifier = Modifier,
    onUpdate: (() -> Unit) = {  },
    children: @Composable() () -> Unit
) {
    val offsetPoint = dragHandler.getAmount()
    Surface(
        color = Transparent,
        modifier = modifier.offset(offsetPoint.x.dp, offsetPoint.y.dp).pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { dragHandler.reset() },
                onDragEnd = { dragHandler.reset() },
                onDragCancel = { dragHandler.cancel() },
            ) { change, dragAmount ->
                dragHandler.drag(dragAmount)
                onUpdate.invoke()
                change.consumePositionChange()
            }
        }
    ) {
        children()
    }
}

class DragHandler {

    private val amount = mutableStateOf(Point(0f, 0f))
    private val distance = mutableStateOf(Point(0f, 0f))
    private val locker: EventLocker = EventLocker()

    fun getAmount(): Point {
        return amount.value
    }

    fun getDistance(): Point {
        return distance.value
    }

    fun reset() {
        distance.value = Point(Offset.Zero)
        locker.unlock()
    }

    fun cancel() {
        distance.value = Point(Offset.Zero)
        locker.lock()
    }

    fun drag(dragDistance: Offset) {
        val locked = locker.isLocked()
        if (locked) {
            val dx = dragDistance.x
            val dy = dragDistance.y
            
            distance.value = Point(distance.value.x + dx, distance.value.y + dy)
            amount.value = Point(amount.value.x + dx, amount.value.y + dy)
        }
    }
}

class Point {
    var x: Float = 0f
    var y: Float = 0f
    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
    constructor(point: Offset) {
        this.x = point.x
        this.y = point.y
    }
    fun setAttr(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
