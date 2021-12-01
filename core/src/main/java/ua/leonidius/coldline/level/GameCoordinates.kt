package ua.leonidius.coldline.level

import com.badlogic.gdx.math.Vector2
import kotlin.math.absoluteValue

open class GameCoordinates {

    @Deprecated("Switching to tile XY", ReplaceWith("tileX * 16F"))
    val mapX: Float
        get() = tileX * 16F

    @Deprecated("Switching to tile XY", ReplaceWith("tileY * 16F"))
    val mapY: Float
        get() = tileY * 16F

    var tileX: Int = -1

    var tileY: Int = -1

    /*var mapXY: Pair<Float, Float>
        get() = Pair(mapX, mapY)
        set(value) {
            mapX = value.first
            mapY = value.second
        }*/

    var tileXY: Pair<Int, Int>
        get() = Pair(tileX, tileY)
        set(value) {
            tileX = value.first
            tileY = value.second
        }

    // fun toMapVector() = Vector2(mapX, mapY)

    fun toTileVector() = Vector2(tileX.toFloat(), tileY.toFloat())

    fun addVector(vector: Pair<Int, Int>) {
        tileX += vector.first
        tileY += vector.second
    }

    fun clone(): GameCoordinates {
        return fromTile(tileX, tileY)
    }

    /**
     * Returns the direction to move from the "other" coordinates to these
     */
    fun vectorSub(other: GameCoordinates): Pair<Int, Int> {
        val xDiff = tileX - other.tileX
        val yDiff = tileY - other.tileY

        return Pair(if (xDiff == 0) 0 else xDiff / xDiff.absoluteValue,
            if (yDiff == 0) 0 else yDiff / yDiff.absoluteValue)
    }

    companion object {

        @Deprecated("Switching to tile XY only")
        fun fromMap(mapX: Float, mapY: Float): GameCoordinates {
            val coordinates = GameCoordinates()
            coordinates.tileX = (mapX / 16).toInt()
            coordinates.tileY = (mapY / 16).toInt()
            return coordinates
        }

        fun fromTile(tileX: Int, tileY: Int): GameCoordinates {
            val coordinates = GameCoordinates()
            coordinates.tileX = tileX
            coordinates.tileY = tileY
            return coordinates
        }

    }

}