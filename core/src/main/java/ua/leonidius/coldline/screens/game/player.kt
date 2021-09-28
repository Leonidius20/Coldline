package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ua.leonidius.coldline.entity.components.*

fun GameScreen.createPlayer(mapX: Float, mapY: Float): Entity {
    with(engine) {
        createEntity().run {
            /*add(createComponent(PositionComponent::class.java).apply {
                position = Vector3(
                    tileToMapCoordinate(tileX),
                    tileToMapCoordinate(tileY),
                    2F
                )
            })*/

            add(createComponent(SpriteComponent::class.java).apply {
                sprite = Sprite(Texture("player.png")).apply {
                    x = mapX
                    y = mapY
                }
            })

            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.PLAYER
            })

            add(createComponent(CollisionComponent::class.java))

            add(createComponent(MovementComponent::class.java))

            add(createComponent(PlayerComponent::class.java))

            addEntity(this)

            return this
        }
    }
}