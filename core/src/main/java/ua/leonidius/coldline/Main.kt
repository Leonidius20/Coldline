package ua.leonidius.coldline

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : Game() {

    lateinit var batch: SpriteBatch
    lateinit var bitmapFont: BitmapFont

    private lateinit var gameScreen: GameScreen

    override fun create() {
        batch = SpriteBatch()

        bitmapFont = BitmapFont()

        toMenuScreen()
    }

    fun toGameScreen() {
        if (!this::gameScreen.isInitialized)
            gameScreen = GameScreen(this)
        setScreen(gameScreen)
    }

    fun toMenuScreen() {
        setScreen(MenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        bitmapFont.dispose()

        if (this::gameScreen.isInitialized)
            gameScreen.dispose()
    }

}