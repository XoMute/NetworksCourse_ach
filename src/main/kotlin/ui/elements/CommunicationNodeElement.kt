package ui.elements

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DesktopCanvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.unit.IntOffset
import ui.core.DrawPageContext
import ui.elements.base.ConnectableElement
import ui.elements.base.ElementType

class CommunicationNodeElement : ConnectableElement {

    override val id: Int
    override var pos: Offset
    override val type: ElementType
        get() = TODO("Not yet implemented")
    override val connectionIds: MutableState<MutableSet<Int>> = mutableStateOf(mutableSetOf())
    override val connectable: Boolean
        get() = TODO("Not yet implemented")

    override val width: Int = 64
    override val height: Int = 64

    constructor(id: Int, pos: Offset) {
        this.id = id
        this.pos = Offset(pos.x - width / 2f, pos.y - height / 2f)
    }

    private val image: ImageBitmap = imageFromResource("router.png")

    override fun draw(scope: DrawScope, context: DrawPageContext) {
        connectionIds.value.forEach { id ->
            scope.drawLine(Color.Black, center, (context.elementsState.value.find { it.id == id }!! as ConnectableElement).center,5f)
        }
        scope.drawImage(image, dstOffset = IntOffset(pos.x.toInt(), pos.y.toInt()))
        scope.drawIntoCanvas { canvas ->
            (canvas as DesktopCanvas).skija.drawString(id.toString(), center.x, center.y - height * 0.6f, skiaFont, paint.asFrameworkPaint())
        }
    }
}