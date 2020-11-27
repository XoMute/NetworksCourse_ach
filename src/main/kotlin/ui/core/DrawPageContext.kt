package ui.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import ui.elements.CommunicationNodeElement
import ui.elements.ConnectionElement
import ui.elements.WorkstationElement
import ui.elements.base.Element
import ui.elements.base.ElementType

class DrawPageContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<List<Element>> = mutableStateOf(emptyList())
    var idGenerator: Int = 0

    fun onMouseMove(pos: Offset): Boolean {
//        println("Moved mouse to ${pos.x}, ${pos.y}")
        // todo: add logic for connection establishment
        // todo: also draw selected type under mouse
        return true
    }

    fun click(pos: Offset) {
        elementsState.value.find { it.collides(pos) }?.let {
            // todo: show info about element in separate window
            println("Clicked on element ${it.id}")
        } ?: selectedTypeState.value?.let {
            elementsState.value = elementsState.value.toMutableList().apply {
                add(when (it) {
                    ElementType.CONNECTION -> ConnectionElement(idGenerator++, pos)
                    ElementType.WORKSTATION -> WorkstationElement(idGenerator++, pos)
                    ElementType.COMMUNICATION_NODE -> CommunicationNodeElement(idGenerator++, pos)
                })
            }
        }
    }
}