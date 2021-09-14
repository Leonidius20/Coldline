package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.math.Vector2

class DistanceHeuristic: Heuristic<GraphNodeObject> {

    override fun estimate(currentNode: GraphNodeObject, goalNode: GraphNodeObject) =
        Vector2.dst(currentNode.getX(), currentNode.getY(),
            goalNode.getX(), goalNode.getY())

}