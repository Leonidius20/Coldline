package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ua.leonidius.coldline.entity.components.*

/**
 * System to move entities that have collision
 */
class MovementSystem: IntervalIteratingSystem(
    Family.all(MovementComponent::class.java,
        TextureComponent::class.java,
        CollisionComponent::class.java).get(), 0.25F, 3
) {

    private val movementMapper
        = ComponentMapper.getFor(MovementComponent::class.java)

    private val positionMapper
        = ComponentMapper.getFor(PositionComponent::class.java)

    private val collisionMapper: ComponentMapper<CollisionComponent>
            = ComponentMapper.getFor(CollisionComponent::class.java)

    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)

    override fun processEntity(entity: Entity) {
        val movementComponent = movementMapper.get(entity)
        val positionComponent = positionMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)

        var deltaX = movementComponent.velocity.first * movementComponent.speed
        var deltaY = movementComponent.velocity.second * movementComponent.speed

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