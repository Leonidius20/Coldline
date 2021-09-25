package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.SpriteComponent

/**
 * System to move entities that have collision
 */
class MovementSystem: IteratingSystem(
    Family.all(MovementComponent::class.java,
        SpriteComponent::class.java,
        CollisionComponent::class.java).get(), 3
) {

    private val movementMapper: ComponentMapper<MovementComponent>
        = ComponentMapper.getFor(MovementComponent::class.java)

    private val spriteMapper: ComponentMapper<SpriteComponent>
        = ComponentMapper.getFor(SpriteComponent::class.java)

    private val collisionMapper: ComponentMapper<CollisionComponent>
            = ComponentMapper.getFor(CollisionComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val movementComponent = movementMapper.get(entity)
        val spriteComponent = spriteMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)

        spriteComponent.sprite.apply {
            if (!collisionComponent.collidesOnX)
                x += (movementComponent.velocity.x * deltaTime)
            if (!collisionComponent.collidesOnY)
                y += (movementComponent.velocity.y * deltaTime)
        }
    }

}