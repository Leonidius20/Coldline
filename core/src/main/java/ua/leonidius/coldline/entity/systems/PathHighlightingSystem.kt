package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.PlayerComponent
import ua.leonidius.coldline.entity.components.SpriteComponent
import ua.leonidius.coldline.renderer.PathRenderer

class PathHighlightingSystem(private val renderer: PathRenderer): IteratingSystem(
    Family.all(PlayerComponent::class.java).get(), 0
) {

    private val textureMapper = ComponentMapper.getFor(SpriteComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (!renderer.displayWholePath && renderer.currentDestinationIndex != renderer.path.size) {
            val textureComponent = textureMapper.get(entity)
            val sprite = textureComponent.sprite
            val destination = renderer.path[renderer.currentDestinationIndex]

            if (sprite.x >= destination.mapX - 16
                && sprite.x <= destination.mapX + 16
                && sprite.y >= destination.mapY - 16
                && sprite.y <= destination.mapY + 16) {
                renderer.currentDestinationIndex++
            }
        }

    }
}