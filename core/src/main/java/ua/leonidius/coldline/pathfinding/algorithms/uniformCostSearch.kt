package ua.leonidius.coldline.pathfinding.algorithms

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import java.util.*

fun uniformCostSearch(
    graph: Graph, startNode: GraphNode, endNode: GraphNode
): List<GraphNode> {

    val costs = graph.nodes.associateWith { Double.POSITIVE_INFINITY }.toMutableMap()
    val paths = graph.nodes.associateWith { emptyList<GraphNode>() }.toMutableMap()

    val queue = PriorityQueue<GraphNode>(Comparator.comparingDouble { costs[it]!! })

    costs[startNode] = 0.0
    paths[startNode] = listOf(startNode)
    queue.add(startNode)

    while (!queue.isEmpty()) {
        val node = queue.poll()
        val cost = costs[node]!!

        for (adjacentNode in graph.getAdjacentNodes(node)) {
            val adjacentNodeCost = costs[adjacentNode]!!
            val costThroughThisNode = cost + graph.getConnectionBetween(node, adjacentNode)!!.weight
            if (adjacentNodeCost > costThroughThisNode) {
                costs[adjacentNode] = costThroughThisNode
                paths[adjacentNode] = mutableListOf<GraphNode>().apply {
                    addAll(paths[node]!!)
                    add(adjacentNode)
                }
                queue.add(adjacentNode)
            }
        }
    }

    return paths[endNode]!!
}