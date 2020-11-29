package ui.pages.draw

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerMoveFilter
import ui.core.DrawPageContext
import ui.elements.base.DrawableElement
import ui.elements.base.ElementType

@Composable
fun DrawCanvas(context: DrawPageContext, modifier: Modifier = Modifier) = Canvas(modifier = modifier
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
    context.elementsState.value.forEach { (it as DrawableElement).draw(this, context) }
    context.drawLine(this)
}

fun DrawPageContext.drawLine(scope: DrawScope) {
    if (selectedTypeState.value == ElementType.LINE && connectingElementsState.value) {
        selectedElementState.value!!.let {
            scope.drawLine(Color.Black, (it as DrawableElement).center, mousePosState.value, 5f)
        }
    }
}