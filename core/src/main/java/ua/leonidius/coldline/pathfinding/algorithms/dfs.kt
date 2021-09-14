package ua.leonidius.coldline.pathfinding.algorithms

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.utils.Array
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import kotlin.collections.ArrayDeque

fun dfs(
    graph: Graph, startNode: GraphNode, endNode: GraphNode
): DefaultGraphPath<GraphNode>? {

    val discoveredNode = graph.nodes.associateWith { false }.toMutableMap()

    val stack = ArrayDeque<GraphNode>()
    stack.addLast(startNode)

    while (!stack.isEmpty()) {
        val node = stack.last()

        var noUndiscoveredConnections = true

        for (adjacentNode in graph.getAdjacentNodes(node)) {
            if (!discoveredNode[adjacentNode]!!) {
                stack.addLast(adjacentNode!!)

                if (adjacentNode == endNode) {
                    return DefaultGraphPath<GraphNode>(Array(stack.toTypedArray()))
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