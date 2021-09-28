package ua.leonidius.coldline.level

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.*
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.noise4j.map.Grid
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator
import ua.leonidius.coldline.pathfinding.Graph

class Level(val map: TiledMap, val exitTileCoords: Vector2) {

    val objectLayer = map.layers["objects"]
    val collisionLayer = map.layers.get("collision") as TiledMapTileLayer

    fun dispose() {
        map.dispose()
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