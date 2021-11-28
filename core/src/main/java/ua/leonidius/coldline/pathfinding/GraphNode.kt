package ua.leonidius.coldline.pathfinding

import ua.leonidius.coldline.level.GameCoordinates

class GraphNode: GameCoordinates {

    val index: Int

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(index: Int, mapX: Float, mapY: Float): super() {
        this.index = index
        this.mapX = mapX
        this.mapY = mapY
    }

}