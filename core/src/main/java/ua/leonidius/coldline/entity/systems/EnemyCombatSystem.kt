package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.entity.components.EnemyComponent
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.PositionComponent

class EnemyCombatSystem(private val playerPos: PositionComponent): IteratingSystem(
    Family.all(EnemyComponent::class.java).get()
) {

    private val enemyMapper = ComponentMapper.getFor(EnemyComponent::class.java)
    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val enemyComponent = enemyMapper.get(entity)
        val movementComponent = movementMapper.get(entity)
        if (enemyComponent.isTriggered) {
            val enemyPos = positionMapper.get(entity)

            val playerVector = Vector2(playerPos.mapX, playerPos.mapY)
            val enemyVector = Vector2(enemyPos.mapX, enemyPos.mapY)

            // create bullet

            movementComponent.velocity = playerVector.sub(enemyVector)
        } else {
            movementComponent.velocity.apply { x = 0F; y = 0F }
        }
    }

}