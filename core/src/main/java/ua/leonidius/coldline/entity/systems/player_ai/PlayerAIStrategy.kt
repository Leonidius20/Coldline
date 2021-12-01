package ua.leonidius.coldline.entity.systems.player_ai

import com.badlogic.ashley.core.Entity
import ua.leonidius.coldline.level.GameCoordinates

interface PlayerAIStrategy {

    fun findNextMove(player: Entity, enemies: List<Entity>, playerPos: GameCoordinates): MinMaxPlayerAIStrategy.Node

}