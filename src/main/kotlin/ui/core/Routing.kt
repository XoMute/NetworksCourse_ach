package ui.core

import ui.elements.base.ConnectableElement

/**
 * Class which contains hash table with shortest paths to each node
 */
class RoutingTable {

    val table: MutableMap<Int, ConnectableElement> = mutableMapOf()

    fun addRoute(dst: Int, nextNode: ConnectableElement) {
        table[dst] = nextNode
    }

    override fun toString(): String {
        return table.toString()
    }
}