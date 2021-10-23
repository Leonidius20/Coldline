package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.PlayerComponent
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.renderer.PathRenderer

class PathHighlightingSystem(private val renderer: PathRenderer): IteratingSystem(
    Family.all(PlayerComponent::class.java).get(), 0
) {

    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (!renderer.displayWholePath && renderer.currentDestinationIndex != renderer.path.size) {
            val position = positionMapper.get(entity)
            val destination = renderer.path[renderer.currentDestinationIndex]

            if (position.mapX >= destination.mapX - 16
                && position.mapX <= destination.mapX + 16
                && position.mapY >= destination.mapY - 16
                && position.mapY <= destination.mapY + 16) {
                renderer.currentDestinationIndex++
            }
        }

    }
}