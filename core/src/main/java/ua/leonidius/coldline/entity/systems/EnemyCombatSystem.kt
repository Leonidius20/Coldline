package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.entity.components.*

class EnemyCombatSystem(private val playerPos: PositionComponent): IntervalIteratingSystem(
    Family.all(EnemyComponent::class.java).get(), 0.25F
) {

    private val enemyMapper = ComponentMapper.getFor(EnemyComponent::class.java)
    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    override fun processEntity(entity: Entity?) {
        val enemyComponent = enemyMapper.get(entity)
        val movementComponent = movementMapper.get(entity)
        if (enemyComponent.isTriggered) {
            val enemyPos = positionMapper.get(entity)

            val playerVector = Vector2(playerPos.mapX, playerPos.mapY)
            val enemyVector = Vector2(enemyPos.mapX, enemyPos.mapY)

            // create bullet

            movementComponent.velocity = playerVector.sub(enemyVector).nor()

            // createBullet(movementComponent.velocity)
        } else {
            movementComponent.velocity.apply { x = 0F; y = 0F }
        }
    }

    private fun createBullet(direction: Vector2, mapX: Float, mapY: Float) {
        with(engine) {
            createEntity().run {
                add(createComponent(BulletComponent::class.java))

                add(createComponent(ShapeComponent::class.java).apply {
                    // shape of line
                })

                add(createComponent(PositionComponent::class.java).apply {
                    // mapX = playerMapX
                    // mapY = playerMapY
                })

                add(createComponent(CollisionComponent::class.java))

                add(createComponent(MovementComponent::class.java))

                add(createComponent(TypeComponent::class.java).apply {
                    type = EntityType.BULLET
                })

                addEntity(this)
            }
        }
    }

}