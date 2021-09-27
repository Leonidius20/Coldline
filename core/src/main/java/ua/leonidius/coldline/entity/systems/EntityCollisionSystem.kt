package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.SpriteComponent
import ua.leonidius.coldline.entity.components.TypeComponent
import kotlin.properties.Delegates

/**
 * A system for detecting when player is colliding with an entity
 */
class EntityCollisionSystem(private val getDoorX: () -> Int,
                            private val getDoorY: () -> Int,
                            private val mapToTileCoord: (Float) -> Int,
                            private val onDoorCollision: () -> Unit): IteratingSystem(

    // TODO: instead of getting door x and y here just get door entity coordinates

    Family.all(SpriteComponent::class.java, CollisionComponent::class.java, TypeComponent::class.java).get()) {

    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val spriteMapper = ComponentMapper.getFor(SpriteComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (typeMapper.get(entity).type == EntityType.PLAYER) {
            with(spriteMapper.get(entity).sprite) {
                if (mapToTileCoord(x) == getDoorX() && mapToTileCoord(y) == getDoorY()) {
                    // TODO: emit event to end the game
                    onDoorCollision()
                }
                // check is tile X is the same as door's
            }
        }
    }

}