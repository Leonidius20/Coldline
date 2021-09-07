package ua.leonidius.coldline

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils

class GameScreen(val game: Main) : Screen {

    private val dropImage = Texture(Gdx.files.internal("droplet.png"))
    private val bucketImage = Texture(Gdx.files.internal("bucket.png"))
    private val dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))
    private val rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.wav"))
        .apply { isLooping = true }



    private val bucket = Rectangle().apply {
        width = 64F
        height = 64F
        x = 800F / 2 - 64F / 2
        y = 20F
    }

    private val touchPos = Vector3()

    private val raindrops = Array<Rectangle>()
    private var lastDropTime = 0L

    init {
        spawnRaindrop()
    }

    private fun spawnRaindrop() {
        val drop = Rectangle().apply {
            x = MathUtils.random(0, 800 - 64).toFloat()
            y = 480F
            width = 64F
            height = 64F
        }
        raindrops.add(drop)
        lastDropTime = TimeUtils.nanoTime()
    }

    override fun show() {
        rainMusic.play()
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0F, 0F, 0.2F, 1F)

        with(game) {
            camera.update()

            batch.projectionMatrix = camera.combined
            batch.begin()
            batch.draw(bucketImage, bucket.x, bucket.y)
            for (raindrop in raindrops) {
                batch.draw(dropImage, raindrop.x, raindrop.y)
            }
            batch.end()
        }

        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0F)
            game.camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= 500 * Gdx.graphics.deltaTime
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 500 * Gdx.graphics.deltaTime
        }

        if(bucket.x < 0) bucket.x = 0F
        if(bucket.x > 800 - 64) bucket.x = 800F - 64F

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop()

        var index = -1
        for (raindrop in raindrops) {
            index++
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0) raindrops.removeIndex(index)
            if (raindrop.overlaps(bucket)) {
                dropSound.play()
                raindrops.removeIndex(index)
            }
        }
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        dropImage.dispose()
        bucketImage.dispose()
        dropSound.dispose()
        rainMusic.dispose()

    }
}