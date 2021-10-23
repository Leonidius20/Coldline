package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.*

/**
 * A system for detecting when player is colliding with an entity
 */
class EntityCollisionSystem(private val getDoorX: () -> Int,
                            private val getDoorY: () -> Int,
                            private val onDoorCollision: () -> Unit): IteratingSystem(

    // TODO: instead of getting door x and y here just get door entity coordinates

    Family.all(TextureComponent::class.java, CollisionComponent::class.java, TypeComponent::class.java).get()) {

    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (typeMapper.get(entity).type == EntityType.PLAYER) {
            with(positionMapper.get(entity)) {
                if (tileX == getDoorX() && tileY == getDoorY()) {
                    // TODO: emit event to end the game
                    onDoorCollision()
                }
                // check is tile X is the same as door's
            }
        }
    }

}