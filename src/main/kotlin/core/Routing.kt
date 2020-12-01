package core

import ui.elements.base.ConnectableElement

/**
 * Class which contains hash table with shortest paths to each node
 */
class RoutingTable(val elId: Int) {

    val table: MutableMap<Int, List<Int>> = mutableMapOf(Pair(elId, emptyList()))

    override fun toString(): String {
        return table.toString()
    }
}

data class RoutingPath(val path: List<Int>) {

}

fun constructPath(
        from: ConnectableElement,
        to: ConnectableElement,
): RoutingPath {
    val path = from.routingTable.table[to.id]!!
    return RoutingPath(path)
}