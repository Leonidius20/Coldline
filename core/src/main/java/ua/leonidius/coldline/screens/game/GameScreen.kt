package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.ScreenUtils
import ua.leonidius.coldline.Main
import ua.leonidius.coldline.controller.KeyboardController
import ua.leonidius.coldline.entity.components.HealthComponent
import ua.leonidius.coldline.entity.components.PositionComponent
import ua.leonidius.coldline.entity.components.ScoreComponent
import ua.leonidius.coldline.entity.systems.*
import ua.leonidius.coldline.level.generation.LevelGenerator
import ua.leonidius.coldline.renderer.MapWithObjectsRenderer
import ua.leonidius.coldline.renderer.PathRenderer


class GameScreen(private val game: Main) : Screen {

    companion object {
        lateinit var instance: GameScreen
    }

    init {
        instance = this
    }

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
        zoom = 0.3F
    }

    private val guiCamera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    // TODO: private val assetManager = AssetManager()

    // TODO: create tile set loader
    private val tileSet = TmxMapLoader().load("maps/level2.tmx").tileSets.getTileSet(0)
    private val levelGenerator = LevelGenerator(tileSet)
    val level = levelGenerator.generate()
    private val renderer = MapWithObjectsRenderer(level, 1F)

    private val keyboardController = KeyboardController(this)

    private var exitTileX = levelGenerator.doorTileX
    private var exitTileY = levelGenerator.doorTileY

    val pathRenderer = PathRenderer(camera, level)
    var lastPathAlgorithm: String? = null
    var lastPathComputeTime = -1.0

    val engine = PooledEngine().apply {
        addSystem(RenderingSystem(renderer.batch, camera))
        addSystem(PlayerControlSystem(keyboardController))
        addSystem(MovementSystem())
        addSystem(WallCollisionSystem(level.collisionLayer))
        addSystem(EntityCollisionSystem())
        addSystem(DoorSystem(::gameOver))
        addSystem(PathHighlightingSystem(pathRenderer))
        addSystem(DeathSystem())
        addSystem(EnemyHarmSystem())
        addSystem(ChestCollectionSystem())
    }

    private lateinit var playerPosition: PositionComponent
    private lateinit var playerHealth: HealthComponent
    private lateinit var playerScore: ScoreComponent

    override fun show() {
        Gdx.input.inputProcessor = keyboardController

        with (level.objectLayer.objects.get("spawnPoint") as RectangleMapObject) {
            val player = createPlayer(rectangle.x, rectangle.y)
            playerPosition = player.getComponent(PositionComponent::class.java)
            playerHealth = player.getComponent(HealthComponent::class.java)
            playerScore = player.getComponent(ScoreComponent::class.java)
        }

        // TODO: similar to addCHestEntities()
        createDoor(exitTileX, exitTileY)

        addChestEntities()

        engine.addSystem(CombatActivatorSystem(playerPosition))
        engine.addSystem(EnemyCombatSystem(playerPosition, level))

        addEnemies()
    }

    private fun addChestEntities() {
        level.objectLayer.objects
            .filter { it.properties.get("tag") == "chest" }
            .forEach {
                val chest = it as TiledMapTileMapObject
                createChest(chest.x, chest.y)
            }
    }

    private fun addEnemies() {
        repeat(5) {
            val x = MathUtils.random(1, levelGenerator.width - 1)
            val y = MathUtils.random(1, levelGenerator.height - 1)
            if (level.collisionLayer.getCell(x, y).tile == levelGenerator.floorTile) {
                createEnemy(x, y, true) // dumb enemies
            }
        }
        repeat(5) {
            val x = MathUtils.random(1, levelGenerator.width - 1)
            val y = MathUtils.random(1, levelGenerator.height - 1)
            if (level.collisionLayer.getCell(x, y).tile == levelGenerator.floorTile) {
                createEnemy(x, y, false) // smart enemies
            }
        }
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
    }

    fun switchPathAlgo() {
        val data = pathRenderer.switchPathAlgorithm()
        lastPathAlgorithm = data.first
        lastPathComputeTime = data.second
    }

    fun switchPathHighlightingMode() {
        pathRenderer.displayWholePath = !pathRenderer.displayWholePath
    }

    private fun mapToTileCoordinate(coordinate: Float) =
        (coordinate / (level.collisionLayer.tileWidth)).toInt()

    fun tileToMapCoordinate(coordinate: Int) =
        (coordinate * level.collisionLayer.tileWidth).toFloat()

    private fun printDebugData(batch: Batch) {
        printPathComputeTime(batch)
        printPlayerLocation(batch)
        printDoorLocation(batch)
        printPlayerStats(batch)
    }

    private fun printPathComputeTime(batch: Batch) {
        pathRenderer.let {
            if (lastPathAlgorithm != null) {
                game.bitmapFont.draw(
                    batch,
                    "${lastPathAlgorithm}: ${lastPathComputeTime}ms",
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
            "x = ${playerPosition.tileX}, y = ${playerPosition.tileY}",
            0F, 50F
        )
    }

    private fun printPlayerStats(batch: Batch) {
        game.bitmapFont.draw(
            batch,
            "${playerHealth.health} HP",
            0F, 110F
        )
        game.bitmapFont.draw(
            batch,
            "Traversed ${playerScore.distanceTraversed.toInt()}tl, collected ${playerScore.chestsCollected} chests",
            0F, 80F
        )
    }

    fun gameOver() {
        val score = if (playerHealth.health <= 0) 0 else  1000 / playerScore.distanceTraversed + playerScore.chestsCollected * 20 + playerHealth.health
        game.toMenuScreen(score.toInt())
    }

}