package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.SpriteComponent

class CollisionSystem(private val collisionLayer: TiledMapTileLayer): IteratingSystem(
    Family.all(MovementComponent::class.java, CollisionComponent::class.java).get(), 2) {

    private val spriteMapper: ComponentMapper<SpriteComponent>
            = ComponentMapper.getFor(SpriteComponent::class.java)

    private val collisionMapper: ComponentMapper<CollisionComponent>
            = ComponentMapper.getFor(CollisionComponent::class.java)

    private val movementMapper: ComponentMapper<MovementComponent>
            = ComponentMapper.getFor(MovementComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val spriteComponent = spriteMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)
        val movementComponent = movementMapper.get(entity)

        with(spriteComponent.sprite) {
            val newX = x + movementComponent.velocity.x * deltaTime
            val newY = y + movementComponent.velocity.y * deltaTime

            collisionComponent.collidesOnX =
                if (movementComponent.velocity.x < 0)
                /*isTileWithCollisionAt(newX, y + height) // using old Y here so avoid going through corners diagonally
                        || isTileWithCollisionAt(newX, y / 2)
                        ||*/ isTileWithCollisionAt(newX, y)
                else if (movementComponent.velocity.x > 0)
                /*isTileWithCollisionAt(newX + width, y + height)
                        || isTileWithCollisionAt(newX + width, y / 2)
                        ||*/ isTileWithCollisionAt(newX + width, y)
                else false

            collisionComponent.collidesOnY =
                if (movementComponent.velocity.y < 0)  // down (or up)
                    isTileWithCollisionAt(x, newY)
                /*|| isTileWithCollisionAt(x + width / 2, newY)
                || isTileWithCollisionAt(x + width, newY)*/
                else if (movementComponent.velocity.y > 0)
                    isTileWithCollisionAt(x, newY + height)
                /*|| isTileWithCollisionAt(x + width / 2, newY + height)
                || isTileWithCollisionAt(x + width, newY + height)*/
                else false
        }
    }

    private fun isTileWithCollisionAt(mapX: Float, mapY: Float) =
        collisionLayer.run {
            val cell = getCell((mapX / (tileWidth)).toInt(), (mapY / (tileHeight)).toInt())
            cell?.tile?.properties?.containsKey("blocked") ?: false
        }


}