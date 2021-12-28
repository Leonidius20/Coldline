package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.*

/**
 * System to move entities that have collision
 */
class MovementSystem: IteratingSystem(
    Family.all(MovementComponent::class.java,
        TextureComponent::class.java,
        CollisionComponent::class.java).get(), 3
) {

    private val movementMapper
        = ComponentMapper.getFor(MovementComponent::class.java)

    private val positionMapper
        = ComponentMapper.getFor(PositionComponent::class.java)

    private val collisionMapper: ComponentMapper<CollisionComponent>
            = ComponentMapper.getFor(CollisionComponent::class.java)

    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)

    var deltaAcc = 0.0

    override fun processEntity(entity: Entity, delta: Float) {
        val movementComponent = movementMapper.get(entity)
        val positionComponent = positionMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)

        if (typeMapper.get(entity).type == EntityType.ENEMY_DUMB || typeMapper.get(entity).type == EntityType.ENEMY_SMART) {
            deltaAcc += delta
            if (delta > 0.25) deltaAcc = 0.0
            else return
        }

        var deltaX = movementComponent.velocity.first
        var deltaY = movementComponent.velocity.second

        positionComponent.apply {
            if (deltaX < 0 && !collisionComponent.collidesOnXNegative
                || deltaX > 0 && !collisionComponent.collidesOnXPositive)
            {
                tileX += deltaX
            } else deltaX = 0

            if (deltaY < 0 && !collisionComponent.collidesOnYNegative
                || deltaY > 0 && !collisionComponent.collidesOnYPositive) {
                tileY += deltaY
            } else deltaY = 0
        }

        if (typeMapper.get(entity).type == EntityType.PLAYER) {
            val scoreMapper = ComponentMapper.getFor(ScoreComponent::class.java)
            scoreMapper.get(entity).distanceTraversed += if (deltaX + deltaY == 0) 0 else 1
        }

    }

}