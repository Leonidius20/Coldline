package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.HealthComponent
import ua.leonidius.coldline.entity.components.PlayerComponent
import ua.leonidius.coldline.screens.game.GameScreen

class DeathSystem: IteratingSystem(
    Family.all(HealthComponent::class.java).get()
) {

    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (healthMapper.get(entity).health <= 0) {
            // TODO: replace with type component
            if (entity.components.any { it is PlayerComponent }) {
                GameScreen.instance.gameOver()
            }

            engine.removeEntity(entity)
        }
    }

}