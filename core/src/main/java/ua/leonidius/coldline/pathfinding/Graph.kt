package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2

// TODO: decouple physical objects line polylines from graph logic
class Graph() {

    private lateinit var adjacencyLists: MutableMap<GraphNode, MutableList<GraphNode>>
    val nodes = emptyList<GraphNode>().toMutableList()
    private val connections = emptyList<GraphConnection>().toMutableList()

    constructor(objectLayer: MapLayer) : this() {
        nodes.addAll(objectLayer.objects.filter {
            it.properties.containsKey("graphNodeId")
        }.map { GraphNode(it as RectangleMapObject) })

        connections.addAll(objectLayer.objects.filter {
            it.properties.containsKey("tag") && it.properties["tag"] == "graphConnection"
        }.map { GraphConnection(this, it as PolylineMapObject) })

        generateAdjacencyLists()
    }

    fun generateAdjacencyLists() {
        adjacencyLists = nodes.associateWith { emptyList<GraphNode>().toMutableList() }.toMutableMap().apply {
            connections.forEach {
                this[it.fromNode]!!.add(it.toNode!!)
                this[it.toNode]!!.add(it.fromNode!!)
            }
        }
    }

    fun addNode(rectMapObject: RectangleMapObject) {
        nodes.add(GraphNode(rectMapObject))
    }

    fun addConnection(polyline: PolylineMapObject) {
        connections.add(GraphConnection(this, polyline))
    }

    fun getNodeById(id: Int) = nodes.find { it.getIndex() == id }

    fun getConnectionBetween(startNode: GraphNode, endNode: GraphNode) =
        connections.find {
            (it.fromNode == startNode && it.toNode == endNode)
                    || (it.fromNode == endNode && it.toNode == startNode)
        }

    /*override fun getConnections(fromNode: GraphNode): Array<Connection<GraphNode>> =
        Array(connections.filter {
            it.polylineObj.properties["startNode"] == fromNode.getIndex()
        }.toTypedArray())*/

    fun findNearestNodeTo(mapX: Float, mapY: Float) = nodes.minByOrNull {
        val score = Vector2.dst(it.getX(), it.getY(), mapX, mapY)
        // add punishment for going through walls
        score
    }

    fun getAdjacentNodes(node: GraphNode) = /*connections
        .filter { it.fromNode == node || it.toNode == node }
        .map {
            if (it.fromNode == node) it.toNode else it.fromNode
        }*/ adjacencyLists[node]!!

}