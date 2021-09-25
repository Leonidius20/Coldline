package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.ScreenUtils
import ua.leonidius.coldline.Main
import ua.leonidius.coldline.controller.KeyboardController
import ua.leonidius.coldline.entity.components.SpriteComponent
import ua.leonidius.coldline.entity.systems.*
import ua.leonidius.coldline.renderer.MapWithObjectsRenderer
import ua.leonidius.coldline.renderer.PathRenderer


class GameScreen(private val game: Main) : Screen {

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
        zoom = 0.3F
    }

    private val guiCamera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    private val map = TmxMapLoader().load("maps/level2.tmx")
    private val renderer = MapWithObjectsRenderer(map, 1F)

    private val collisionLayer = map.layers.get("collision") as TiledMapTileLayer

    private val keyboardController = KeyboardController(this)

    val engine = PooledEngine().apply {
        addSystem(RenderingSystem(renderer.batch, camera,
            guiCamera, game.bitmapFont, ::mapToTileCoordinate))
        addSystem(PlayerControlSystem(keyboardController))
        addSystem(MovementSystem())
        addSystem(WallCollisionSystem(collisionLayer))
        addSystem(EntityCollisionSystem())
    }

    private val pathRenderer = PathRenderer(this, camera, map.layers["objects"])

    private var exitTileX = 45
    private var exitTileY = 45

    private lateinit var playerSprite: Sprite

    override fun show() {
        Gdx.input.inputProcessor = keyboardController

        createEnemy(30, 12)
        createEnemy(30, 24)
        createEnemy(40, 30)
        createEnemy(38, 19)
        createEnemy(16, 45)
        createEnemy(27, 45)

        val player = createPlayer(45, 6)
        playerSprite = player.getComponent(SpriteComponent::class.java).sprite

        createDoor(exitTileX, exitTileX) // TODO: make these coords work
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0F, 0F, 0.2F, 1F)

        // rendering map and player
        renderer.run {
            setView(camera)
            render()

            with(batch) {
                begin()
                projectionMatrix = guiCamera.combined
                printDebugData(this)
                end()
            }
        }

        // rendering path
        pathRenderer.render()

        engine.update(delta)

        // checking if it's the exit
        /*if (player.getTileX() == exitTileX && player.getTileY() == exitTileY) {
            game.toMenuScreen()
        }*/
    }

    override fun resize(width: Int, height: Int) {
        camera.run {
            viewportWidth = width.toFloat()
            viewportHeight = height.toFloat()
            update()
        }
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        map.dispose()
        renderer.dispose()
        playerSprite.texture.dispose()
    }

    fun switchPathAlgo() {
        pathRenderer.switchPathAlgorithm()
    }

    private fun mapToTileCoordinate(coordinate: Float) =
        (coordinate / (collisionLayer.tileWidth)).toInt()

    fun tileToMapCoordinate(coordinate: Int) =
        (coordinate * collisionLayer.tileWidth).toFloat()

    fun getPlayerX() = playerSprite.x

    fun getPlayerY() = playerSprite.y

    fun getPlayerTileX() = mapToTileCoordinate(getPlayerX())

    fun getPlayerTileY() = mapToTileCoordinate(getPlayerY())

    private fun printDebugData(batch: Batch) {
        printPathComputeTime(batch)
        printPlayerLocation(batch)
        printDoorLocation(batch)
    }

    private fun printPathComputeTime(batch: Batch) {
        pathRenderer.let {
            if (it.lastUsedAlgorithm.isNotEmpty()) {
                game.bitmapFont.draw(
                    batch,
                    "${it.lastUsedAlgorithm}: ${it.lastComputeTime}ms",
                    0F, 80F
                )
            }
        }
    }

    private fun printDoorLocation(batch: Batch) {
        game.bitmapFont.draw(
            batch,
            "doorX = $exitTileX, doorY = $exitTileY",
            0F, 20F
        )
    }

    private fun printPlayerLocation(batch: Batch) {
        game.bitmapFont.draw(
            batch,
            "x = ${getPlayerTileX()}, y = ${getPlayerTileY()}",
            0F, 50F
        )

    }

}