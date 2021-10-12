package ua.leonidius.coldline.level

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Vector2

class Level(val map: TiledMap, val exitTileCoords: Vector2) {

    val objectLayer = map.layers["objects"]
    val collisionLayer = map.layers.get("collision") as TiledMapTileLayer

    fun dispose() {
        map.dispose()
    }

    fun isWallBetween(mapX1: Float, mapY1: Float, mapX2: Float, mapY2: Float) {
        // TODO: go down the line in small steps (8px), calculate what tile there is and check
        // if it is a wall
    }

    companion object {

        fun load(tmxFile: String): Level {
            val map = TmxMapLoader().load(tmxFile)
            val objectLayer = map.layers["objects"]
            val door = objectLayer.objects.find { it.name == "door" } as TiledMapTileMapObject
            val doorCoords = Vector2(door.x, door.y)
            return Level(map, doorCoords)
        }

    }

}