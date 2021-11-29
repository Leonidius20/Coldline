package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.Entity

class MinMaxPlayerAIStrategy(
    private val player: Entity,
    private val enemies: List<Entity>,
    private val isTerminalState: (Entity, Pair<Int, Int>) -> Boolean,
    private val heuristic: (Entity, Pair<Int, Int>) -> Double,
    private val getPossibleMoves: (Entity) -> List<Pair<Int, Int>>
): PlayerAIStrategy {

    data class Node(
        var position: Pair<Int, Int>,
        var value: Float = -1F,
    )

    private val maxDepth = 5

    private  val playerIndex = -1 // const

    override fun findNextMove(position: Pair<Int, Int>): Pair<Int, Int> {
        return minmax(0, Node(position), playerIndex, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY).position
    }

    private fun minmax(currentDepth: Int, node: Node, currentAgentIndex: Int, alphaVal: Float, betaVal: Float): Node {
        var alpha = alphaVal
        var beta = betaVal

        if (currentDepth == this.maxDepth || this.isTerminalState(player, node.position)) {
            node.value = this.heuristic(player, node.position).toFloat()
            return node
        }

        // idk if this is right

        val currentAgent = if (currentAgentIndex == -1) player else enemies[currentAgentIndex]

        var bestValue = if (currentAgentIndex == playerIndex) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
        var bestNode: Node? = null
        // var bestMove: Pair<Int, Int>? = null

        for (move in getPossibleMoves(currentAgent)) {
            // val newBoardState = applyMove(node.boardState, currentColor, move, alpha, beta);
            val newNode = Node(Pair(
                node.position.first + move.first,
                node.position.second + move.second))

            val nextAgentIndex =
                if (currentAgent == player) 0 // player -> first enemy
                else if (currentAgentIndex == enemies.size - 1) -1 // last enemy -> player
                else currentAgentIndex + 1 // non-last enemy -> next enemy


            val newEvaluatedNode = this.minmax(currentDepth + 1, newNode, nextAgentIndex, alpha, beta)

            // player == maximizer
            if (currentAgent == player && bestValue < newEvaluatedNode.value) {
                bestValue = newEvaluatedNode.value
                bestNode = newEvaluatedNode
                // bestMove = move
                alpha = alpha.coerceAtLeast(bestValue)
                if (beta <= alpha) {
                    break
                }
            } else if (currentAgent != player && bestValue > newEvaluatedNode.value) {
                bestValue = newEvaluatedNode.value
                bestNode = newEvaluatedNode
                // bestMove = move
                beta = beta.coerceAtMost(bestValue)
                if (beta <= alpha) {
                    break
                }
            }


        }

        if (bestNode === null) {
            bestNode = node
            bestValue = this.heuristic(currentAgent, node.position).toFloat()
        }

        bestNode.value = bestValue
        // bestNode.move = bestMove /
        return bestNode
    }

}