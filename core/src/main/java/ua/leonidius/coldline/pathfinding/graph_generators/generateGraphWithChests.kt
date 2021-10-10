package ua.leonidius.coldline.pathfinding.graph_generators

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode

/**
 * Used to generate a graph with chests, spawn and door as nodes
 * to subsequently use in TSP
 * @return Triple of graph, start node (spawn) and end node (door)
 */
fun generateGraphWithChests(objectLayer: MapLayer): Triple<Graph, GraphNode, GraphNode> {
    val graph = Graph()

    // TODO: remove 16 magic number

    var nodeIndex = 0

    val door = objectLayer.objects.get("door") as TiledMapTileMapObject
    val doorNode = GraphNode(nodeIndex++, door.x, door.y)
    graph.addNode(doorNode)

    val spawn = objectLayer.objects.get("spawnPoint") as RectangleMapObject
    val spawnNode = GraphNode(nodeIndex++, spawn.rectangle.x, spawn.rectangle.y)
    graph.addNode(spawnNode)

    val chests = objectLayer.objects.filter { it.properties["tag"] == "chest" }.map { it as TiledMapTileMapObject }
    chests.forEach {
        val chestNode = GraphNode(nodeIndex++, it.x, it.y)
        graph.addNode(chestNode)
    }

    // create connections between each and every node
    graph.nodes.forEachIndexed { index1, graphNode1 ->
        graph.nodes.forEachIndexed { index2, graphNode2 ->
            if (index2 > index1) {
                graph.addConnection(graphNode1, graphNode2)
            }
        }
    }

    graph.generateAdjacencyLists()

    return Triple(graph, spawnNode, doorNode)
}