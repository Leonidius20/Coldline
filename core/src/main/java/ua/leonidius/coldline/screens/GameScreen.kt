package ua.leonidius.coldline.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.ScreenUtils
import ua.leonidius.coldline.Main
import ua.leonidius.coldline.entities.Player

class GameScreen(private val game: Main) : Screen {

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    private val map = TmxMapLoader().load("maps/level1.tmx")
    private val renderer = OrthogonalTiledMapRenderer(map, 3F)

    private val player = Player(Sprite(Texture("bucket.png")),
        map.layers[0] as TiledMapTileLayer)

    override fun show() {
        player.moveToTile(5, 5)
        Gdx.input.inputProcessor = player
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0F, 0F, 0.2F, 1F)

        renderer.run {
            setView(camera)
            render()
            batch.run {
                begin()
                player.draw(this)
                end()
            }
        }

    }

    override fun resize(width: Int, height: Int) {
        camera.run {
            viewportWidth = width.toFloat()
            viewportHeight = height.toFloat()
            update()
        }
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        map.dispose()
        renderer.dispose()
        player.texture.dispose()
    }
}