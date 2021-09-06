package ua.leonidius.coldline

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
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

    private lateinit var dropImage: Texture
    private lateinit var bucketImage: Texture
    private lateinit var dropSound: Sound
    private lateinit var rainMusic: Music

    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch

    private lateinit var bucket: Rectangle

    private val touchPos: Vector3 = Vector3()

    private lateinit var raindrops: Array<Rectangle>
    private var lastDropTime = 0L

    override fun create() {
        // setScreen(FirstScreen())

        dropImage = Texture(Gdx.files.internal("droplet.png"))
        bucketImage = Texture(Gdx.files.internal("bucket.png"))
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.wav"))

        rainMusic.isLooping = true
        rainMusic.play()

        camera = OrthographicCamera()
        camera.setToOrtho(false, 800F, 480F)

        batch = SpriteBatch()

        bucket = Rectangle().apply {
            width = 64F
            height = 64F
            x = 800F / 2 - 64F / 2
            y = 20F
        }

        raindrops = Array()
        spawnRaindrop()
    }

    override fun render() {
        ScreenUtils.clear(0F, 0F, 0.2F, 1F)

        camera.update()

        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.draw(bucketImage, bucket.x, bucket.y)
        for (raindrop in raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y)
        }
        batch.end()

        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0F)
            camera.unproject(touchPos)
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

    override fun dispose() {
        dropImage.dispose()
        bucketImage.dispose()
        dropSound.dispose()
        rainMusic.dispose()
        batch.dispose()
    }

}