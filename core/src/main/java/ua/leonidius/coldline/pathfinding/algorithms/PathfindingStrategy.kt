package ua.leonidius.coldline.pathfinding.algorithms

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode

interface PathfindingStrategy {

    fun findPath(graph: Graph, startNode: GraphNode, endNode: GraphNode): List<GraphNode>?

}