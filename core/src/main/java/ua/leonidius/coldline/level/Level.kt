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
        fun generate(tileSet: TiledMapTileSet): Level {
            val width = 50
            val height = 50

            val tileWidth = 16
            val tileHeight = 16

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
            val horizontalWallTileBottom = tileSet.getTile(41)
            val horizontalWallTileTop = tileSet.getTile(2)
            val verticalWallTileRight = tileSet.getTile(14)
            val verticalWallTileLeft = tileSet.getTile(17)
            val floorTile = tileSet.getTile(95)

            // TODO: remove hardcoded tile width and height values
            val collisionLayer = TiledMapTileLayer(width, height, tileWidth, tileHeight).apply {
                name = "collision"
            }

            val objectLayer = MapLayer().apply { name = "objects" }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    // TODO: adding left and right walls, possibly top and bottom too
                    val cell = grid.get(x / 2, y / 2)

                    collisionLayer.setCell(x, y,
                        TiledMapTileLayer.Cell().setTile(
                             if (cell == 1F) blankTile
                            else floorTile
                        ))
                }
            }

            // adding walls
            with(collisionLayer) {
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val cell = getCell(x, y).tile

                        if (cell != blankTile) continue // we replace black tiles with walls

                        val cellStillBlank = true // to determine whether to put graph node there

                        if ((x + 1) < width // cell on the right exists
                            && getCell(x  + 1, y).tile == floorTile) { // cell on the right is room floor
                                // put wall here
                                setCell(x, y, TiledMapTileLayer.Cell().setTile(verticalWallTileRight))
                        }

                        if ((x - 1) >= 0  // cell on the left exists
                            && getCell(x - 1, y).tile == floorTile) { // cell on the left is room floor
                                setCell(x, y, TiledMapTileLayer.Cell().setTile(verticalWallTileLeft))
                        }

                        if (y - 1 >= 0 // cell on top exists
                            && getCell(x, y - 1).tile == floorTile) { // cell on top is floor
                            setCell(x, y, TiledMapTileLayer.Cell().setTile(horizontalWallTileTop))
                        }

                        if (y + 1 < height // cell on the bottom exists
                            && getCell(x, y + 1).tile == floorTile) { // cell on the bottom is floor
                            setCell(x, y, TiledMapTileLayer.Cell().setTile(horizontalWallTileBottom))
                        }

                        var counter = 0
                        if (cellStillBlank) {
                            // put graph node
                            val graphNode = RectangleMapObject().apply {
                                with(properties) {
                                    put("graphNodeId", counter++)
                                }
                                rectangle.x = (x * tileWidth).toFloat()
                                rectangle.y = (y * tileHeight).toFloat()
                            }
                            objectLayer.objects.add(graphNode)

                            // adding down-left, down and down-right connections


                            // adding right connections
                        }


                    }
                }
            }




            map.layers.add(collisionLayer)

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