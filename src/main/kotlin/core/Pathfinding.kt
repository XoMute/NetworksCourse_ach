package core

import ui.elements.base.ConnectableElement
import java.util.*

data class Node(val id: Int) {
    var shortestPath: List<Node> = mutableListOf()
    val realPath: List<Node>
        get() {
            return if (shortestPath.isEmpty()) {
                emptyList()
            } else {
                LinkedList(shortestPath).also { it.add(this) }
            }
        }
    var distance: Int = Int.MAX_VALUE // initial distances
    val adjacentNodes: MutableMap<Node, Int> = mutableMapOf()

    fun addLink(destination: Node, distance: Int) {
        adjacentNodes[destination] = distance
        destination.adjacentNodes[this] = distance
    }
}

class Graph {
    val nodes: MutableSet<Node> = mutableSetOf()

    fun addNode(node: ConnectableElement) {
        nodes.add(node.toGraphNode())
    }

    fun addLink(id1: Int, id2: Int, weight: Int) {
        val node1 = nodes.find { it.id == id1 }!!
        val node2 = nodes.find { it.id == id2 }!!
        node1.addLink(node2, weight)
    }

    fun calculateShortestPathFromSource(nodeId: Int) {
        setupNodes()
        val sourceNode = nodes.find { it.id == nodeId }!!

        sourceNode.distance = 0
        val settledNodes: MutableSet<Node> = mutableSetOf()
        val unsettledNodes: MutableSet<Node> = mutableSetOf()
        unsettledNodes.add(sourceNode)

        while (unsettledNodes.isNotEmpty()) {
            val currentNode = unsettledNodes.minBy { it.distance }!!
            unsettledNodes.remove(currentNode)
            currentNode.adjacentNodes.entries.forEach {
                val adjacentNode = it.key
                val edgeWeight = it.value
                if (!settledNodes.contains(currentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode)
                    unsettledNodes.add(adjacentNode)
                }
            }
            settledNodes.add(currentNode)
        }
    }

    fun clear() {
        nodes.clear()
    }

    private fun calculateMinimumDistance(evaluationNode: Node, edgeWeight: Int, sourceNode: Node) {
        val calcValue = sourceNode.distance + edgeWeight
        if (calcValue < evaluationNode.distance) {
            evaluationNode.distance = calcValue
            val shortestPath: LinkedList<Node> = LinkedList(sourceNode.shortestPath)
            shortestPath.add(sourceNode)
            evaluationNode.shortestPath = shortestPath
        }
    }

    private fun setupNodes() {
        nodes.forEach { it.distance = Int.MAX_VALUE; it.shortestPath = emptyList(); }
    }
}

