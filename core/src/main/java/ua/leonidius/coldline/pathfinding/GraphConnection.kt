package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.math.Polyline

class GraphConnection(val startNode: GraphNode, val endNode: GraphNode) : PolylineMapObject(
    floatArrayOf(startNode.tileX * 16F, startNode.tileY * 16F,
        endNode.tileX * 16F, endNode.tileY * 16F)
),
    Connection<GraphNode> {

    override fun getCost() = polyline.length

    override fun getFromNode() = startNode

    override fun getToNode() = endNode

    fun render(shapeRenderer: ShapeRenderer, color: Color) {
        shapeRenderer.color = color
        shapeRenderer.polyline(polyline.transformedVertices)
    }

}