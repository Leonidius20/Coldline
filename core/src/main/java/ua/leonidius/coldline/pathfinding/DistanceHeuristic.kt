package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.math.Vector2

class DistanceHeuristic: Heuristic<GraphNode> {

    override fun estimate(currentNode: GraphNode, goalNode: GraphNode) =
        Vector2.dst(currentNode.getX(), currentNode.getY(),
            goalNode.getX(), goalNode.getY())

}