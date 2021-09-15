package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3

data class PositionComponent(
    var position: Vector3 = Vector3()
) : Component