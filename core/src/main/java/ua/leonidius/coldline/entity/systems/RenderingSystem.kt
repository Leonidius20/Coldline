package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import ua.leonidius.coldline.entity.components.EntityType
import ua.leonidius.coldline.entity.components.PlayerComponent
import ua.leonidius.coldline.entity.components.SpriteComponent
import ua.leonidius.coldline.entity.components.TypeComponent

class RenderingSystem(
    private val batch: Batch,
    private val camera: OrthographicCamera,
    private val guiCamera: OrthographicCamera,
    private val bitmapFont: BitmapFont,
    private val mapToTileCoordinate: (Float) -> Int
) : IteratingSystem(Family.all(SpriteComponent::class.java).get(), 1) {

    private val renderQueue = ArrayDeque<Entity>()

    private val textureMapper = ComponentMapper.getFor(SpriteComponent::class.java)
    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)

    private var lastPlayerX: Int = -1
    private var lastPlayerY: Int = -1

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        // renderQueue sort on Z value

        with(batch) {
            projectionMatrix = camera.combined
            // enableBlending() idk what this is
            begin()

            for (entity in renderQueue) {
                val textureComponent = textureMapper.get(entity)
                textureComponent.sprite.draw(this)

                if (typeMapper.get(entity).type == EntityType.PLAYER) {
                    camera.position.apply {
                        x = textureComponent.sprite.x
                        y = textureComponent.sprite.y
                    }
                }
            }

            end()
        }

        camera.update()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        renderQueue.addLast(entity)
        if (entity.getComponent(PlayerComponent::class.java) != null) {
            val sprite = entity.getComponent(SpriteComponent::class.java).sprite
            lastPlayerX = mapToTileCoordinate(sprite.x)
            lastPlayerY = mapToTileCoordinate(sprite.y)
        }
    }

    /*companion object {

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

    }*/

}