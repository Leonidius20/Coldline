package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.ashley.utils.ImmutableArray
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.TypeComponent


/**
 * A system for detecting when player is colliding with an entity
 */
class EntityCollisionSystem(private val getDoorX: () -> Int,
                            private val getDoorY: () -> Int,
                            private val onDoorCollision: () -> Unit): IntervalSystem(0.25F) {
    // TODO: instead of getting door x and y here just get door entity coordinates
    // TODO: just check if some two enttit
    private lateinit var entities: ImmutableArray<Entity>

    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(Family.all(CollisionComponent::class.java).get())
    }

    override fun updateInterval() {
        for (entity in entities) {
            collisionMapper.get(entity).collidesWith = null
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

        for (i in 0 until entities.size()) {
            for (j in i + 1 until entities.size()) {
                val entity1 = entities[i]
                val entity2 = entities[j]
                val pos1 = positionMapper.get(entity1)
                val pos2 = positionMapper.get(entity2)

                if (pos1.tileX == pos2.tileX && pos1.tileY == pos2.tileY) {
                    collisionMapper.get(entity1).collidesWith = entity2
                    collisionMapper.get(entity2).collidesWith = entity1
                }
            }
        }
    }

}