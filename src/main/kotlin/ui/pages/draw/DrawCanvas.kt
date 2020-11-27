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
import androidx.compose.ui.input.pointer.pointerMoveFilter
import ui.core.DrawPageContext

@Composable
fun DrawCanvas(modifier: Modifier, context: DrawPageContext) = Canvas(modifier = modifier
        .fillMaxSize()
        .background(Color.White)
        .pointerMoveFilter(onMove = { context.onMouseMove(it) })
        .tapGestureFilter { context.click(it) }
        /*.dragGestureFilter(dragObserver = object : DragObserver { // todo
            override fun onDrag(dragDistance: Offset): Offset {
                context.drag(dragDistance)
                return dragDistance
            }
        })*/
) {
    context.elementsState.value.forEach { it.draw(this) }
}