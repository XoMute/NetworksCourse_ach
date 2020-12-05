package ui.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.geometry.Offset
import routing.*
import kotlinx.coroutines.*
import ui.elements.CommunicationNodeElement
import ui.elements.ChannelElement
import ui.elements.WorkstationElement
import ui.elements.base.*
import ui.appInterface.windows.*
import javax.swing.SwingUtilities
import kotlin.random.Random

const val CHANNEL_DELAY = 10 // in nanoseconds
const val SIMULATION_DELAY = 300L // in milliseconds
const val SIMULATION_STEPS = 50

val CHANNEL_WEIGHTS: List<Int> = listOf(1, 3, 5, 7, 8, 11, 12, 15, 18, 21, 25, 27, 32)

class AppContext {
    val selectedTypeState: MutableState<ElementType?> = mutableStateOf(null)
    val elementsState: MutableState<MutableList<Element>> = mutableStateOf(mutableListOf())
    val channelWeightState = mutableStateOf("Random")
    val graph: Graph = Graph()
    var workstationIdGenerator: Int = 0
    var channelIdGenerator: Int = 0
    var satelliteChannelState: MutableState<Boolean> = mutableStateOf(false)

    var connectingElementsState: MutableState<Boolean> = mutableStateOf(false) // selected element for connection
    var selectedElementState: MutableState<Element?> = mutableStateOf(null)
    var infoElementState: MutableState<Element?> = mutableStateOf(null)
    var mousePosState: MutableState<Offset> = mutableStateOf(Offset.Zero)

    var showInfoState: MutableState<Boolean> = mutableStateOf(false)

    //    var packagesState: MutableState<Map<Int, DrawablePackage?>> = mutableStateOf(mutableMapOf()) // state for drawing
    var packagesState: SnapshotStateMap<Int, DrawablePackage?> = mutableStateMapOf() // state for drawing
    var visualSimulationState: MutableState<Boolean> = mutableStateOf(false)
    val sendMessageButtonState: MutableState<Boolean> = mutableStateOf(true)

    var playSimulationState: MutableState<Boolean> = mutableStateOf(false)
    var stepCountState: MutableState<Int> = mutableStateOf(0)

