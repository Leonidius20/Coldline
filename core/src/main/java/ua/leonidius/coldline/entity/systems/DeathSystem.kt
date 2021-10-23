package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.HealthComponent

class DeathSystem: IteratingSystem(
    Family.all(HealthComponent::class.java).get()
) {

    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (healthMapper.get(entity).health <= 0) {
            engine.removeEntity(entity)

            // TODO: if it is player - game over
        }
    }

}