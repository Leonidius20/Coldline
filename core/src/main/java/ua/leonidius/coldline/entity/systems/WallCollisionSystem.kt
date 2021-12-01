package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import ua.leonidius.coldline.entity.components.CollisionComponent
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.level.GameCoordinates
import ua.leonidius.coldline.level.Level

class WallCollisionSystem(private val level: Level): IntervalIteratingSystem(
    Family.all(MovementComponent::class.java, CollisionComponent::class.java).get(), 0.25F, 2) {

    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)

    override fun processEntity(entity: Entity) {
        val positionComponent = positionMapper.get(entity)
        val collisionComponent = collisionMapper.get(entity)

        with(positionComponent) {
            val leftTileXY = GameCoordinates.fromTile(tileX - 1, tileY)
            val rightTileXY = GameCoordinates.fromTile(tileX + 1, tileY)
            val topTileXY = GameCoordinates.fromTile(tileX, tileY + 1)
            val bottomTileXY = GameCoordinates.fromTile(tileX, tileY - 1)

            collisionComponent.collidesOnXNegative =
                    level.isTileWithCollisionAt(leftTileXY)

            collisionComponent.collidesOnXPositive =
                    level.isTileWithCollisionAt(rightTileXY)

            collisionComponent.collidesOnYNegative =
                    level.isTileWithCollisionAt(bottomTileXY)

            collisionComponent.collidesOnYPositive =
                    level.isTileWithCollisionAt(topTileXY)
        }
    }


}