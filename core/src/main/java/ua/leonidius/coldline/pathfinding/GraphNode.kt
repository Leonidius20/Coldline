package ua.leonidius.coldline.pathfinding

import com.badlogic.gdx.maps.objects.RectangleMapObject

class GraphNode(val rectMapObj: RectangleMapObject) {

    fun getIndex() = rectMapObj.properties["graphNodeId"] as Int

    fun getX() = rectMapObj.rectangle.x

    fun getY() = rectMapObj.rectangle.y

}