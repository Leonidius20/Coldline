package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.level.GameCoordinates

class MinMaxPlayerAIStrategy(
    private val isTerminalState: (Entity, GameCoordinates) -> Boolean,
    private val heuristic: (Entity, GameCoordinates) -> Double,
    private val getPossibleMoves: (Entity) -> List<Pair<Int, Int>>
): PlayerAIStrategy {

    data class Node(
        var levelState: Map<Entity, GameCoordinates>,
        var value: Float = -1F,
    )

    private val maxDepth = 3

    private  val playerIndex = -1 // const

    private var player: Entity? = null
    private var enemies = emptyList<Entity>()

    override fun findNextMove(player: Entity, enemies: List<Entity>, playerPos: GameCoordinates): Node {
        this.player = player
        this.enemies = enemies

        val levelState = mutableMapOf<Entity, GameCoordinates>()
        levelState[player] = playerPos.clone()

        val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
        for (enemy in enemies) {
            levelState[enemy] = positionMapper.get(enemy).clone()
        }

        return minmax(0, Node(levelState), playerIndex, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)
    }

    private fun minmax(currentDepthVal: Int, node: Node, currentAgentIndex: Int, alphaVal: Float, betaVal: Float): Node {
        var alpha = alphaVal
        var beta = betaVal
        var currentDepth = currentDepthVal

        if (currentDepth >= this.maxDepth || this.isTerminalState(player!!, node.levelState[player]!!)) {
            node.value = this.heuristic(player!!, node.levelState[player]!!).toFloat()
            return node
        }

        // idk if this is right

        val currentAgent = if (currentAgentIndex == -1) {
            player!!
        } else enemies[currentAgentIndex]

        var bestValue = if (currentAgentIndex == playerIndex) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
        var bestNode: Node? = null

        for (move in getPossibleMoves(currentAgent)) {
            val newLevelState = cloneLevelState(node.levelState)
            newLevelState[currentAgent]!!.addVector(move)

            val newNode = Node(newLevelState)

            val nextAgentIndex =
                if (currentAgent == player) 0 // player -> first enemy
                else if (currentAgentIndex == enemies.size - 1) {
                    currentDepth += 1
                    -1
                } // last enemy -> player
                else currentAgentIndex + 1 // non-last enemy -> next enemy


            // TODO: check why this newEvalNode is not the same as simply newNode
            val newEvaluatedNode = this.minmax(currentDepth, newNode, nextAgentIndex, alpha, beta)

            // player == maximizer
            if (currentAgent == player && bestValue < newEvaluatedNode.value) {
                bestNode = newEvaluatedNode
                bestValue = newEvaluatedNode.value
                alpha = alpha.coerceAtLeast(bestValue)
                if (beta <= alpha) {
                    break
                }
            } else if (currentAgent != player && bestValue > newEvaluatedNode.value) {
                bestNode = newEvaluatedNode
                bestValue = newEvaluatedNode.value
                beta = beta.coerceAtMost(bestValue)
                if (beta <= alpha) {
                    break
                }
            }


        }

        if (bestNode == null) {
            bestNode = node
            bestNode.value = this.heuristic(player!!, node.levelState[player]!!).toFloat() // heuristic should always evaluate it from the player's standpoint!! bc enemies wanna mimimize this value!
        }

        return bestNode
    }

    private fun cloneLevelState(map: Map<Entity, GameCoordinates>): Map<Entity, GameCoordinates> {
        val new = mutableMapOf<Entity, GameCoordinates>()
        map.forEach { (entity, xy) ->
            new[entity] = xy.clone()
        }
        return new
    }

}