package ua.leonidius.coldline.pathfinding.algorithms

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.utils.Array
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode

fun bfs(
    graph: Graph, startNode: GraphNode, endNode: GraphNode
): List<GraphNode>? {

    val discoveredNode = graph.nodes.associateWith { false }.toMutableMap()

    val queue = ArrayDeque<List<GraphNode>>()
    queue.addLast(arrayListOf(startNode))

    while (!queue.isEmpty()) {
        val path = queue.first()
        val node = path.last()

        for (adjacentNode in graph.getAdjacentNodes(node)) {
            if (!discoveredNode[adjacentNode]!!) {
                val newPath = ArrayList(path).apply { add(adjacentNode) }
                queue.addLast(newPath)

                if (adjacentNode == endNode) return newPath

                discoveredNode[adjacentNode] = true
            }
        }

        queue.removeFirst()
    }

    return null
}