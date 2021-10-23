package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

data class EnemyComponent(
    var isDumb: Boolean = true,
    var isTriggered: Boolean = false,
): Component