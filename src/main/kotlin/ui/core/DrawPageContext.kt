package ui.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import ui.elements.CommunicationNodeElement
import ui.elements.ConnectionElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import ui.elements.base.Element
import ui.elements.base.ElementType

class DrawPageContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<List<Element>> = mutableStateOf(emptyList())
    var idGenerator: Int = 0
    var connectingElements: Boolean = false
    var selectedElement: MutableState<Element?> = mutableStateOf(null)
    var mousePos: Offset = Offset.Zero

    fun onMouseMove(pos: Offset): Boolean {
//        println("Moved mouse to ${pos.x}, ${pos.y}")
        // todo: add logic for connection establishment
        mousePos = pos
        if (selectedTypeState.value == ElementType.CONNECTION && connectingElements) {
//            connectedElement!!.let {
//
//            }
        } else {
            // todo: draw selected type under mouse
        }
        return true
    }

    fun click(pos: Offset) {
        elementsState.value.find { it.collides(pos) }?.let {
            println("Clicked on element ${it.id}")
            when (selectedTypeState.value) {
                ElementType.CONNECTION -> {
                    if (connectingElements) {
                        createConnection(selectedElement.value!! as ConnectableElement, it as ConnectableElement)
                    } else {
                        startConnection(it as ConnectableElement)
                    }
                }
                else -> {
                    selectedElement.value = it
                    //todo: select element and show info about it
                }
            }
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

    private fun startConnection(elem: ConnectableElement) {
        selectedElement.value = elem
        connectingElements = true
    }

    private fun createConnection(start: ConnectableElement, end: ConnectableElement) {
        // todo: add ids to each's connections list
        start.connectionIds.value = start.connectionIds.value.apply { add(end.id) }.toMutableSet()
        end.connectionIds.value = end.connectionIds.value.apply { add(start.id) }.toMutableSet()
        println("Connected ${start.id} and ${end.id}")
        connectingElements = false
        selectedElement.value = null
    }
}