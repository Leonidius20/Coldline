package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.Entity
import ua.leonidius.coldline.entity.components.*

fun GameScreen.createDoor(doorTileX: Int, doorTileY: Int): Entity {
    with(engine) {
        createEntity().run {
            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.DOOR
            })

            add(createComponent(CollisionComponent::class.java))

            add(createComponent(DoorComponent::class.java))

            add(createComponent(PositionComponent::class.java).apply {
                tileX = doorTileX
                tileY = doorTileY
            })

            addEntity(this)

            return this
        }
    }
}