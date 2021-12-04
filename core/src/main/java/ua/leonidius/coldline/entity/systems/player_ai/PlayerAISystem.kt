package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.entity.components.*
import ua.leonidius.coldline.level.GameCoordinates
import ua.leonidius.coldline.level.Level
import ua.leonidius.coldline.screens.game.GameScreen

class PlayerAISystem(private val level: Level, playerAiAlgo: String) : IntervalIteratingSystem(
    Family.all(PlayerComponent::class.java).get(), 0.25F
) {

    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)
    private val collisionMapper = ComponentMapper.getFor(CollisionComponent::class.java)
    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val typeMapper = ComponentMapper.getFor(TypeComponent::class.java)
    private val healthMapper = ComponentMapper.getFor(HealthComponent::class.java)

    private val usedNodes = emptyList<GameCoordinates>().toMutableList()

    private val strategy = if (playerAiAlgo == "minmax") MinMaxPlayerAIStrategy(
        ::isPositionTerminal,
        ::evaluatePosition,
        ::getPossibleMoves
    ) else ExpectiMaxPlayerAIStrategy(
        ::isPositionTerminal,
        ::evaluatePosition,
        ::getPossibleMoves
    )

    private lateinit var enemies: List<Entity>

    override fun processEntity(player: Entity) {
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent::class.java).get()).toList()

        val nextMove = strategy.findNextMove(player,
           enemies,
           positionMapper.get(player))

        usedNodes.add(nextMove.levelState[player]!!)

        val move = nextMove.levelState[player]!!.vectorSub(positionMapper.get(player))
        movementMapper.get(player).velocity = move

        GameScreen.instance.move = move
    }

    // the closer to the door, the better
    // punishment for hitting an enemy, bonus for hitting a chest
    private fun evaluatePosition(player: Entity, xy: GameCoordinates): Double {
        var score = 1000.0

        val door = engine.getEntitiesFor(Family.one(DoorComponent::class.java).get()).first()
        val doorPos = positionMapper.get(door)

        val playerPos = positionMapper.get(player)

        val distToDoor = Vector2.dst(
            doorPos.tileX.toFloat(), doorPos.tileY.toFloat(),
            playerPos.tileX.toFloat(), playerPos.tileY.toFloat()
        )

        score -= distToDoor

        if (distToDoor == 0F) score += 500

        // punishment for being close to enemies
        for (enemy in enemies) {
            val enemyPos = positionMapper.get(enemy)

            score -= Vector2.dst(
                enemyPos.tileX.toFloat(), enemyPos.tileY.toFloat(),
                playerPos.tileX.toFloat(), playerPos.tileY.toFloat()
            ) / 10
        }


        // punishment for used nodes
        val nodeWasUsed = usedNodes.filter { it.tileX == xy.tileX && it.tileY == xy.tileY }
        score -= nodeWasUsed.size * 50
        GameScreen.instance.reusing = nodeWasUsed.size

        for (entity in engine.entities) {
            val pos = positionMapper.get(entity)
            if (pos.tileX == xy.tileX && pos.tileY == xy.tileY) {
                val type = typeMapper.get(entity).type
                if (type == EntityType.CHEST) score += 100
                else if (type == EntityType.ENEMY_DUMB
                    || type == EntityType.ENEMY_SMART
                ) score -= 100
            }
        }

        return score
    }

    private fun isPositionTerminal(player: Entity, xy: GameCoordinates): Boolean {
        for (entity in engine.entities) {
            val pos = positionMapper.get(entity)
            if (pos.tileX == xy.tileX && pos.tileY == xy.tileY) {
                val type = typeMapper.get(entity).type
                if (type == EntityType.DOOR) return true
                /*else if (type == EntityType.ENEMY_DUMB
                    || type == EntityType.ENEMY_SMART
                ) {
                    if (healthMapper.get(player).health <= 20) return true
                }*/
            }
        }

        return false
    }

    // account for walls
    private fun getPossibleMoves(entity: Entity): MutableList<Pair<Int, Int>> {
        val position = positionMapper.get(entity)

        val moves = mutableListOf(
            Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0),
            Pair(-1, -1), Pair(-1, +1), Pair(+1, -1), Pair(+1, +1),
        )

        moves.retainAll {
            !level.isTileWithCollisionAt(
                GameCoordinates.fromTile( position.tileX + it.first,
                position.tileY + it.second))
        }

        return moves
    }

}