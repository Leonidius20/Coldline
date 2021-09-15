package ua.leonidius.coldline.screens.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.TextureComponent
import ua.leonidius.coldline.entity.components.TypeComponent

fun GameScreen.createEnemy(tileX: Int, tileY: Int) {
    with(engine) {
        createEntity().run {
            add(createComponent(PositionComponent::class.java).apply {
                position = Vector3(
                    tileToMapCoordinate(tileX),
                    tileToMapCoordinate(tileY),
                    1F
                )
            })

            add(createComponent(TextureComponent::class.java).apply {
                textureRegion = TextureRegion(Texture("enemy.png"))
            })

            add(createComponent(TypeComponent::class.java).apply {
                type = EntityType.ENEMY
            })

            addEntity(this)
        }
    }
}