package ua.leonidius.coldline.controller

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import ua.leonidius.coldline.screens.game.GameScreen

class KeyboardController(private val gameScreen: GameScreen): InputProcessor {

    var left = false
    var right = false
    var up = false
    var down = false

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> up = true
            Input.Keys.A -> left = true
            Input.Keys.S -> down = true
            Input.Keys.D -> right = true
            Input.Keys.Z -> gameScreen.switchPathAlgo()
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> up = false
            Input.Keys.A -> left = false
            Input.Keys.S -> down = false
            Input.Keys.D -> right = false
        }
        return true
    }

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false

}