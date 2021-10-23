package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.TextureComponent

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

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val movementComponent = movementMapper.get(entity)
        val positionComponent = positionMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)

        positionComponent.apply {
            if (!collisionComponent.collidesOnX)
                mapX += (movementComponent.velocity.x * movementComponent.speed * deltaTime)
            if (!collisionComponent.collidesOnY)
                mapY += (movementComponent.velocity.y * movementComponent.speed * deltaTime)
        }
    }

}