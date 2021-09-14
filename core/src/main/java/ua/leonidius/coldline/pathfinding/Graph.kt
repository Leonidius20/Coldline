package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.utils.Array

class Graph(objectLayer: MapLayer): IndexedGraph<GraphNodeObject> {

    private val nodes = objectLayer.objects.filter {
        it.properties.containsKey("graphNodeId")
    }.map { GraphNodeObject(it as RectangleMapObject) }

    private val connections = objectLayer.objects.filter {
        it.properties.containsKey("tag") && it.properties["tag"] == "graphConnection"
    }.map { GraphConnection(this, it as PolylineMapObject) }

    private val heuristic = DistanceHeuristic()

    fun getNodeById(id: Int) = nodes.find { it.getIndex() == id }

    override fun getConnections(fromNode: GraphNodeObject): Array<Connection<GraphNodeObject>> =
        Array(connections.filter {
            it.polylineObj.properties["startNode"] == fromNode.getIndex()
        }.toTypedArray())


    override fun getIndex(node: GraphNodeObject) = node.getIndex()

    override fun getNodeCount() = nodes.size

    fun findPath(startNode: GraphNodeObject, goalNode: GraphNodeObject): GraphPath<GraphNodeObject> {
        val path = DefaultGraphPath<GraphNodeObject>()
        IndexedAStarPathFinder(this).searchNodePath(startNode, goalNode, heuristic, path)
        return path
    }

}