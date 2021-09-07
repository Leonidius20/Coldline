package ua.leonidius.coldline

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils
import ua.leonidius.coldline.FirstScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class Main : Game() {

    lateinit var camera: OrthographicCamera
    lateinit var batch: SpriteBatch
    lateinit var bitmapFont: BitmapFont

    // private lateinit var menuScreen: MenuScreen
    private lateinit var gameScreen: GameScreen

    override fun create() {
        camera = OrthographicCamera()
        camera.setToOrtho(false, 800F, 480F)

        batch = SpriteBatch()

        bitmapFont = BitmapFont()

        // setScreen(FirstScreen())
        toGameScreen()
    }

    fun toGameScreen() {
        if (!this::gameScreen.isInitialized)
            gameScreen = GameScreen(this)
        setScreen(gameScreen)
    }

    override fun dispose() {
        batch.dispose()
        bitmapFont.dispose()
        if (this::gameScreen.isInitialized)
            gameScreen.dispose()
    }

}