package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.PolylineMapObject

class GraphConnection(graph: Graph, val polylineObj: PolylineMapObject):
    Connection<GraphNode> {

    private val startNode = graph.getNodeById(polylineObj.properties["startNode"] as Int)
    private val endNode = graph.getNodeById(polylineObj.properties["endNode"] as Int)

    override fun getCost() = polylineObj.polyline.length

    override fun getFromNode() = startNode

    override fun getToNode() = endNode

    fun render(shapeRenderer: ShapeRenderer, color: Color) {
        shapeRenderer.color = color
        shapeRenderer.polyline(polylineObj.polyline.transformedVertices)
    }

}