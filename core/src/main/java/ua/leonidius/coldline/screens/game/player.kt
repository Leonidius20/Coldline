package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import ua.leonidius.coldline.entity.components.*

@Deprecated("use tile coordinates")
fun GameScreen.createPlayer(playerMapX: Float, playerMapY: Float): Entity {
    with(engine) {
        createEntity().run {
            add(createComponent(PositionComponent::class.java).apply {
                tileX = (playerMapX / 16).toInt()
                tileY = (playerMapY / 16).toInt()
            })

            add(createComponent(TextureComponent::class.java).apply {
                texture = Texture("player.png")
            })

            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.PLAYER
            })

            add(createComponent(CollisionComponent::class.java))

            add(createComponent(MovementComponent::class.java))

            add(createComponent(PlayerComponent::class.java))

            add(createComponent(HealthComponent::class.java))

            add(createComponent(ScoreComponent::class.java))

            addEntity(this)

            return this
        }
    }
}