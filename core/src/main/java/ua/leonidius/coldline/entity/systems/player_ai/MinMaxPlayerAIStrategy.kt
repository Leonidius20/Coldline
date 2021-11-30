package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.Entity

class MinMaxPlayerAIStrategy(
    private val isTerminalState: (Entity, Pair<Int, Int>) -> Boolean,
    private val heuristic: (Entity, Pair<Int, Int>) -> Double,
    private val getPossibleMoves: (Entity) -> List<Pair<Int, Int>>
): PlayerAIStrategy {

    data class Node(
        var position: Pair<Int, Int>,
        var value: Float = -1F,
        var move: Pair<Int, Int>? = null,
    )

    private val maxDepth = 3

    private  val playerIndex = -1 // const

    private var player: Entity? = null
    private var enemies = emptyList<Entity>()

    override fun findNextMove(player: Entity, enemies: List<Entity>, position: Pair<Int, Int>): Node {
        this.player = player
        this.enemies = enemies
        return minmax(0, Node(position), playerIndex, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)
    }

    private fun minmax(currentDepthVal: Int, node: Node, currentAgentIndex: Int, alphaVal: Float, betaVal: Float): Node {
        var alpha = alphaVal
        var beta = betaVal
        var currentDepth = currentDepthVal

        if (currentDepth >= this.maxDepth || this.isTerminalState(player!!, node.position)) {
            node.value = this.heuristic(player!!, node.position).toFloat()
            return node
        }

        // idk if this is right

        val currentAgent = if (currentAgentIndex == -1) {
            player!!
        } else enemies[currentAgentIndex]

        var bestValue = if (currentAgentIndex == playerIndex) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
        var bestNode: Node? = null
        var bestMove: Pair<Int, Int>? = null

        for (move in getPossibleMoves(currentAgent)) {
            // val newBoardState = applyMove(node.boardState, currentColor, move, alpha, beta);
            val newNode = Node(Pair(
                node.position.first + move.first,
                node.position.second + move.second))

            val nextAgentIndex =
                if (currentAgent == player) 0 // player -> first enemy
                else if (currentAgentIndex == enemies.size - 1) {
                    currentDepth += 1
                    -1
                } // last enemy -> player
                else currentAgentIndex + 1 // non-last enemy -> next enemy


            val newEvaluatedNode = this.minmax(currentDepth, newNode, nextAgentIndex, alpha, beta)

            // player == maximizer
            if (currentAgent == player && bestValue < newEvaluatedNode.value) {
                bestValue = newEvaluatedNode.value
                bestNode = newEvaluatedNode
                bestMove = move
                alpha = alpha.coerceAtLeast(bestValue)
                if (beta <= alpha) {
                    break
                }
            } else if (currentAgent != player && bestValue > newEvaluatedNode.value) {
                bestValue = newEvaluatedNode.value
                bestNode = newEvaluatedNode
                bestMove = move
                beta = beta.coerceAtMost(bestValue)
                if (beta <= alpha) {
                    break
                }
            }


        }

        if (bestNode === null) {
            bestNode = node
            bestValue = this.heuristic(player!!, node.position).toFloat() // heuristic should always evaluate it from the player's standpoint!! bc enemies wanna mimimize this value!
        }

        bestNode.value = bestValue
        bestNode.move = bestMove
        return bestNode
    }

}