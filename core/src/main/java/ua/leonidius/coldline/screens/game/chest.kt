package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.Entity
import ua.leonidius.coldline.entity.components.*

@Deprecated("move to tile coordinates")
fun GameScreen.createChest(mapX: Float, mapY: Float): Entity {
    with(engine) {
        createEntity().run {
            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.CHEST
            })

            add(createComponent(CollisionComponent::class.java))

            add(createComponent(ChestComponent::class.java))

            add(createComponent(PositionComponent::class.java).apply {
                this.tileX = (mapX / 16).toInt()
                this.tileY = (mapY / 16).toInt()
            })

            addEntity(this)

            return this
        }
    }
}