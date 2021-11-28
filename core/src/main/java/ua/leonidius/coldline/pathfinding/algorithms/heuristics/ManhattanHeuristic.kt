package ua.leonidius.coldline.pathfinding.algorithms.heuristics

import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.AStarStrategy
import kotlin.math.abs

class ManhattanHeuristic: AStarStrategy.Heuristic {

    override fun estimate(node1: GraphNode, node2: GraphNode): Float {
        val dx = abs(node1.tileX - node2.tileX)
        val dy = abs(node1.tileY - node2.tileY)
        return (dx + dy).toFloat()
    }

}