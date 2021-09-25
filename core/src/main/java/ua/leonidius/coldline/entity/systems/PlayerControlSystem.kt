package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import ua.leonidius.coldline.controller.KeyboardController
import ua.leonidius.coldline.entity.components.MovementComponent
import ua.leonidius.coldline.entity.components.PlayerComponent

// setting the velocity if keys are pressed
// not changing the position, MovementSystem will handle that
class PlayerControlSystem(
    private val keyboardController: KeyboardController
): IteratingSystem(
    Family.all(PlayerComponent::class.java).get(), 0
) {

    private val movementMapper = ComponentMapper.getFor(MovementComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val movementComponent = movementMapper.get(entity)
        with(movementComponent) {
            velocity.y =
                if (keyboardController.up) speed
                else if (keyboardController.down) -speed
                else 0F

            velocity.x =
                if (keyboardController.right) speed
                else if (keyboardController.left) -speed
                else 0F
        }
    }

}