    var virtualState: MutableState<Boolean> = mutableStateOf(false)

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
                        if (el.id == selectedElementState.value!!.id) {
                            dropConnection()
                        } else {
                            createConnection(selectedElementState.value!! as ConnectableElement, el as ConnectableElement)
                        }
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
            } ?: if (el is ChannelElement || selectedTypeState.value != ElementType.CHANNEL) showInfo(el)
            selectedElementState.value = el
        } ?: selectedTypeState.value?.let {
            elementsState.value = elementsState.value.toMutableList().apply {
                when (it) {
                    ElementType.WORKSTATION -> {
                        val workstation = WorkstationElement(workstationIdGenerator++, pos)
                        add(workstation)
                        graph.addNode(workstation)
                    }
                    ElementType.COMMUNICATION_NODE -> {
                        val commNode = CommunicationNodeElement(workstationIdGenerator++, pos)
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
        var infoPackages = 0
        var servicePackages = 0
        var time = 0L
        SendMessageWindow(nodes, this) { src, dest, message ->
            virtualState.value = message.protocol == ProtocolType.VIRTUAL
            // todo: get visualization id and pass it to package

            GlobalScope.launch {
                val startTime = System.nanoTime()
                println("Sending message $message from $src to $dest")
                try {
                    if (message.protocol.directConnection()) {
                        // handshake
                        nodes.node(src).sendPackage(Package(src, dest, PackageType.SERVICE, message.protocol, message.servicePackageSize), this@AppContext)
                        nodes.node(dest).sendPackage(Package(dest, src, PackageType.SERVICE, message.protocol, message.servicePackageSize), this@AppContext)
                        servicePackages += 2
                    }
                    if (message.protocol.directConnection()) {
                        message.splitIntoPackages(src, dest).forEach {
                            var sent = false
                            while (!sent) {
                                try {
                                    val retValue = nodes.node(src).sendPackage(it, this@AppContext)
                                    sent = true
                                    servicePackages += retValue
                                    infoPackages++
                                } catch (e: ChannelErrorException) {
                                    infoPackages++
                                    servicePackages++
                                    continue
                                }
                            }
                        }
                    } else {
                        val coroutines: List<Job> = message.splitIntoPackages(src, dest).map {
                            if (visualSimulationState.value) {
                                delay(200)
                            } else {
                                delay(5)
                            }
                            launch {
                                val key = generatePackageMapId()
                                it.visualizationId = key
                                var sent = false
                                while (!sent) {
                                    try {
                                        val retValue = nodes.node(src).sendPackage(it, this@AppContext)
                                        sent = true
                                        servicePackages += retValue
                                        infoPackages++
                                    } catch (e: ChannelErrorException) {
                                        infoPackages++
                                        servicePackages++
                                        continue
                                    }
                                }
                            }
                        }
                        coroutines.joinAll()
                    }
                    if (message.protocol.directConnection()) {
                        // end connection
                        nodes.node(src).sendPackage(Package(src, dest, PackageType.SERVICE, message.protocol, message.servicePackageSize), this@AppContext)
                        nodes.node(dest).sendPackage(Package(dest, src, PackageType.SERVICE, message.protocol, message.servicePackageSize), this@AppContext)
                        servicePackages += 2
                    }
                } catch (e: NoConnectionException) {
                    SwingUtilities.invokeLater {
                        SendMessageErrorWindow("Can't send message from $src to $dest: ${e.message}")
                    }
                    stopVisualSimulation()
                    return@launch
                }

                val endTime = System.nanoTime()
                time = (endTime - startTime)
                stopVisualSimulation()
                SwingUtilities.invokeLater {
                    SendMessageResultWindow(message, infoPackages, servicePackages, time / 1_000_000)
                }
                this@AppContext.sendMessageButtonState.value = true
            }
        }
    }

    fun showRoutingTable() {
        RoutingTableWindow(this)
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
                weight = if (channelWeightState.value == "Random") CHANNEL_WEIGHTS.random() else channelWeightState.value.toInt(),
                satellite = satelliteChannelState.value)
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
        if (virtualState.value) {
            return
        }
        val connectableElems: List<ConnectableElement> = elementsState.value.filterIsInstance<ConnectableElement>()
        connectableElems.forEach { it.routingTable.clear() }
        connectableElems
                .filter { it.channels.value.isNotEmpty() } // todo: calculate only for connected and active nodes
                .map { x ->
                    if (!x.enabled) { // todo: try to move that check to filter
                        x.routingTable.clear()
                        return@map
                    }
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
        removeHighlight()
    }

    fun disableNode(id: Int) {
        elementsState.value.filterIsInstance<ConnectableElement>().find { it.id == id }!!.enabled = false
        graph.disableNode(id)
        updateRouteTables()
    }

    fun enableNode(id: Int) {
        elementsState.value.filterIsInstance<ConnectableElement>().find { it.id == id }!!.enabled = true
        graph.enableNode(id)
        updateRouteTables()
    }

    fun deleteNode(id: Int) {
        val node = elementsState.value.filterIsInstance<ConnectableElement>().find { it.id == id }!!
        elementsState.value.filterIsInstance<ChannelElement>().forEach { ch ->
            if (ch.el1.id == id || ch.el2.id == id) {
                elementsState.value.removeIf { it == ch }
            }
        }
        elementsState.value.remove(node)
        graph.deleteNode(id)
        stopShowingInfo()
        updateRouteTables()
    }

    fun disableChannel(id: Int) {
        val channel = elementsState.value.filterIsInstance<ChannelElement>().find { it.id == id }!!
        channel.enabled = false
        graph.deleteLink(channel.el1.id, channel.el2.id)
        updateRouteTables()
    }

    fun enableChannel(id: Int) {
        val channel = elementsState.value.filterIsInstance<ChannelElement>().find { it.id == id }!!
        channel.enabled = true
        graph.addLink(channel.el1.id, channel.el2.id, channel.weight)
        updateRouteTables()
    }

    fun deleteChannel(channel: ChannelElement) {
        elementsState.value.remove(channel)
        graph.deleteLink(channel.el1.id, channel.el2.id)
        stopShowingInfo()
        updateRouteTables()
    }

    private fun List<ConnectableElement>.node(id: Int): ConnectableElement {
        return filterIsInstance<ConnectableElement>().find { it.id == id }!!
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

    fun removeHighlight() {
        elementsState.value.filterIsInstance<ChannelElement>().forEach { it.highlightedState.value = false }
    }

    fun dumpGraph() {
        DumpGraphWindow(elementsState.value)
    }

    fun loadGraph() {
        LoadGraphWindow(this)
    }

    fun startVisualSimulation() {
        visualSimulationState.value = true
        playSimulationState.value = false
        stepCountState.value = 0
    }

    fun stopVisualSimulation() {
//        sendMessageButtonState.value = true
        visualSimulationState.value = false
        playSimulationState.value = false
        virtualState.value = false
        stepCountState.value = 0
        packagesState.values.clear()
    }

    // returns free id from packagesState.value to be used in visual simulation
    fun generatePackageMapId(): Int {
        val keys = packagesState.keys
        var key = Random.nextInt()
        while (keys.contains(key)) {
            key = Random.nextInt()
        }
        return key
    }

    fun clear() {
        elementsState.value = mutableListOf()
        selectedTypeState.value = null
        channelWeightState.value = "Random"
        graph.clear()
        workstationIdGenerator = 0
        channelIdGenerator = 0
        connectingElementsState.value = false
        selectedElementState.value = null
        infoElementState.value = null
        showInfoState.value = false
        packagesState = mutableStateMapOf()
        satelliteChannelState.value = false
        visualSimulationState.value = false
        playSimulationState.value = false
        stepCountState.value = 0
        virtualState.value = false
        sendMessageButtonState.value = true
    }
}