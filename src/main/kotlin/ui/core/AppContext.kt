package ui.core

import androidx.compose.desktop.Window
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import core.Graph
import ui.elements.CommunicationNodeElement
import ui.elements.ChannelElement
import ui.elements.WorkstationElement
import ui.elements.base.ConnectableElement
import ui.elements.base.DrawableElement
import ui.elements.base.Element
import ui.elements.base.ElementType
import ui.pages.draw.windows.DumpGraphWindow
import ui.pages.draw.windows.LoadGraphWindow
import ui.pages.draw.windows.SendMessageWindow

const val CHANNEL_DELAY = 5L // in milliseconds
const val SIMULATION_DELAY = 100L

val CHANNEL_WEIGHTS: List<Int> = listOf(1, 3, 5, 7, 8, 11, 12, 15, 18, 21, 25, 27, 32)

class AppContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<MutableList<Element>> = mutableStateOf(mutableListOf())
    val channelWeightState = mutableStateOf("Random")
    val graph: Graph = Graph()
    private var idGenerator: Int = 0
    private var channelIdGenerator: Int = 0

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
            // todo: start simulation with ticks (which somehow will not block ui so that we can pause or fast-forward it)
            val startTime = System.nanoTime()
            println("Sending message $message from $src to $dest")
            // todo: if tcp - handshake (+ 2 service packages)
            message.splitIntoPackages(src, dest).forEach {
                servicePackages += nodes.node(src).sendPackage(it)
                infoPackages++
            }
            val endTime = System.nanoTime()
            val time = (endTime - startTime)
            // todo: show new window with metrics
            println("Result:\nInfo packages sent: $infoPackages, service packages sent: $servicePackages, time: $time ns")
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
        val channel = ChannelElement(
                channelIdGenerator++,
                start as DrawableElement,
                end as DrawableElement,
                weight = if (channelWeightState.value == "Random") CHANNEL_WEIGHTS.random() else channelWeightState.value.toInt())
        start.channels.value = start.channels.value.apply { add(channel) }.toMutableSet()
        end.channels.value = end.channels.value.apply { add(channel) }.toMutableSet()
        elementsState.value = elementsState.value.toMutableList().apply { // todo: do i need channel in element list?
            add(channel)
            graph.addLink(start.id, end.id, channel.weight)
        }
        updateRouteTables()
        connectingElementsState.value = false
        selectedElementState.value = null
    }

    fun updateRouteTables() {
        val connectableElems: List<ConnectableElement> = elementsState.value.filterIsInstance<ConnectableElement>()
        connectableElems
                .filter { it.channels.value.isNotEmpty() } // todo: calculate only for connected and active nodes
                .map { x ->
                    graph.calculateShortestPathFromSource(x.id)
                    connectableElems.forEach { y ->
                        val path = graph.nodes.find { node -> node.id == y.id }!!.realPath.map { it.id }
                        if (path.isNotEmpty()) {
                            var currentNode = path[0]
                            path.subList(1, path.size).forEach { id ->
                                val elem: ConnectableElement = connectableElems.node(id)
                                connectableElems.node(currentNode).routingTable.addRoute(y.id, elem)
                                currentNode = id
                            }
                        }
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


    fun dumpGraph() {
        DumpGraphWindow(elementsState.value)
    }

    fun loadGraph() {
        LoadGraphWindow(this)
    }

    fun clear() {
        elementsState.value = mutableListOf()
        selectedTypeState.value = null
        channelWeightState.value = "Random"
        graph.clear()
        idGenerator = 0
        channelIdGenerator = 0
        connectingElementsState.value = false
        selectedElementState.value = null
        infoElementState.value = null
        showInfoState.value = false
    }
}