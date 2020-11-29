package ui.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import ui.elements.CommunicationNodeElement
import ui.elements.LineElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import ui.elements.base.DrawableElement
import ui.elements.base.Element
import ui.elements.base.ElementType

class DrawPageContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<MutableList<Element>> = mutableStateOf(mutableListOf())
    var idGenerator: Int = 0

    var connectingElementsState: MutableState<Boolean> = mutableStateOf(false) // selected element for connection
    var selectedElementState: MutableState<Element?> = mutableStateOf(null)
    var infoElementState: MutableState<Element?> = mutableStateOf(null)
    var mousePosState: MutableState<Offset> = mutableStateOf(Offset.Zero)

    var showInfoState: MutableState<Boolean> = mutableStateOf(false)

    fun onMouseMove(pos: Offset): Boolean {
        mousePosState.value = pos
        if (!connectingElementsState.value) {
            // todo: draw selected type under mouse
        }
        return true
    }

    fun click(pos: Offset) {
        val clickedElements = elementsState.value.filter { (it as DrawableElement).collides(pos) }
        val clickedElement = when {
            clickedElements.isEmpty() -> null
            clickedElements.size == 1 -> clickedElements[0]
            else -> clickedElements.find { it !is LineElement }
        }
        clickedElement?.let { el ->
            println("Clicked on element ${el.id}")
            if (selectedTypeState.value == ElementType.LINE) {
                if (el !is LineElement) {
                    if (connectingElementsState.value) {
                        createConnection(selectedElementState.value!! as ConnectableElement, el as ConnectableElement)
                    } else {
                        startConnection(el as ConnectableElement)
                    }
                }
            }
            infoElementState.value?.let {
                if (infoElementState.value != null
                        && infoElementState.value!!.id == el.id
                        && el.type == selectedTypeState.value) {
                    stopShowingInfo()
                } else {
                    null
                }
            } ?: if (el is LineElement || selectedTypeState.value != ElementType.LINE) showInfo(el)
            selectedElementState.value = el
        } ?: selectedTypeState.value?.let {
            elementsState.value = elementsState.value.toMutableList().apply {
                when (it) {
                    ElementType.WORKSTATION -> add(WorkstationElement(idGenerator++, pos))
                    ElementType.COMMUNICATION_NODE -> add(CommunicationNodeElement(idGenerator++, pos))
                    ElementType.LINE -> {
                        if (connectingElementsState.value) {
                            dropConnection()
                        } else {
                            stopShowingInfo()
                        }
                    }
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
        elementsState.value = elementsState.value.toMutableList().apply {
            add(LineElement(idGenerator++, start as DrawableElement, end as DrawableElement))// todo: remove id from line (??)
        }
        println("Connected ${start.id} and ${end.id}")
        connectingElementsState.value = false
        selectedElementState.value = null
    }

    private fun dropConnection() {
        selectedElementState.value = null
        connectingElementsState.value = false
    }

    private fun showInfo(el: Element) {
        infoElementState.value = el
        showInfoState.value = true
    }

    private fun stopShowingInfo() {
        infoElementState.value = null
        showInfoState.value = false
    }
}