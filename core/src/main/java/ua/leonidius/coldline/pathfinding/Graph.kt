package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Vector2
import org.w3c.dom.css.Rect

// TODO: decouple physical objects line polylines from graph logic
class Graph(collisionLayer: TiledMapTileLayer, objectLayer: MapLayer, floorTile: TiledMapTile) {

    private lateinit var adjacencyLists: MutableMap<GraphNode, MutableList<GraphNode>>
    val nodes = emptyList<GraphNode>().toMutableList()
    private val connections = emptyList<GraphConnection>().toMutableList()

    lateinit var startNode: GraphNode
    lateinit var endNode: GraphNode

    init {
        val door = objectLayer.objects.get("door") as TiledMapTileMapObject
        val spawn = objectLayer.objects.get("spawnPoint") as RectangleMapObject

        with(collisionLayer) {
            // adding nodes
            var counter = 0
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (getCell(x, y).tile == floorTile) {

                        val node = GraphNode(counter++, x, y)
                        nodes.add(node)

                        if (door.x.toInt() / 16 == x && door.y.toInt() / 16 == y) {
                            endNode = node
                        }

                        // TODO: remove magic 16 nunber
                        if (spawn.rectangle.x.toInt() / 16 == x && spawn.rectangle.y.toInt() / 16 == y) {
                            startNode = node
                        }
                    }
                }
            }

            // adding connections
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val node = nodes.find { it.tileX == x && it.tileY == y }
                    if (node != null) {
                        val downLeftNode = nodes.find { it.tileX == x - 1 && it.tileY == y - 1 }
                        if (downLeftNode != null)
                            connections.add(GraphConnection(node, downLeftNode))

                        val downNode = nodes.find { it.tileX == x && it.tileY == y - 1 }
                        if (downNode != null)
                            connections.add(GraphConnection(node, downNode))

                        val downRightNode = nodes.find { it.tileX == x + 1 && it.tileY == y - 1 }
                        if (downRightNode != null)
                            connections.add(GraphConnection(node, downRightNode))

                        val rightNode = nodes.find { it.tileX == x + 1 && it.tileY == y }
                        if (rightNode != null)
                            connections.add(GraphConnection(node, rightNode))

                    }
                }
            }
        }


        generateAdjacencyLists()
    }

    fun generateAdjacencyLists() {
        adjacencyLists = nodes.associateWith { emptyList<GraphNode>().toMutableList() }.toMutableMap().apply {
            connections.forEach {
                this[it.fromNode]!!.add(it.toNode)
                this[it.toNode]!!.add(it.fromNode)
            }
        }
    }

    fun getNodeById(id: Int) = nodes.find { it.index == id }

    fun getConnectionBetween(startNode: GraphNode, endNode: GraphNode) =
        connections.find {
            (it.fromNode == startNode && it.toNode == endNode)
                    || (it.fromNode == endNode && it.toNode == startNode)
        }

    /*override fun getConnections(fromNode: GraphNode): Array<Connection<GraphNode>> =
        Array(connections.filter {
            it.polylineObj.properties["startNode"] == fromNode.getIndex()
        }.toTypedArray())*/

    /*fun findNearestNodeTo(mapX: Float, mapY: Float) = nodes.minByOrNull {
        val score = Vector2.dst(it.getX(), it.getY(), mapX, mapY)
        // add punishment for going through walls
        score
    }*/

    fun findNodeAt(x: Int, y: Int): GraphNode? {
        return nodes.find { it.tileX == x && it.tileY == y }
    }

    fun getAdjacentNodes(node: GraphNode) = /*connections
        .filter { it.fromNode == node || it.toNode == node }
        .map {
            if (it.fromNode == node) it.toNode else it.fromNode
        }*/ adjacencyLists[node]!!

}