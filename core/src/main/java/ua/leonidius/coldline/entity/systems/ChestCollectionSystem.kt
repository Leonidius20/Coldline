package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.*

class ChestCollectionSystem: IteratingSystem(
    Family.all(ChestComponent::class.java).get()
) {

    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val scoreMapper = ComponentMapper.getFor(ScoreComponent::class.java)
    private val chestMapper = ComponentMapper.getFor(ChestComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val chestComponent = chestMapper.get(entity)

        if (chestComponent.isLooted) return

        val collision = collisionMapper.get(entity)
        if (collision.collidesWith != null) {
            if (typeMapper.get(collision.collidesWith).type == EntityType.PLAYER) {
                val player = collision.collidesWith
                chestComponent.isLooted = true
                scoreMapper.get(player).chestsCollected += 1
            }
        }
    }


}