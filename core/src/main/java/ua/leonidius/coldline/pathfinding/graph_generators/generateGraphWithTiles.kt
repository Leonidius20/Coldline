package ua.leonidius.coldline.pathfinding.graph_generators

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode

/**
 * Used to generate a graph with all floor tiles as nodes
 * @return Triple of graph, start node (spawn) and end node (door)
 */
fun generateGraphWithTiles(collisionLayer: TiledMapTileLayer, objectLayer: MapLayer, floorTile: TiledMapTile): Triple<Graph, GraphNode, GraphNode> {
    val graph = Graph()

    val door = objectLayer.objects.get("door") as TiledMapTileMapObject
    val spawn = objectLayer.objects.get("spawnPoint") as RectangleMapObject

    lateinit var startNode: GraphNode
    lateinit var endNode: GraphNode

    with(collisionLayer) {
        // adding nodes
        var counter = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (getCell(x, y).tile == floorTile) {
                    // TODO: remove magic 16 number
                    val mapX = x * 16F
                    val mapY = y * 16F

                    val node = GraphNode(counter++, mapX, mapY)
                    graph.addNode(node)

                    if (door.x == mapX && door.y == mapY) {
                        endNode = node
                    }

                    if (spawn.rectangle.x == mapX && spawn.rectangle.y == mapY) {
                        startNode = node
                    }
                }
            }
        }

        // adding connections
        for (x in 0 until width) {
            for (y in 0 until height) {
                val node = graph.findNode { it.tileX == x && it.tileY == y }
                if (node != null) {
                    val downLeftNode = graph.findNode { it.tileX == x - 1 && it.tileY == y - 1 }
                    if (downLeftNode != null)
                        graph.addConnection(node, downLeftNode)

                    val downNode = graph.findNode { it.tileX == x && it.tileY == y - 1 }
                    if (downNode != null)
                        graph.addConnection(node, downNode)

                    val downRightNode = graph.findNode { it.tileX == x + 1 && it.tileY == y - 1 }
                    if (downRightNode != null)
                        graph.addConnection(node, downRightNode)

                    val rightNode = graph.findNode { it.tileX == x + 1 && it.tileY == y }
                    if (rightNode != null)
                        graph.addConnection(node, rightNode)

                }
            }
        }
    }


    graph.generateAdjacencyLists()

    return Triple(graph, startNode, endNode)
}