package ua.leonidius.coldline.pathfinding

import ua.leonidius.coldline.level.GameCoordinates

class GraphNode: GameCoordinates {

    val index: Int

    @Suppress("ConvertSecondaryConstructorToPrimary")
    @Deprecated("Move to tile XY")
    constructor(index: Int, mapX: Float, mapY: Float): super() {
        this.index = index
        this.tileX = (mapX / 16).toInt()
        this.tileY = (mapY / 16).toInt()
    }

    constructor(index: Int, tileX: Int, tileY: Int): super() {
        this.index = index
        this.tileX = tileX
        this.tileY = tileY
    }

}