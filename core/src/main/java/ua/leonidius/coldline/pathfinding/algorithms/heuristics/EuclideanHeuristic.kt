package ua.leonidius.coldline.pathfinding.algorithms.heuristics

import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.AStarStrategy

class EuclideanHeuristic: AStarStrategy.Heuristic {

    override fun estimate(node1: GraphNode, node2: GraphNode): Float {
        return Vector2.dst(node1.tileX.toFloat(), node1.tileY.toFloat(), node2.tileX.toFloat(), node2.tileY.toFloat())
    }

}