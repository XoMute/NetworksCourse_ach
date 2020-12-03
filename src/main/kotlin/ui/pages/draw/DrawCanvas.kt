package ui.pages.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerMoveFilter
import ui.core.AppContext
import ui.elements.base.DrawableElement
import ui.elements.base.ElementType

@Composable
fun DrawCanvas(context: AppContext, modifier: Modifier = Modifier) = Canvas(modifier = modifier
        .fillMaxSize()
        .background(Color.White)
        .pointerMoveFilter(onMove = { context.onMouseMove(it) })
        .tapGestureFilter { context.click(it) }
        .dragGestureFilter(dragObserver = object : DragObserver {
            override fun onStart(downPosition: Offset) {
                super.onStart(downPosition)
                println("Starting drag at $downPosition")
            }

            override fun onStop(velocity: Offset) {
                super.onStop(velocity)
                println("Stopping drag with velocity $velocity")
            }

            override fun onDrag(dragDistance: Offset): Offset {
                println("Dragged $dragDistance")
                return dragDistance
            }
        })
) {
    context.elementsState.value.forEach { (it as DrawableElement).draw(this, context) }
    context.drawChannel(this)
    context.packageState.value?.draw(this)
}

fun AppContext.drawChannel(scope: DrawScope) {
    if (selectedTypeState.value == ElementType.CHANNEL && connectingElementsState.value) {
        selectedElementState.value!!.let {
            scope.drawLine(if (satelliteChannelState.value) Color.Blue else Color.Black, (it as DrawableElement).center, mousePosState.value, 3f)
        }
    }
}