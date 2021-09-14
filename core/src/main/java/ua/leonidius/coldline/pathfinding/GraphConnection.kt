package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.maps.objects.PolylineMapObject

class GraphConnection(graph: Graph, val polylineObj: PolylineMapObject):
    Connection<GraphNodeObject> {

    private val startNode = graph.getNodeById(polylineObj.properties["startNode"] as Int)
    private val endNode = graph.getNodeById(polylineObj.properties["endNode"] as Int)

    override fun getCost() = polylineObj.polyline.length

    override fun getFromNode() = startNode

    override fun getToNode() = endNode

    fun render() {

    }

}