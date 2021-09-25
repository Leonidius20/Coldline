package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.Entity
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.TypeComponent

fun GameScreen.createDoor(tileX: Int, tileY: Int): Entity {
    with(engine) {
        createEntity().run {
            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.DOOR
            })

            addEntity(this)

            return this
        }
    }
}