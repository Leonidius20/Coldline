package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

data class MovementComponent(
    var velocity: Pair<Int, Int> = Pair(0, 0),
    var speed: Int = 1
): Component