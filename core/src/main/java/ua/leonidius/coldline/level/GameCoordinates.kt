package ua.leonidius.coldline.level

open class GameCoordinates {

    var mapX = -1F
    var mapY = -1F

    var tileX: Int
        get() = mapX.toInt() / 16
        set(value) { mapX = value * 16F }

    var tileY: Int
        get() = mapY.toInt() / 16
        set(value) { mapY = value * 16F }

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

    }

}