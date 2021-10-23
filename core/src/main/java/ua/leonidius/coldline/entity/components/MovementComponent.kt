package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class MovementComponent(
    var velocity: Vector2 = Vector2(0F, 0F),
    var speed: Float = 60 * 2F
): Component