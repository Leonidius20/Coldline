package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import ua.leonidius.coldline.entity.components.BulletComponent

class BulletSystem: IteratingSystem(Family.all(BulletComponent::class.java).get()) {

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        TODO("Not yet implemented")
    }

}