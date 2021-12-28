package ua.leonidius.coldline.level.generation

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.MathUtils
import com.github.czyzby.noise4j.map.Grid
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator
import ua.leonidius.coldline.level.GameCoordinates
import ua.leonidius.coldline.level.Level

class LevelGenerator(tileSet: TiledMapTileSet) {

    val width = 50
    val height = 50

    private val tileWidth = 16
    private val tileHeight = 16

    private val blankTile = tileSet.getTile(101)
    private val horizontalWallTileBottom = tileSet.getTile(41)
    private val horizontalWallTileTop = tileSet.getTile(2)
    private val verticalWallTileRight = tileSet.getTile(14)
    private val verticalWallTileLeft = tileSet.getTile(17)
    val floorTile = tileSet.getTile(95)
    private var doorTile = tileSet.getTile(32)
    private var chestTile = tileSet.getTile(65)

    private lateinit var collisionLayer: TiledMapTileLayer
    private lateinit var objectLayer: MapLayer

    var doorTileX = -1
    var doorTileY = -1

    fun generate(): Level {
        val builder = Level.Builder()

        generateMap(builder)

        generateGraphs(builder)

        return builder.get()
    }

    fun load(tmxFile: String): Level {
        val builder = Level.Builder()
        loadMap(tmxFile, builder)

        // generateGraphs(builder)
        return builder.get()
    }

    private fun generateMap(builder: Level.Builder) {
        val grid = Grid(width / 2, height / 2)

        DungeonGenerator().apply {
            roomGenerationAttempts = 10
            maxRoomSize = 15 / 2
            tolerance = 6 / 2
            minRoomSize = 5 / 2 + 1
            randomConnectorChance = 0.4F
        }.generate(grid)

        val map = TiledMap()

        collisionLayer = TiledMapTileLayer(width, height, tileWidth, tileHeight).apply {
            name = "collision"
        }

        objectLayer = MapLayer().apply { name = "objects" }

        // filling map with tiles (floor & empty space)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val cell = grid.get(x / 2, y / 2)

                collisionLayer.setCell(x, y,
                    TiledMapTileLayer.Cell().setTile(
                        if (cell == 1F) blankTile
                        else floorTile
                    ))
            }
        }

        placeWalls()

        placeDoor(builder)

        placeSpawnPoint()

        placeChests()

        map.layers.add(collisionLayer)
        map.layers.add(objectLayer)

        builder.setMap(map)
    }

    private fun placeWalls() {
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
    }

    private fun placeDoor(builder: Level.Builder) {
        for (y in height - 2 downTo 1) {
            for (x in 1 until width - 2) {
                if (is3x3RadiusEmpty(x, y)) {
                    doorTileX = x
                    doorTileY = y
                    objectLayer.objects.add(TiledMapTileMapObject(doorTile, false, false).apply {
                        name = "door"
                        this.x = (x * tileWidth).toFloat()
                        this.y = (y * tileHeight).toFloat()
                    })
                    builder.setDoorCoordinates(GameCoordinates.fromTile(doorTileX, doorTileY))
                    return
                }
            }
        }
    }

    private fun placeSpawnPoint() {
        for (y in 1 until height - 2) {
            for (x in width - 2 downTo 1) {
                if (is3x3RadiusEmpty(x, y)) {
                    objectLayer.objects.add(RectangleMapObject().apply {
                        name = "spawnPoint"
                        this.rectangle.x = (x * tileWidth).toFloat()
                        this.rectangle.y = (y * tileHeight).toFloat()
                    })
                    return
                }
            }
        }
    }

    /**
     * Checking if a 3x3 rectangle with the center at supplied coordinates
     * only contains floor tiles, i.e. is fully inside a room. Does not
     * check if the coordinate range is correct
     * @throws IndexOutOfBoundsException if the 3x3 rectangle goes beyond the map
     */
    private fun is3x3RadiusEmpty(x: Int, y: Int): Boolean {
        return collisionLayer.getCell(x, y).tile == floorTile
            && collisionLayer.getCell(x, y + 1).tile == floorTile
            && collisionLayer.getCell(x, y - 1).tile == floorTile
            && collisionLayer.getCell(x + 1, y).tile == floorTile
            && collisionLayer.getCell(x - 1, y).tile == floorTile
            && collisionLayer.getCell(x - 1, y - 1).tile == floorTile
            && collisionLayer.getCell(x - 1, y + 1).tile == floorTile
            && collisionLayer.getCell(x + 1, y - 1).tile == floorTile
            && collisionLayer.getCell(x + 1, y + 1).tile == floorTile

    }

    private fun placeChests() {
        repeat(11) {
            val x = MathUtils.random(1, width - 1)
            val y = MathUtils.random(1, height - 1)
            if (collisionLayer.getCell(x, y).tile == floorTile) {
                objectLayer.objects.add(TiledMapTileMapObject(chestTile, false, false).apply {
                    this.x = (x * tileWidth).toFloat()
                    this.y = (y * tileHeight).toFloat()
                    this.properties.put("tag", "chest")
                })
            }
        }
    }

    private fun generateGraphs(builder: Level.Builder) {
        val tileGraphTrio = generateGraphWithTiles(collisionLayer, objectLayer, floorTile)
        builder.setTileGraph(tileGraphTrio.first, tileGraphTrio.second, tileGraphTrio.third)

        val chestGraphTrio = generateGraphWithChests(objectLayer)
        builder.setChestGraph(chestGraphTrio.first, chestGraphTrio.second, chestGraphTrio.third)
    }

    private fun loadMap(tmxFile: String, builder: Level.Builder) {
        val map = TmxMapLoader().load(tmxFile)
        objectLayer = map.layers["objects"]



        collisionLayer = map.layers["collision"] as TiledMapTileLayer
        val door = objectLayer.objects.find { it.name == "door" } as TiledMapTileMapObject

        builder.setMap(map)
        builder.setDoorCoordinates(GameCoordinates.fromMap(door.x, door.y))
    }

}