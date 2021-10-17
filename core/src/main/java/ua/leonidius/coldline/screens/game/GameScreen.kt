package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.utils.ScreenUtils
import ua.leonidius.coldline.Main
import ua.leonidius.coldline.controller.KeyboardController
import ua.leonidius.coldline.entity.components.SpriteComponent
import ua.leonidius.coldline.entity.systems.*
import ua.leonidius.coldline.level.Level
import ua.leonidius.coldline.level.LevelGenerator
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

    private val assetManager = AssetManager()

    // TODO: create tile set loader
    private val tileSet = Level.load("maps/level2.tmx").map.tileSets.getTileSet(0)
    private val levelGenerator = LevelGenerator(tileSet)
    private val level = levelGenerator.generate()
    private val renderer = MapWithObjectsRenderer(level, 1F)

    private val keyboardController = KeyboardController(this)

    private var exitTileX = levelGenerator.doorTileX
    private var exitTileY = levelGenerator.doorTileY

    val pathRenderer = PathRenderer(camera,
        level.collisionLayer, level.objectLayer, tileSet.getTile(95))

    val engine = PooledEngine().apply {
        addSystem(RenderingSystem(renderer.batch, camera,
            guiCamera, game.bitmapFont, ::mapToTileCoordinate))
        addSystem(PlayerControlSystem(keyboardController))
        addSystem(MovementSystem())
        addSystem(WallCollisionSystem(level.collisionLayer))
        addSystem(EntityCollisionSystem(
            { exitTileX }, { exitTileY },
            ::mapToTileCoordinate, game::toMenuScreen))
        addSystem(PathHighlightingSystem(pathRenderer))
    }

    private lateinit var playerSprite: Sprite

    override fun show() {
        Gdx.input.inputProcessor = keyboardController

        createEnemy(30, 12)
        createEnemy(30, 24)
        createEnemy(40, 30)
        createEnemy(38, 19)
        createEnemy(16, 45)
        createEnemy(27, 45)

        with (level.objectLayer.objects.get("spawnPoint") as RectangleMapObject) {
            val player = createPlayer(rectangle.x, rectangle.y)
            playerSprite = player.getComponent(SpriteComponent::class.java).sprite
        }

        createDoor(exitTileX, exitTileX)
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
        level.dispose()
        renderer.dispose()
        playerSprite.texture.dispose()
    }

    fun switchPathAlgo() {
        pathRenderer.switchPathAlgorithm()
    }

    fun switchPathHighlightingMode() {
        pathRenderer.displayWholePath = !pathRenderer.displayWholePath
    }

    private fun mapToTileCoordinate(coordinate: Float) =
        (coordinate / (level.collisionLayer.tileWidth)).toInt()

    fun tileToMapCoordinate(coordinate: Int) =
        (coordinate * level.collisionLayer.tileWidth).toFloat()

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