package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.entity.components.*
import ua.leonidius.coldline.level.Level
import ua.leonidius.coldline.pathfinding.algorithms.BreadthFirstSearchStrategy

class EnemyCombatSystem(private val playerPos: PositionComponent,
                        private val level: Level): IntervalIteratingSystem(
    Family.all(EnemyComponent::class.java).get(), 0.25F
) {

    private val enemyMapper = ComponentMapper.getFor(EnemyComponent::class.java)
    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)

    override fun processEntity(entity: Entity?) {
        val enemyComponent = enemyMapper.get(entity)
        val movementComponent = movementMapper.get(entity)
        if (enemyComponent.isTriggered) {
            val enemyPos: PositionComponent = positionMapper.get(entity)

            val enemyVector = Vector2(enemyPos.mapX, enemyPos.mapY)

            if (enemyComponent.isDumb) {
                val playerVector = Vector2(playerPos.mapX, playerPos.mapY)
                movementComponent.velocity = playerVector.sub(enemyVector).nor()
            } else {
                // TODO: fix this shit
                val enemyNode = level.tileGraph.findNodeAt(enemyPos.tileX, enemyPos.tileY)!!
                val playerNode = level.tileGraph.findNodeAt(playerPos.tileX, playerPos.tileY)!!

                val path = BreadthFirstSearchStrategy().findPath(
                    level.tileGraph, enemyNode, playerNode)!!

                val nextNode = path[1]
                val destinationVector = Vector2(nextNode.mapX, nextNode.mapY)

                movementComponent.velocity = destinationVector.sub(enemyVector).nor()
            }
        } else {
            movementComponent.velocity.apply { x = 0F; y = 0F }
        }
    }

    private fun createBullet(direction: Vector2, mapX: Float, mapY: Float) {
        with(engine) {
            createEntity().run {
                add(createComponent(BulletComponent::class.java))

                add(createComponent(ShapeComponent::class.java).apply {
                    // shape of line
                })

                add(createComponent(PositionComponent::class.java).apply {
                    // mapX = playerMapX
                    // mapY = playerMapY
                })

                add(createComponent(CollisionComponent::class.java))

                add(createComponent(MovementComponent::class.java))

                add(createComponent(TypeComponent::class.java).apply {
                    type = EntityType.BULLET
                })

                addEntity(this)
            }
        }
    }

}