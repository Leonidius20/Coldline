package ua.leonidius.coldline

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ua.leonidius.coldline.screens.game.GameScreen
import ua.leonidius.coldline.screens.MenuScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : Game() {

    lateinit var batch: SpriteBatch
    lateinit var bitmapFont: BitmapFont

    override fun create() {
        batch = SpriteBatch()

        bitmapFont = BitmapFont()

        toMenuScreen()
    }

    fun toGameScreen() {
        setScreen(GameScreen(this))
    }

    fun toMenuScreen() {
        setScreen(MenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        bitmapFont.dispose()
    }

}