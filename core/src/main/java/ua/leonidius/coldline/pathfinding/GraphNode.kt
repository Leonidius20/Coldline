package ua.leonidius.coldline.pathfinding

data class GraphNode(val index: Int, val mapX: Float, val mapY: Float) {
    val tileX: Int
        get() = mapX.toInt() / 16

    val tileY: Int
        get() = mapY.toInt() / 16
}