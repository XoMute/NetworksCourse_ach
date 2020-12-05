package ui.elements

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import routing.Package
import serialization.Element
import ui.core.AppContext
import ui.core.RoutingTable
import ui.elements.base.ConnectableElement
import ui.elements.base.DrawableImageElement
import ui.elements.base.ElementType

class WorkstationElement : ConnectableElement, DrawableImageElement {

    override val id: Int
    override var pos: Offset
    override val type: ElementType = ElementType.WORKSTATION
    override val channels: MutableState<MutableSet<ChannelElement>> = mutableStateOf(mutableSetOf())
    override val routingTable: RoutingTable
    override val packages: MutableState<MutableList<Package>> = mutableStateOf(mutableListOf())
    override val acceptedPackages: MutableState<MutableList<Package>> = mutableStateOf(mutableListOf())
    override var enabled: Boolean = true

    constructor(id: Int, pos: Offset) {
        this.id = id
        this.pos = Offset(pos.x - width / 2f, pos.y - height / 2f)
        this.routingTable = RoutingTable()
    }

    override val width: Int = 32
    override val height: Int = 32

    override val image: ImageBitmap = imageFromResource("workstation.png")

    override fun toString(): String {
        return "Workstation(Id: $id)"
    }

    companion object {
        fun deserialize(el: Element, context: AppContext): WorkstationElement {
            return WorkstationElement(el.id, Offset(el.x, el.y))
        }
    }
}