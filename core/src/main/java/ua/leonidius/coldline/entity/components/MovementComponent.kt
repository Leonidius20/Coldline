package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class MovementComponent(
    val velocity: Vector2 = Vector2(0F, 0F),
    var speed: Int = 60 * 2
): Component