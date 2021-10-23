package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.EnemyComponent
import ua.leonidius.coldline.entity.components.PositionComponent

class CombatActivatorSystem(private val playerPos: PositionComponent): IteratingSystem(
    Family.all(EnemyComponent::class.java).get()
) {

    private val positionMapper = ComponentMapper.getFor(PositionComponent::class.java)
    private val enemyMapper = ComponentMapper.getFor(EnemyComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val enemyPos = positionMapper.get(entity)
        val enemyComponent = enemyMapper.get(entity)
        enemyComponent.isTriggered = isInVicinityOf(enemyPos)
    }

    private fun isInVicinityOf(enemyPos: PositionComponent): Boolean {
        return playerPos.tileX in (enemyPos.tileX - 3)..(enemyPos.tileX + 3)
                && playerPos.tileY in (enemyPos.tileY - 3)..(enemyPos.tileY + 3)
    }

}