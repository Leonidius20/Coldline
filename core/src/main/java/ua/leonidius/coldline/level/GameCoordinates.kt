package ua.leonidius.coldline.level

import com.badlogic.gdx.math.Vector2

open class GameCoordinates {

    var mapX = -1F
    var mapY = -1F

    var tileX: Int
        get() = mapX.toInt() / 16
        set(value) { mapX = value * 16F }

    var tileY: Int
        get() = mapY.toInt() / 16
        set(value) { mapY = value * 16F }

    var mapXY: Pair<Float, Float>
        get() = Pair(mapX, mapY)
        set(value) {
            mapX = value.first
            mapY = value.second
        }

    var tileXY: Pair<Int, Int>
        get() = Pair(tileX, tileY)
        set(value) {
            tileX = value.first
            tileY = value.second
        }

    fun toMapVector() = Vector2(mapX, mapY)

    fun toTileVector() = Vector2(tileX.toFloat(), tileY.toFloat())

    companion object {

        fun fromMap(mapX: Float, mapY: Float): GameCoordinates {
            val coordinates = GameCoordinates()
            coordinates.mapX = mapX
            coordinates.mapY = mapY
            return coordinates
        }

        fun fromTile(tileX: Int, tileY: Int): GameCoordinates {
            val coordinates = GameCoordinates()
            coordinates.tileX = tileX
            coordinates.tileY = tileY
            return coordinates
        }

        fun fromMapVector(vector2: Vector2): GameCoordinates {
            val coordinates = GameCoordinates()
            coordinates.mapX = vector2.x
            coordinates.mapY = vector2.y
            return coordinates
        }

    }

}