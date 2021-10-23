package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

data class HealthComponent(
    var health: Int = 100
): Component