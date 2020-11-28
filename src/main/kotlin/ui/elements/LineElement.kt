package ui.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DesktopCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import ui.core.DrawPageContext
import ui.elements.base.DrawableElement
import ui.elements.base.ElementType
import kotlin.math.abs

class LineElement(
        override val id: Int,
        var el1: DrawableElement,
        var el2: DrawableElement,
        var weight: Int = 1
) : DrawableElement() {
    override val type: ElementType
        get() = TODO("Not yet implemented")
    override var pos: Offset = Offset.Zero //todo: change all that
    override val connectable: Boolean = false
    override val width: Int = 0
    override val height: Int = 0

    override fun draw(scope: DrawScope, context: DrawPageContext) {
        scope.drawLine(Color.Black, el1.center, el2.center, 5f)
        scope.drawIntoCanvas { canvas ->
            (canvas as DesktopCanvas).skija.drawString(weight.toString(),
                    abs((el1.center.x + el2.center.x) / 2f),
                    abs((el1.center.y + el2.center.y) / 2f) - 10,
                    skiaFont, paint.asFrameworkPaint())
        }
    }

}