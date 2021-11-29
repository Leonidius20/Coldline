package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ua.leonidius.coldline.entity.components.*

/**
 * A system for reducing player's health when in contact with an enemy
 */
class EnemyHarmSystem: IntervalIteratingSystem(
    Family.all(EnemyComponent::class.java).get(), 0.5F
) {

    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    override fun processEntity(entity: Entity) {
        val collision = collisionMapper.get(entity)
        if (collision.collidesWith != null) {
            if (typeMapper.get(collision.collidesWith).type == EntityType.PLAYER) {
                val playerHealth = healthMapper.get(collision.collidesWith)
                playerHealth.health -= 40
            }
        }
    }

}