package core

/**
 * Class which contains hash table with shortest paths to each node
 */
class RoutingTable(val elId: Int) {

    val table: MutableMap<Int, List<Int>> = mutableMapOf(Pair(elId, emptyList()))

    override fun toString(): String {
        return table.toString()
    }
}