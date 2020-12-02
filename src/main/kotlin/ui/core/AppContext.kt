package ui.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import core.Graph
import core.PackageType
import ui.elements.CommunicationNodeElement
import ui.elements.ChannelElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import ui.elements.base.DrawableElement
import ui.elements.base.Element
import ui.elements.base.ElementType
import ui.pages.draw.SendMessageWindow

class AppContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<MutableList<Element>> = mutableStateOf(mutableListOf())
    val graph: Graph = Graph()
    private var idGenerator: Int = 0
    private var lineIdGenerator: Int = 0

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
            else -> clickedElements.find { it !is ChannelElement }
        }
        clickedElement?.let { el ->
            if (selectedTypeState.value == ElementType.CHANNEL) {
                if (el !is ChannelElement) {
                    if (connectingElementsState.value) {
                        createConnection(selectedElementState.value!! as ConnectableElement, el as ConnectableElement)
                    } else {
                        startConnection(el as ConnectableElement)
                    }
                } else return
            }
            infoElementState.value?.let {
                if (infoElementState.value != null
                        && infoElementState.value!!.id == el.id
                        && el.type == selectedTypeState.value) {
                    stopShowingInfo()
                } else {
                    null
                }
            } ?: if (el is ChannelElement || selectedTypeState.value != ElementType.CHANNEL) showInfo(el)
            selectedElementState.value = el
        } ?: selectedTypeState.value?.let {
            elementsState.value = elementsState.value.toMutableList().apply {
                when (it) {
                    ElementType.WORKSTATION -> {
                        val workstation = WorkstationElement(idGenerator++, pos)
                        add(workstation)
                        graph.addNode(workstation)
                    }
                    ElementType.COMMUNICATION_NODE -> {
                        val commNode = CommunicationNodeElement(idGenerator++, pos)
                        add(commNode)
                        graph.addNode(commNode)

                    }
                    ElementType.CHANNEL -> {
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

    fun sendMessage() {
        val nodes = elementsState.value.filterIsInstance<ConnectableElement>()
        val lines = elementsState.value.filterIsInstance<ChannelElement>()
        var infoPackages = 0
        var servicePackages = 0
        SendMessageWindow(nodes, lines) { src, dest, message ->
            val startTime = System.nanoTime()
            println("Sending message $message from $src to $dest")
            // todo: if tcp - handshake (+ 2 service packages)
            message.splitIntoPackages(src, dest).forEach {
                servicePackages += nodes.node(src).sendPackage(it, message.protocol)
                infoPackages++
            }
            val endTime = System.nanoTime()
            // todo: show new window with metrics
            println("Result:\nInfo packages sent: $infoPackages, service packages sent: $servicePackages, time: ${endTime - startTime}ns")
        }
    }

    fun showRoutingTable() {
        println((infoElementState.value as ConnectableElement).showRoutingTable())
    }

    private fun startConnection(elem: ConnectableElement) {
        selectedElementState.value = elem
        connectingElementsState.value = true
    }

    private fun createConnection(start: ConnectableElement, end: ConnectableElement) {
        start.connectionIds.value = start.connectionIds.value.apply { add(end.id) }.toMutableSet()
        end.connectionIds.value = end.connectionIds.value.apply { add(start.id) }.toMutableSet()
        elementsState.value = elementsState.value.toMutableList().apply {
            val channelElement = ChannelElement(lineIdGenerator++, start as DrawableElement, end as DrawableElement)
            add(channelElement)
            graph.addLink(start.id, end.id, channelElement.weight)
        }
        updateRouteTables()
        connectingElementsState.value = false
        selectedElementState.value = null
    }

    private fun updateRouteTables() {
        val connectableElems: List<ConnectableElement> = elementsState.value.filterIsInstance<ConnectableElement>()
                connectableElems
                .filter { it.connectionIds.value.isNotEmpty() } // todo: calculate only for connected and active nodes
                .map { x ->
                    graph.calculateShortestPathFromSource(x.id)
                    connectableElems.forEach { y ->
                        val path = graph.nodes.find { node -> node.id == y.id }!!.realPath.map { it.id }
                        println("Path for ${y.id} is $path")
                        if (path.isNotEmpty()) {
                            var currentNode = path[0]
                            path.subList(1, path.size).forEach { id ->
                                val elem: ConnectableElement = connectableElems.node(id)
                                connectableElems.node(currentNode).routingTable.addRoute(y.id, elem)
                                currentNode = id
                            }
                        }
//                        x.routingTable.table[y.id] = path
//                                .map { node -> node.id }
                    }
                }
    }

    private fun List<ConnectableElement>.node(id: Int): ConnectableElement {
        return find { it.id == id }!!
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