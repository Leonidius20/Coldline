package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.*
import kotlin.math.pow
import kotlin.math.sqrt

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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val movementComponent = movementMapper.get(entity)
        val positionComponent = positionMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)

        var deltaX = movementComponent.velocity.x * movementComponent.speed * deltaTime
        var deltaY = movementComponent.velocity.y * movementComponent.speed * deltaTime

        positionComponent.apply {
            if (!collisionComponent.collidesOnX) {
                mapX += deltaX
            } else deltaX = 0F

            if (!collisionComponent.collidesOnY)
                mapY += deltaY
            else deltaY = 0F
        }

        if (typeMapper.get(entity).type == EntityType.PLAYER) {
            val scoreMapper = ComponentMapper.getFor(ScoreComponent::class.java)
            scoreMapper.get(entity).distanceTraversed +=
                sqrt(deltaX.pow(2) + deltaY.pow(2)) / 16
        }

    }

}