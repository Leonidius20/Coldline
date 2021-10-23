package ua.leonidius.coldline.pathfinding.algorithms

import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import java.util.*
import kotlin.math.abs

fun aStar(graph: Graph,
          start: GraphNode,
          end: GraphNode,
          heuristic: (GraphNode, GraphNode) -> Float): List<GraphNode> {
    val numberOfNodes = graph.nodes.size

    val traceback = Array<GraphNode?>(numberOfNodes) { null } // filled with -1

    val visited = BooleanArray(numberOfNodes) { false }

    val distFromStart = DoubleArray(numberOfNodes) { Double.POSITIVE_INFINITY }
    distFromStart[start.index] = 0.0

    val q = PriorityQueue<GraphNode>(Comparator.comparingDouble {
        distFromStart[it.index] + 10 * heuristic(it, end)
    })

    q.add(start)
    visited[start.index] = true

    while (q.isNotEmpty()) {
        val node = q.poll()
        // visited[node] = false

        if (node == end) break
        for (nodeV in graph.getAdjacentNodes(node)) {
            val tentDistFromStart = distFromStart[node.index] + graph.getEdgeWeight(node, nodeV)

            if (tentDistFromStart < distFromStart[nodeV.index]) {
                distFromStart[nodeV.index] = tentDistFromStart
                traceback[nodeV.index] = node
                if (!visited[nodeV.index]) {
                    visited[nodeV.index] = true
                    q.add(nodeV)
                }
            }
        }
    }

    val path = ArrayList<GraphNode>()
    var currentNode = end
    while (currentNode != start) {
        path.add(0, currentNode)
        currentNode = traceback[currentNode.index]!!
    }

    return path
}

fun euclidianHeuristic(node1: GraphNode, node2: GraphNode): Float {
    return Vector2.dst(node1.tileX.toFloat(), node1.tileY.toFloat(), node2.tileX.toFloat(), node2.tileY.toFloat())
}

fun manhattanHeuristic(node1: GraphNode, node2: GraphNode): Float {
    val dx = abs(node1.tileX - node2.tileX)
    val dy = abs(node1.tileY - node2.tileY)
    return (dx + dy).toFloat()
}

/**
 * A* becomes Dijkstra's
 */
fun greedyHeuristic(node1: GraphNode, node2: GraphNode): Float = 0F