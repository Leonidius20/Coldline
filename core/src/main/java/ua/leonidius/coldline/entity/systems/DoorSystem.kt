package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.DoorComponent
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.TypeComponent

class DoorSystem(private val onWin: () -> Unit)
    : IteratingSystem(Family.one(DoorComponent::class.java).get()) {

    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)

    override fun processEntity(doorEntity: Entity?, deltaTime: Float) {
        val collidesWith = collisionMapper.get(doorEntity).collidesWith
        if (collidesWith != null && typeMapper.get(collidesWith).type == EntityType.PLAYER) {
            // TODO: game won event
            onWin()
        }
    }

}