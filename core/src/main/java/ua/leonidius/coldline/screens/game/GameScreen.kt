package ua.leonidius.coldline.screens.game

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.TimeUtils
import ua.leonidius.coldline.Main
import ua.leonidius.coldline.controller.KeyboardController
import ua.leonidius.coldline.entity.Player
import ua.leonidius.coldline.entity.systems.MovementSystem
import ua.leonidius.coldline.entity.systems.PlayerControlSystem
import ua.leonidius.coldline.entity.systems.RenderingSystem
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.bfs
import ua.leonidius.coldline.pathfinding.algorithms.dfs
import ua.leonidius.coldline.pathfinding.algorithms.uniformCostSearch
import ua.leonidius.coldline.renderer.MapWithObjectsRenderer
import java.math.BigInteger


class GameScreen(private val game: Main) : Screen {

    enum class PathAlgorithmTypes(val id: Int, val color: Color) {
        NONE(0, Color.CLEAR),
        BFS(1, Color.YELLOW),
        DFS(2, Color.RED),
        UCS(3, Color.GREEN)
    }

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
        zoom = 0.3F
    }

    private val guiCamera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    private val map = TmxMapLoader().load("maps/level2.tmx")
    private val renderer = MapWithObjectsRenderer(map, 1F)

    private val keyboardController = KeyboardController()

    val engine = PooledEngine().apply {
        addSystem(RenderingSystem(renderer.batch, camera))
        addSystem(PlayerControlSystem(keyboardController))
        addSystem(MovementSystem())
        // collision
    }

    private val shapeRenderer = ShapeRenderer()

    private val collisionLayer = map.layers.get("collision") as TiledMapTileLayer

    val player = Player(collisionLayer, this)
        .apply { moveToTile(45, 6) }

    private val objectLayer = map.layers["objects"]
    private val graph = Graph(objectLayer)

    private var exitTileX = 45
    private var exitTileY = 45

    // for path rendering
    private var currentPathAlgorithm = PathAlgorithmTypes.NONE

    private lateinit var path: GraphPath<GraphNode>
    private var playerXWhenPathWasBuilt = player.x
    private var playerYWhenPathWasBuilt = player.y

    private var debugInfoToRender = ""

    override fun show() {
        Gdx.input.inputProcessor = keyboardController

        createEnemy(30, 12)
        createEnemy(30, 24)
        createEnemy(40, 30)
        createEnemy(38, 19)
        createEnemy(16, 45)
        createEnemy(27, 45)

        createPlayer(45, 6)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0F, 0F, 0.2F, 1F)

        // rendering map and player
        renderer.run {
            setView(camera)
            render()
            batch.run {
                projectionMatrix = camera.combined
                begin()
                // player.draw(this)

                projectionMatrix = guiCamera.combined
                game.bitmapFont.draw(
                    this,
                    debugInfoToRender,
                    0F, 80F
                )
                /*game.bitmapFont.draw(
                    this,
                    "x = ${player.getTileX()}, y = ${player.getTileY()}",
                    0F, 50F
                )*/
                game.bitmapFont.draw(
                    this,
                    "doorX = $exitTileX, doorY = $exitTileY",
                    0F, 20F
                )

                end()
            }
        }

        // rendering path
        if (currentPathAlgorithm != PathAlgorithmTypes.NONE) {
            shapeRenderer.projectionMatrix = camera.combined
            Gdx.gl.glLineWidth(10F)
            // shapeRenderer.scale(scale, scale, 1F)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            var startX = 0F
            var startY = 0F
            path.forEachIndexed { index, graphNodeObject ->
                if (index != 0) {
                    val startNode = path[index - 1]
                    graph.getConnectionBetween(startNode, graphNodeObject)!!.render(shapeRenderer, currentPathAlgorithm.color)
                } else {
                    with(graphNodeObject.rectMapObj.rectangle) { startX = x; startY = y }
                }
            }
            shapeRenderer.line(playerXWhenPathWasBuilt, playerYWhenPathWasBuilt, startX, startY)
            shapeRenderer.end()
        }

        engine.update(delta)

        /*camera.position.set(player.x, player.y, 0F)
        camera.update()*/

        // checking if it's the exit
        if (player.getTileX() == exitTileX && player.getTileY() == exitTileY) {
            game.toMenuScreen()
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

    fun switchPathAlgorithm() {
        val newPathAlgorithm =  PathAlgorithmTypes.values()[(currentPathAlgorithm.id + 1) % PathAlgorithmTypes.values().size]

        if (newPathAlgorithm != PathAlgorithmTypes.NONE) {
            with(graph) {
                // val nodeStart = findNearestNodeTo(player.x, player.y)
                val nodeStart = getNodeById(0)!!
                val nodeEnd = getNodeById(15)!!

                var timeBefore = BigInteger.ZERO
                var timeAfter = BigInteger.ZERO

                // path = DefaultGraphPath<GraphNode>() // attempt to fool optimizations

                when(newPathAlgorithm) {
                    PathAlgorithmTypes.DFS -> {
                        timeBefore = TimeUtils.nanoTime().toBigInteger()
                        path = dfs(graph, nodeStart, nodeEnd)!!
                        timeAfter = TimeUtils.nanoTime().toBigInteger()
                    }
                    PathAlgorithmTypes.BFS -> {
                        timeBefore = TimeUtils.nanoTime().toBigInteger()
                        path = bfs(graph, nodeStart, nodeEnd)!!
                        timeAfter = TimeUtils.nanoTime().toBigInteger()
                    }
                    PathAlgorithmTypes.UCS -> {
                        timeBefore = TimeUtils.nanoTime().toBigInteger()
                        path = uniformCostSearch(graph, nodeStart, nodeEnd)
                        timeAfter = TimeUtils.nanoTime().toBigInteger()
                    }
                    else -> {
                        path = dfs(graph, nodeStart, nodeEnd)!!
                    }
                }
                val timeElapsed = (timeAfter - timeBefore).toDouble() / 1000000.0
                debugInfoToRender = "${newPathAlgorithm.name}: ${timeElapsed}ms"

            }

            // playerXWhenPathWasBuilt = player.x
            // playerYWhenPathWasBuilt = player.y
        } else {
            debugInfoToRender = ""
        }

        currentPathAlgorithm = newPathAlgorithm
    }

    fun mapToTileCoordinate(coordinate: Float) =
        (coordinate / (collisionLayer.tileWidth)).toInt()

    fun tileToMapCoordinate(coordinate: Int) =
        (coordinate * collisionLayer.tileWidth).toFloat()

}