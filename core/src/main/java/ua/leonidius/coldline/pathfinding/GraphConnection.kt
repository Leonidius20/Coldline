package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.math.Polyline

class GraphConnection(val startNode: GraphNode, val endNode: GraphNode) : PolylineMapObject(
    floatArrayOf(startNode.tileX * 16F + 16 / 2, startNode.tileY * 16F + 16 / 2,
        endNode.tileX * 16F + 16 / 2, endNode.tileY * 16F + 16 / 2)
),
    Connection<GraphNode> {

    override fun getCost() = polyline.length

    override fun getFromNode() = startNode

    override fun getToNode() = endNode

}