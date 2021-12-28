package ua.leonidius.coldline.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
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
            val velocityY =
                if (keyboardController.up && !madeStepY) {
                    madeStepY = true
                    1
                }
                else if (keyboardController.down && !madeStepY) {
                    madeStepY = true
                    -1
                }
                else 0

            val velocityX =
                if (keyboardController.right && !madeStepX) {
                    madeStepX = true
                    1
                }
                else if (keyboardController.left && !madeStepX) {
                    madeStepX = true
                    -1

                }
                else 0

            if (!keyboardController.down && !keyboardController.up) madeStepY = false
            if (!keyboardController.right && !keyboardController.left) madeStepX = false

            velocity = Pair(velocityX, velocityY)
        }
    }

}