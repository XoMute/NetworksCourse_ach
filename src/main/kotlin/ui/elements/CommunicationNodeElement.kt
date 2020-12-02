package ui.elements

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import ui.core.RoutingTable
import ui.elements.base.ConnectableElement
import ui.elements.base.DrawableImageElement
import ui.elements.base.Element
import ui.elements.base.ElementType

class CommunicationNodeElement : ConnectableElement, DrawableImageElement {

    override val id: Int
    override var pos: Offset
    override val type: ElementType = ElementType.COMMUNICATION_NODE
    override val connectionIds: MutableState<MutableSet<Int>> = mutableStateOf(mutableSetOf())
    override val routingTable: RoutingTable

    override val width: Int = 64
    override val height: Int = 64
    override val image: ImageBitmap = imageFromResource("router.png")

    constructor(id: Int, pos: Offset) {
        this.id = id
        this.pos = Offset(pos.x - width / 2f, pos.y - height / 2f)
        this.routingTable = RoutingTable()
    }

    override fun toString(): String {
        return "Communication node(Id: $id)"
    }


    override fun equals(other: Any?): Boolean {
        return this === other || id == (other as? Element)?.id && type == (other).type
    }
}