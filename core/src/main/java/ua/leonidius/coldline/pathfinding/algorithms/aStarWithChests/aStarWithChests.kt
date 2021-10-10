package ua.leonidius.coldline.pathfinding.algorithms.aStarWithChests

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.aStar

/**
 * Runs Nearest Neighbor TSP with a dummy node to build a path
 * through all chests from spawn to door, then uses A* to find
 * the shortest paths between the chests in that path
 */
fun aStarWithChests(chestGraph: Graph,
                    chestStartNode: GraphNode,
                    chestEndNode: GraphNode,
                    graph: Graph,
                    heuristic: (GraphNode, GraphNode) -> Float): List<GraphNode> {
    val path = mutableListOf<GraphNode>()

    val chestPath = nearestNeighborTsp(chestGraph, chestStartNode, chestEndNode)

    var prevChestNode = chestPath.first()
    chestPath.forEachIndexed { index, chestNode ->
        if (index != 0) {
            val startNode = graph.findNode {
                it.mapX == prevChestNode.mapX && it.mapY == prevChestNode.mapY }!!

            val endNode = graph.findNode {
                it.mapX == chestNode.mapX && it.mapY == chestNode.mapY }!!

            path.addAll(aStar(graph, startNode, endNode, heuristic))

            prevChestNode = chestNode
        }
    }

    return path.toList()
}