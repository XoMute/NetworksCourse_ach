package ui.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DesktopCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import core.Node
import ui.core.DrawPageContext
import ui.elements.base.DrawableElement
import ui.elements.base.ElementType
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val THRESHOLD = 10f

class ChannelElement(
        override val id: Int,
        var el1: DrawableElement,
        var el2: DrawableElement,
        var weight: Int = 1
) : DrawableElement() {
    override var pos: Offset = Offset.Zero //todo: change
    override val width: Int = 0
    override val height: Int = 0
    override val type = ElementType.CHANNEL
    var lineType: ChannelType = ChannelType.DUPLEX
    var errorProbability: Float = 0f

    override fun collides(offset: Offset): Boolean {
        return distanceTo(offset) <= THRESHOLD
    }

    private fun distanceTo(offset: Offset): Float {
        val start = el1.center
        val end = el2.center
        val l2 = distance(start, end)
        var t = ((offset.x - start.x) * (end.x - start.x) + (offset.y - start.y) * (end.y - start.y)) / l2
        t = max(0f, min(1f, t))
        return distance(offset, Offset(
                start.x + t * (end.x - start.x),
                start.y + t * (end.y - start.y)
        ))
    }

    private fun distance(p1: Offset, p2: Offset): Float {
        return sqr(p1.x - p2.x) + sqr(p1.y - p2.y)
    }

    private fun sqr(x: Float): Float = x * x

    override fun draw(scope: DrawScope, context: DrawPageContext) {
        scope.drawLine(Color.Black, el1.center, el2.center, 5f)
        scope.drawIntoCanvas { canvas ->
            (canvas as DesktopCanvas).skija.drawString(weight.toString(),
                    abs((el1.center.x + el2.center.x) / 2f),
                    abs((el1.center.y + el2.center.y) / 2f) - 10,
                    skiaFont, paint.asFrameworkPaint())
        }
    }

    override fun toString(): String {
        return "Line\nWeight: $weight\nType: $lineType\nError probability: $errorProbability"
    }
}

enum class ChannelType {
    DUPLEX, HALF_DUPLEX
}