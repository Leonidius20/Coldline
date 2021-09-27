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

    companion object {
        fun generate(): Level {
            return load("maps/level2.tmx")
        }

        fun load(tmxFile: String): Level {
            val map = TmxMapLoader().load(tmxFile)
            val objectLayer = map.layers["objects"]
            val door = objectLayer.objects.find { it.name == "door" } as TiledMapTileMapObject
            val doorCoords = Vector2(door.x, door.y)
            return Level(map, doorCoords)
        }

    }

}