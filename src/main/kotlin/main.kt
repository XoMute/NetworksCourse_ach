import androidx.compose.desktop.Window
import core.Graph
import core.Node
import ui.MainUI

fun main() {
    Window(title = "Course_ach") {
        MainUI()
    }
//    val nodeA = Node(0)
//    val nodeB = Node(1)
//    val nodeC = Node(2)
//    val nodeD = Node(3)
//    val nodeE = Node(4)
//    val nodeF = Node(5)
//
//    nodeA.addLink(nodeB,1)
//    nodeA.addLink(nodeC,1)
//
//    nodeB.addLink(nodeD,1)
//    nodeB.addLink(nodeF,1)
//
//    nodeC.addLink(nodeE,1)
//
//    nodeD.addLink(nodeE,1)
//    nodeD.addLink(nodeF,1)
//
//    nodeF.addLink(nodeE,1)
//
//    val graph = Graph()
//
//    graph.nodes.add(nodeA)
//    graph.nodes.add(nodeB)
//    graph.nodes.add(nodeC)
//    graph.nodes.add(nodeD)
//    graph.nodes.add(nodeE)
//    graph.nodes.add(nodeF)
//
//    graph.calculateShortestPathFromSource(nodeA.id)
//    graph.nodes.forEach{ println("node ${it.id}: ${it.shortestPath} + ${if (it.shortestPath.isNotEmpty()) it else ""} ")}
//
//    graph.calculateShortestPathFromSource(nodeB.id)
//    graph.nodes.forEach{ println("node ${it.id}: ${it.shortestPath} + ${if (it.shortestPath.isNotEmpty()) it else ""} ")}

}
