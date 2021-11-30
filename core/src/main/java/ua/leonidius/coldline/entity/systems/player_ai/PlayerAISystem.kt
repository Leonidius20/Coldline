package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.entity.components.*
import ua.leonidius.coldline.level.Level
import ua.leonidius.coldline.screens.game.GameScreen

class PlayerAISystem(private val level: Level) : IntervalIteratingSystem(
    Family.all(PlayerComponent::class.java).get(), 0.25F
) {

    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    private val usedNodes = emptyList<Pair<Int, Int>>().toMutableList()

    private val strategy = MinMaxPlayerAIStrategy(
        { entity, pair -> isPositionTerminal(entity, pair.first, pair.second) },
        { entity, pair -> evaluatePosition(entity, pair.first, pair.second) },
        { entity -> getPossibleMoves(entity) }
    )

    override fun processEntity(player: Entity) {
        val nextMove = strategy.findNextMove(player,
            engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).toList(),
            positionMapper.get(player).tileXY)

        usedNodes.add(nextMove.position)

        movementMapper.get(player).velocity = Vector2(nextMove.move!!.first.toFloat(), nextMove.move!!.second.toFloat()).apply {
            x *= 32
            y *= 32
        }

        GameScreen.instance.move = nextMove.move!!
    }

    // the closer to the door, the better
    // punishment for hitting an enemy, bonus for hitting a chest
    private fun evaluatePosition(player: Entity, tileX: Int, tileY: Int): Double {
        var score = 0.0

        val door = engine.getEntitiesFor(Family.one(DoorComponent::class.java).get()).first()
        val doorPos = positionMapper.get(door)

        val playerPos = positionMapper.get(player)

        val distToDoor = Vector2.dst(
            doorPos.tileX.toFloat(), doorPos.tileY.toFloat(),
            playerPos.tileX.toFloat(), playerPos.tileY.toFloat()
        )

        score -= distToDoor

        if (distToDoor == 0F) score += 500

        // punishment for used nodes
        val nodeWasUsed = usedNodes.filter { it.first == tileX && it.second == tileY }
        score -= nodeWasUsed.size * 50
        GameScreen.instance.reusing = nodeWasUsed.size

        for (entity in engine.entities) {
            val pos = positionMapper.get(entity)
            if (pos.tileX == tileX && pos.tileY == tileY) {
                val type = typeMapper.get(entity).type
                if (type == EntityType.CHEST) score += 100
                else if (type == EntityType.ENEMY_DUMB
                    || type == EntityType.ENEMY_SMART
                ) score -= 100
            }
        }

        return score
    }

    private fun isPositionTerminal(player: Entity, tileX: Int, tileY: Int): Boolean {
        for (entity in engine.entities) {
            val pos = positionMapper.get(entity)
            if (pos.tileX == tileX && pos.tileY == tileY) {
                val type = typeMapper.get(entity).type
                if (type == EntityType.DOOR) return true
                else if (type == EntityType.ENEMY_DUMB
                    || type == EntityType.ENEMY_SMART
                ) {
                    if (healthMapper.get(player).health <= 20) return true
                }
            }
        }

        return false
    }

    // account for walls
    private fun getPossibleMoves(entity: Entity): MutableList<Pair<Int, Int>> {
        val playerPos = positionMapper.get(entity)

        val moves = mutableListOf(
            Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0),
            Pair(-1, -1), Pair(-1, +1), Pair(+1, -1), Pair(+1, +1),
        )

        moves.retainAll {
            !level.isTileWithCollisionAt(playerPos.tileX + it.first,
                playerPos.tileY + it.second)
        }

        return moves
    }

}