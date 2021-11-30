package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.Entity

interface PlayerAIStrategy {

    fun findNextMove(player: Entity, enemies: List<Entity>, position: Pair<Int, Int>): MinMaxPlayerAIStrategy.Node

}