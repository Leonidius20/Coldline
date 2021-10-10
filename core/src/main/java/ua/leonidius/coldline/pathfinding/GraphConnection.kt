package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.math.Vector2

data class GraphConnection(
    val fromNode: GraphNode,
    val toNode: GraphNode,
    val weight: Float = Vector2.dst(
        fromNode.mapX, fromNode.mapY,
        toNode.mapX, toNode.mapY
    )
)