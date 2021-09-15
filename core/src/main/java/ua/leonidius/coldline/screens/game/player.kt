package ua.leonidius.coldline.screens.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import ua.leonidius.coldline.entity.components.*

fun GameScreen.createPlayer(tileX: Int, tileY: Int) {
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
                    x = tileToMapCoordinate(tileX)
                    y = tileToMapCoordinate(tileY)
                }
            })

            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.PLAYER
            })

            add(createComponent(CollisionComponent::class.java))

            add(createComponent(MovementComponent::class.java))

            add(createComponent(PlayerComponent::class.java))

            addEntity(this)
        }
    }
}