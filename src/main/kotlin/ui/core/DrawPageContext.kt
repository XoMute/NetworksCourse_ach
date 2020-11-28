package ui.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import ui.elements.CommunicationNodeElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import ui.elements.base.Element
import ui.elements.base.ElementType

class DrawPageContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<MutableList<Element>> = mutableStateOf(mutableListOf())
    var idGenerator: Int = 0

    var connectingElementsState: MutableState<Boolean> = mutableStateOf(false)
    var selectedElementState: MutableState<Element?> = mutableStateOf(null)
    var mousePosState: MutableState<Offset> = mutableStateOf(Offset.Zero)

    fun onMouseMove(pos: Offset): Boolean {
        mousePosState.value = pos/*.copy()*/
        if (selectedTypeState.value == ElementType.LINE && connectingElementsState.value) {
        } else {
            // todo: draw selected type under mouse
        }
        return true
    }

    fun click(pos: Offset) {
        elementsState.value.find { it.collides(pos) }?.let {
            println("Clicked on element ${it.id}")
            when (selectedTypeState.value) {
                ElementType.LINE -> {
                    if (connectingElementsState.value) {
                        createConnection(selectedElementState.value!! as ConnectableElement, it as ConnectableElement)
                    } else {
                        startConnection(it as ConnectableElement)
                    }
                }
                else -> {
                    selectedElementState.value = it
                    //todo: select element and show info about it
                }
            }
        } ?: selectedTypeState.value?.let {
            elementsState.value = elementsState.value.toMutableList().apply {
                when (it) {
                    ElementType.WORKSTATION -> add(WorkstationElement(idGenerator++, pos))
                    ElementType.COMMUNICATION_NODE -> add(CommunicationNodeElement(idGenerator++, pos))
                    ElementType.LINE -> {}
                }
            }
        }
    }

    private fun startConnection(elem: ConnectableElement) {
        selectedElementState.value = elem
        connectingElementsState.value = true
    }

    private fun createConnection(start: ConnectableElement, end: ConnectableElement) {
        start.connectionIds.value = start.connectionIds.value.apply { add(end.id) }.toMutableSet()
        end.connectionIds.value = end.connectionIds.value.apply { add(start.id) }.toMutableSet()
        println("Connected ${start.id} and ${end.id}")
        connectingElementsState.value = false
        selectedElementState.value = null
    }
}