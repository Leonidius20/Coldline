package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.systems.player_ai.MinMaxPlayerAIStrategy.Node
import ua.leonidius.coldline.level.GameCoordinates

class ExpectiMaxPlayerAIStrategy(
    private val isTerminalState: (Entity, GameCoordinates) -> Boolean,
    private val heuristic: (Entity, GameCoordinates) -> Double,
    private val getPossibleMoves: (Entity) -> List<Pair<Int, Int>>
): PlayerAIStrategy {

    val maxDepth = 2

    private  val playerIndex = -1 // const

    private var player: Entity? = null
    private var enemies = emptyList<Entity>()

    /*class Node (
        var levelState: Map<Entity, GameCoordinates>,
        var value: Float = -1F,
    )*/

    override fun findNextMove(player: Entity, enemies: List<Entity>, playerPos: GameCoordinates): Node {
        this.player = player
        this.enemies = enemies

        val levelState = mutableMapOf<Entity, GameCoordinates>()
        levelState[player] = playerPos.clone()

        val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
        for (enemy in enemies) {
            levelState[enemy] = positionMapper.get(enemy).clone()
        }

        return expectimax(0, Node(levelState = levelState), playerIndex)
    }

    // max depth is always 1
    private fun expectimax(depth: Int, node: Node, currentAgentIndex: Int): Node {
        if (this.isTerminalState(player!!, node.levelState[player]!!)) {
            node.value = this.heuristic(player!!, node.levelState[player]!!).toFloat()
            return node
        }

        // idk if this is right

        val currentAgent = if (currentAgentIndex == -1) {
            player!!
        } else enemies[currentAgentIndex]


        if (currentAgentIndex == -1) { // player/maximizer
            val nextAgentIndex = 0

            return getPossibleMoves(currentAgent)
                .map { // mapping to evaluated nodes
                    expectimax(depth, Node(cloneLevelState(node.levelState).apply {
                        this[currentAgent]!!.addVector(it)
                    }), nextAgentIndex)
                }.maxByOrNull {
                    it.value
                }!!

        } else { // minimizer agent
            val nextDepth = if (currentAgentIndex == enemies.size - 1) depth + 1 else depth

            if (currentAgentIndex == enemies.size - 1 && nextDepth == maxDepth) { // last enemy last depth
                val possibleMoves = getPossibleMoves(currentAgent)
                val probability = 1 / possibleMoves.size

                // get ave score
                // mapping to next nodes estimated values
                val score = possibleMoves // TODO: make eval func dependent on enemy pos
                    .sumOf { // mapping to next nodes estimated values
                        heuristic(player!!, Node(cloneLevelState(node.levelState).apply {
                            this[currentAgent]!!.addVector(it)
                        }).levelState[player]!!) * probability // TODO: make eval func dependent on enemy pos
                    }

                return node.apply { value = score.toFloat() }
            } else {
                val nextAgentIndex =
                    if (currentAgentIndex + 1 == enemies.size)
                        -1
                    else
                        currentAgentIndex + 1

                val possibleMoves = getPossibleMoves(currentAgent)
                val probability = 1 / possibleMoves.size

                val score = possibleMoves // TODO: make eval func dependent on enemy pos
                    .map { // mapping to next nodes estimated values
                        expectimax(nextDepth, Node(cloneLevelState(node.levelState).apply {
                            this[currentAgent]!!.addVector(it)
                        }), nextAgentIndex).value * probability // TODO: make eval func dependent on enemy pos
                    }
                    .sum()

                return node.apply { value = score }
            }


        }
    }

    private fun cloneLevelState(map: Map<Entity, GameCoordinates>): Map<Entity, GameCoordinates> {
        val new = mutableMapOf<Entity, GameCoordinates>()
        map.forEach { (entity, xy) ->
            new[entity] = xy.clone()
        }
        return new
    }



}