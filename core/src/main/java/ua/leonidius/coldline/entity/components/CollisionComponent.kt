package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

/**
 * Included in entities that can move and collide with
 * walls and other obstacles (e.g. player, enemy)
 */
data class CollisionComponent(
    var collidesOnX: Boolean = false,
    var collidesOnY: Boolean = false,
): Component