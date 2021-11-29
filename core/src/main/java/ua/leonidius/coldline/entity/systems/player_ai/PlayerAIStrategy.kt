package ua.leonidius.coldline.entity.systems.player_ai

interface PlayerAIStrategy {

    fun findNextMove(position: Pair<Int, Int>): Pair<Int, Int>

}