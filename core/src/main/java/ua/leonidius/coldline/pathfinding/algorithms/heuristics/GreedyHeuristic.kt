package ua.leonidius.coldline.pathfinding.algorithms.heuristics

import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.AStarStrategy

class GreedyHeuristic: AStarStrategy.Heuristic {

    override fun estimate(node1: GraphNode, node2: GraphNode) = 0F

}