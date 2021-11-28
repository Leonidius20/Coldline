package ua.leonidius.coldline.pathfinding.algorithms

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import java.util.*

open class AStarStrategy(private val heuristic: Heuristic)
    : PathfindingStrategy {

    override fun findPath(graph: Graph,
                          startNode: GraphNode,
                          endNode: GraphNode): List<GraphNode> {
        val numberOfNodes = graph.nodes.size

        val traceback = Array<GraphNode?>(numberOfNodes) { null } // filled with -1

        val visited = BooleanArray(numberOfNodes) { false }

        val distFromStart = DoubleArray(numberOfNodes) { Double.POSITIVE_INFINITY }
        distFromStart[startNode.index] = 0.0

        val q = PriorityQueue<GraphNode>(Comparator.comparingDouble {
            distFromStart[it.index] + 10 * heuristic.estimate(it, endNode)
        })

        q.add(startNode)
        visited[startNode.index] = true

        while (q.isNotEmpty()) {
            val node = q.poll()
            // visited[node] = false

            if (node == endNode) break
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
        var currentNode = endNode
        while (currentNode != startNode) {
            path.add(0, currentNode)
            currentNode = traceback[currentNode.index]!!
        }

        return path
    }

    interface Heuristic {

        fun estimate(node1: GraphNode, node2: GraphNode): Float

    }

}