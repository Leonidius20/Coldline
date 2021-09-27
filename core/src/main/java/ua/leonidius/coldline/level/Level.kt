package ua.leonidius.coldline.level

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.*
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Vector2
import com.github.czyzby.noise4j.map.Grid
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator

class Level(val map: TiledMap, val exitTileCoords: Vector2) {

    val objectLayer = map.layers["objects"]
    val collisionLayer = map.layers.get("collision") as TiledMapTileLayer

    fun dispose() {
        map.dispose()
    }

    companion object {
        fun generate(tileSet: TiledMapTileSet): Level {
            val width = 50
            val height = 50

            val grid = Grid(width / 2, height / 2)

            DungeonGenerator().apply {
                roomGenerationAttempts = 10
                maxRoomSize = 15 / 2
                tolerance = 6 / 2
                minRoomSize = 5 / 2 + 1
                randomConnectorChance = 0F
            }.generate(grid)


            val map = TiledMap()

            val blankTile = tileSet.getTile(100)
            val horizontalWallTile = tileSet.getTile(1)
            val verticalWallTile = tileSet.getTile(13)
            val floorTile = tileSet.getTile(95)

            // TODO: remove hardcoded tile width and height values
            val collisionLayer = TiledMapTileLayer(width, height, 16, 16).apply {
                name = "collision"
            }

            for (x in 0 until width) {
                for (y in 0 until height) {
                    // TODO: adding left and right walls, possibly top and bottom too
                    val cell = grid.get(x / 2, y / 2)
                    collisionLayer.setCell(x, y,
                        TiledMapTileLayer.Cell().setTile(
                            if (cell == 1F) blankTile else floorTile
                        ))
                }
            }


            map.layers.add(collisionLayer)
            val objectLayer = MapLayer().apply { name = "objects" }
            map.layers.add(objectLayer)


            // TODO: garbage coordinates
            return Level(map, Vector2(45F, 45F))
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