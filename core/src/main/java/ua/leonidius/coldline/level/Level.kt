package ua.leonidius.coldline.level

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode

class Level {

    lateinit var map: TiledMap

    lateinit var doorCoordinates: GameCoordinates

    lateinit var tileGraph: Graph
    lateinit var spawnTileGraphNode: GraphNode
    lateinit var doorTileGraphNode: GraphNode

    lateinit var chestGraph: Graph
    lateinit var spawnChestGraphNode: GraphNode
    lateinit var doorChestGraphNode: GraphNode

    val objectLayer: MapLayer
        get() = map.layers["objects"]

    val collisionLayer
        get() = map.layers.get("collision") as TiledMapTileLayer

    fun dispose() {
        map.dispose()
    }

    fun isWallBetween(mapX1: Float, mapY1: Float, mapX2: Float, mapY2: Float) {
        // TODO: go down the line in small steps (8px), calculate what tile there is and check
        // if it is a wall
    }

    fun isTileWithCollisionAt(tileX: Int, tileY: Int) =
        collisionLayer.run {
            val cell = getCell(tileX, tileY)
            cell?.tile?.properties?.containsKey("blocked") ?: false
        }

    class Builder {

        val level = Level()

        fun setMap(map: TiledMap) {
            level.map = map
        }

        fun setDoorCoordinates(doorCoordinates: GameCoordinates) {
            level.doorCoordinates = doorCoordinates
        }

        fun setTileGraph(graph: Graph, spawnNode: GraphNode, doorNode: GraphNode) {
            level.tileGraph = graph
            level.spawnTileGraphNode = spawnNode
            level.doorTileGraphNode = doorNode
        }

        fun setChestGraph(graph: Graph, spawnNode: GraphNode, doorNode: GraphNode) {
            level.chestGraph = graph
            level.spawnChestGraphNode = spawnNode
            level.doorChestGraphNode = doorNode
        }

        fun get() = level

    }

}