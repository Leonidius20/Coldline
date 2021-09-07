package ua.leonidius.coldline

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils

/** First screen of the application. Displayed after the application is created.  */
class MenuScreen(private val game: Main) : Screen {

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    override fun show() {
        // Prepare your screen here.
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0F, 0F, 0.2F, 1F)

        camera.update()

        with(game) {
            batch.projectionMatrix = camera.combined
            batch.begin()

            bitmapFont.draw(batch, "Welcome", 100F, 150F)
            bitmapFont.draw(batch, "Tap anywhere to begin", 100F, 100F)

            batch.end()
        }

        if (Gdx.input.isTouched)
            game.toGameScreen()
            dispose()
    }

    override fun resize(width: Int, height: Int) {
        // Resize your screen here. The parameters represent the new window size.
    }

    override fun pause() {
        // Invoked when your application is paused.
    }

    override fun resume() {
        // Invoked when your application is resumed after pause.
    }

    override fun hide() {
        // This method is called when another screen replaces this one.
    }

    override fun dispose() {
        // Destroy screen's assets here.
    }
}