package ua.leonidius.coldline.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector3
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.TextureComponent
import ua.leonidius.coldline.screens.game.GameScreen

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

            addEntity(this)
        }
    }
}