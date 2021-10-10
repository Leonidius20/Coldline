package ua.leonidius.coldline.pathfinding.algorithms.aStarWithChests

import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphConnection
import ua.leonidius.coldline.pathfinding.GraphNode

fun nearestNeighborTsp(graph: Graph, startNode: GraphNode, endNode: GraphNode): List<GraphNode> {
    val fictitiousNode = GraphNode(graph.nodes.size, 0F, 0F)
    val endToFict = GraphConnection(endNode, fictitiousNode, 0F)
    val fictToStart = GraphConnection(fictitiousNode, startNode,0F)

    with(graph) {
        addNode(fictitiousNode)
        with(connections) {
            add(endToFict)
            add(fictToStart)
        }
        generateAdjacencyLists()
    }

    val result = runAlgo(graph, startNode)
    result.remove(fictitiousNode)
    val endResult = result.distinct()

    with(graph) {
        nodes.remove(fictitiousNode)
        with(connections) {
            remove(endToFict)
            remove(fictToStart)
        }
        generateAdjacencyLists()
    }

    return endResult
}

private fun runAlgo(graph: Graph, startNode: GraphNode): MutableList<GraphNode> {
    val path = mutableListOf<GraphNode>()

    val visited = BooleanArray(graph.nodes.size) { false }
    var currentVertex = startNode
    visited[currentVertex.index] = true
    path.add(currentVertex)

    while (!visited.all { it }) {
        val nextNode = graph.getAdjacentNodes(currentVertex)
            .filter { !visited[it.index] }
            .minByOrNull { graph.getEdgeWeight(currentVertex, it) }!!
        path.add(nextNode)
        visited[nextNode.index] = true
        currentVertex = nextNode
    }

    return path
}