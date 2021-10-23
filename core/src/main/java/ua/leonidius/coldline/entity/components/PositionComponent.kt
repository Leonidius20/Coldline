package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

data class PositionComponent(
    var mapX: Float = 0F,
    var mapY: Float = 0F,
): Component {
    var tileX: Int
        get() = mapX.toInt() / 16
        set(value) { mapX = value * 16F }

    var tileY: Int
        get() = mapY.toInt() / 16
        set(value) { mapY = value * 16F }
}