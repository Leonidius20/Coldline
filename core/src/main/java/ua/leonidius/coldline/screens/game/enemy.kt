package ua.leonidius.coldline.screens.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.SpriteComponent
import ua.leonidius.coldline.entity.components.TypeComponent

fun GameScreen.createEnemy(tileX: Int, tileY: Int, isDumb: Boolean) {
    with(engine) {
        createEntity().run {
            /*add(createComponent(PositionComponent::class.java).apply {
                position = Vector3(
                    tileToMapCoordinate(tileX),
                    tileToMapCoordinate(tileY),
                    1F
                )
            })*/

            add(createComponent(SpriteComponent::class.java).apply {
                sprite = Sprite(Texture("enemy.png")).apply {
                    x = tileToMapCoordinate(tileX)
                    y = tileToMapCoordinate(tileY)
                }
                // TODO: dispose of the asset, maybe through asset manager
            })

            add(createComponent(TypeComponent::class.java).apply {
                type = if (isDumb) EntityType.ENEMY_DUMB else EntityType.ENEMY_SMART
            })

            addEntity(this)
        }
    }
}