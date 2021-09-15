package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.TextureComponent

class RenderingSystem(
    private val batch: Batch,
    private val camera: OrthographicCamera
) : SortedIteratingSystem(
    Family.all(
        PositionComponent::class.java,
        TextureComponent::class.java
    ).get(), ZComparator()
) {

    private val renderQueue = ArrayDeque<Entity>()

    private val textureMapper = ComponentMapper.getFor(TextureComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        camera.update()
        with(batch) {
            projectionMatrix = camera.combined
            // enableBlending() idk what this is
            begin()

            for (entity in renderQueue) {
                val textureComponent = textureMapper.get(entity)
                val positionComponent = positionMapper.get(entity)

                draw(
                    textureComponent.textureRegion,
                    positionComponent.position.x,
                    positionComponent.position.y
                )
            }

            end()
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        renderQueue.addLast(entity)
    }

    companion object {

        class ZComparator : Comparator<Entity> {

            private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

            override fun compare(entity1: Entity, entity2: Entity): Int {
                // todo idiomatic kotlin
                val z1: Float = positionMapper.get(entity1).position.z
                val z2: Float = positionMapper.get(entity2).position.z
                return if (z1 > z2) 1
                else if (z1 < z2) -1
                else 0
            }

        }

    }

}