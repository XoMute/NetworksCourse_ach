package ui.elements

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import ui.core.DrawPageContext
import ui.elements.base.Element
import ui.elements.base.ElementType

class LineElement(override val id: Int, override var pos: Offset) : Element {
    override val type: ElementType
        get() = TODO("Not yet implemented")
    override val connectable: Boolean
        get() = TODO("Not yet implemented")

    override fun collides(offset: Offset): Boolean = false

    override fun draw(scope: DrawScope, context: DrawPageContext) {
        TODO("Not yet implemented")
    }

}