package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.TextureComponent

class WallCollisionSystem(private val collisionLayer: TiledMapTileLayer): IteratingSystem(
    Family.all(MovementComponent::class.java, CollisionComponent::class.java).get(), 2) {

    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    private val textureMapper = ComponentMapper.getFor(TextureComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val positionComponent = positionMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)
        val movementComponent = movementMapper.get(entity)

        val height = textureMapper.get(entity).texture.height
        val width = textureMapper.get(entity).texture.width

        with(positionComponent) {
            val newX = mapX + movementComponent.velocity.x * deltaTime
            val newY = mapY + movementComponent.velocity.y * deltaTime

            collisionComponent.collidesOnX =
                if (movementComponent.velocity.x < 0)
                /*isTileWithCollisionAt(newX, y + height) // using old Y here so avoid going through corners diagonally
                        || isTileWithCollisionAt(newX, y / 2)
                        ||*/ isTileWithCollisionAt(newX, mapY)
                else if (movementComponent.velocity.x > 0)
                /*isTileWithCollisionAt(newX + width, y + height)
                        || isTileWithCollisionAt(newX + width, y / 2)
                        ||*/ isTileWithCollisionAt(newX + width, mapY)
                else false

            collisionComponent.collidesOnY =
                if (movementComponent.velocity.y < 0)  // down (or up)
                    isTileWithCollisionAt(mapX, newY)
                /*|| isTileWithCollisionAt(x + width / 2, newY)
                || isTileWithCollisionAt(x + width, newY)*/
                else if (movementComponent.velocity.y > 0)
                    isTileWithCollisionAt(mapX, newY + height)
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