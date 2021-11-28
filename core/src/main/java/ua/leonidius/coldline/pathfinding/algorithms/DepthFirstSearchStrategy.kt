package ua.leonidius.coldline.pathfinding.algorithms

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode

class DepthFirstSearchStrategy: PathfindingStrategy {

    override fun findPath(
        graph: Graph, startNode: GraphNode, endNode: GraphNode
    ): List<GraphNode>? {

        val discoveredNode = graph.nodes.associateWith { false }.toMutableMap()

        val stack = ArrayDeque<GraphNode>()
        stack.addLast(startNode)

        while (!stack.isEmpty()) {
            val node = stack.last()

            var noUndiscoveredConnections = true

            for (adjacentNode in graph.getAdjacentNodes(node)) {
                if (!discoveredNode[adjacentNode]!!) {
                    stack.addLast(adjacentNode)

                    if (adjacentNode == endNode) {
                        return stack
                    }

                    discoveredNode[adjacentNode] = true
                    noUndiscoveredConnections = false
                    break // because it's depth first
                }
            }

            if (noUndiscoveredConnections) {
                stack.removeLast()
            }
        }

        return null
    }

}