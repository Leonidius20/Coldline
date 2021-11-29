package ua.leonidius.coldline

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ua.leonidius.coldline.screens.MenuScreen
import ua.leonidius.coldline.screens.game.GameScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : Game() {

    lateinit var batch: SpriteBatch
    lateinit var bitmapFont: BitmapFont
    val assetManager = AssetManager()

    override fun create() {
        batch = SpriteBatch()

        bitmapFont = BitmapFont()

        toMenuScreen(null)
    }

    fun toGameScreen() {
        setScreen(GameScreen(this))
    }

    fun toMenuScreen(score: Int?) {
        setScreen(MenuScreen(this, score))
    }

    override fun dispose() {
        batch.dispose()
        bitmapFont.dispose()
        assetManager.dispose()
        Gdx.app.exit()
    }

}