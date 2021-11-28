package ua.leonidius.coldline.pathfinding.algorithms.aStarWithChests

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.AStarStrategy

/**
 * Runs Nearest Neighbor TSP with a dummy node to build a path
 * through all chests from spawn to door, then uses A* to find
 * the shortest paths between the chests in that path
 */

class AStarTSPStrategy(private val chestGraph: Graph,
                       private val chestStartNode: GraphNode,
                       private val chestEndNode: GraphNode,
                       heuristic: Heuristic): AStarStrategy(heuristic) {

    override fun findPath(graph: Graph,
                          igoneThis: GraphNode,
                          igoneThat: GraphNode): List<GraphNode> {
        val path = mutableListOf<GraphNode>()

        val chestPath = nearestNeighborTsp(chestGraph, chestStartNode, chestEndNode).reversed()

        var prevChestNode = chestPath.first()
        chestPath.forEachIndexed { index, chestNode ->
            if (index != 0) {
                val startNode = graph.findNode {
                    it.mapX == prevChestNode.mapX && it.mapY == prevChestNode.mapY }!!

                val endNode = graph.findNode {
                    it.mapX == chestNode.mapX && it.mapY == chestNode.mapY }!!

                path.addAll(super.findPath(graph, startNode, endNode))

                prevChestNode = chestNode
            }
        }

        return path
    }

}