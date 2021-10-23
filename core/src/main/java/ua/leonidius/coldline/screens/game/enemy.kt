package ua.leonidius.coldline.screens.game

import com.badlogic.gdx.graphics.Texture
import ua.leonidius.coldline.entity.components.*

fun GameScreen.createEnemy(enemyTileX: Int, enemyTileY: Int, isEnemyDumb: Boolean) {
    with(engine) {
        createEntity().run {
            add(createComponent(PositionComponent::class.java).apply {
                tileX = enemyTileX
                tileY = enemyTileY
            })

            add(createComponent(TextureComponent::class.java).apply {
                texture = Texture("enemy.png")
                // TODO: dispose of the asset, maybe through asset manager
            })

            add(createComponent(TypeComponent::class.java).apply {
                type = if (isEnemyDumb) EntityType.ENEMY_DUMB else EntityType.ENEMY_SMART
            })

            add(createComponent(EnemyComponent::class.java).apply {
                isDumb = isEnemyDumb
            })

            add(createComponent(MovementComponent::class.java).apply {
                speed /= 1.25F
            })

            add(createComponent(CollisionComponent::class.java))

            addEntity(this)
        }
    }
}