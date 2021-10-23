package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

data class TypeComponent(
    var type: EntityType = EntityType.OTHER
): Component

enum class EntityType {
    PLAYER, ENEMY_DUMB, ENEMY_SMART, DOOR, OTHER
}