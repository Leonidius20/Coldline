package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.maps.objects.PolylineMapObject

class GraphConnection(
    private val startNode: GraphNode,
    private val endNode: GraphNode,
    private val weight: Float)

    : PolylineMapObject(
    floatArrayOf(startNode.tileX * 16F + 16 / 2, startNode.tileY * 16F + 16 / 2,
        endNode.tileX * 16F + 16 / 2, endNode.tileY * 16F + 16 / 2)
),
    Connection<GraphNode> {

    override fun getCost() = polyline.length

    override fun getFromNode() = startNode

    override fun getToNode() = endNode

    fun getWeight() = weight

}