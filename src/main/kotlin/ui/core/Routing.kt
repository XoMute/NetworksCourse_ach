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

    fun clear(id: Int, context: AppContext) {
//        table.keys.forEach { key ->
//            (context.elementsState.value.find { it.id == key }!! as ConnectableElement).routingTable.table.remove(id)
//        }
        table.clear()
    }
}