package ua.leonidius.coldline.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.ScreenUtils
import ua.leonidius.coldline.Main
import ua.leonidius.coldline.entities.Player
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.bfs
import ua.leonidius.coldline.pathfinding.algorithms.dfs
import ua.leonidius.coldline.pathfinding.algorithms.uniformCostSearch
import ua.leonidius.coldline.renderer.MapWithObjectsRenderer

class GameScreen(private val game: Main) : Screen {

    enum class PathAlgorithmTypes(val id: Int, val color: Color) {
        NONE(0, Color.CLEAR),
        DFS(1, Color.RED),
        BFS(2, Color.YELLOW),
        UCS(3, Color.GREEN)
    }

    private val camera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
        zoom = 0.3F
    }

    private val guiCamera = OrthographicCamera().apply {
        setToOrtho(false, 800F, 480F)
    }

    private val scale = 1F

    private val map = TmxMapLoader().load("maps/level2.tmx")
    private val renderer = MapWithObjectsRenderer(map, scale)

    private val shapeRenderer = ShapeRenderer()

    private val player = Player(
        Sprite(Texture("player.png")).apply { setScale(scale) },
        map.layers[0] as TiledMapTileLayer, this
    ).apply { moveToTile(45, 6) }

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
        Gdx.input.inputProcessor = player
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
                player.draw(this)

                projectionMatrix = guiCamera.combined
                game.bitmapFont.draw(
                    this,
                    debugInfoToRender,
                    0F, 80F
                )
                game.bitmapFont.draw(
                    this,
                    "x = ${player.getTileX()}, y = ${player.getTileY()}",
                    0F, 50F
                )
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

        camera.position.set(player.x, player.y, 0F)
        camera.update()

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
            path = with(graph) {
                // val nodeStart = findNearestNodeTo(player.x, player.y)
                val nodeStart = getNodeById(0)!!
                val nodeEnd = getNodeById(15)!!

                when(newPathAlgorithm) {
                    PathAlgorithmTypes.DFS -> {
                        debugInfoToRender = "DFS"
                        dfs(graph, nodeStart, nodeEnd)!!
                    }
                    PathAlgorithmTypes.BFS -> {
                        debugInfoToRender = "BFS"
                        bfs(graph, nodeStart, nodeEnd)!!
                    }
                    PathAlgorithmTypes.UCS -> {
                        debugInfoToRender = "UCS"
                        uniformCostSearch(graph, nodeStart, nodeEnd)
                    }
                    else -> {
                        debugInfoToRender = "Algo out of bounds"
                        dfs(graph, nodeStart, nodeEnd)!!
                    }
                }
                // findPath(nodeStart!!, getNodeById(15)!!)
            }

            // playerXWhenPathWasBuilt = player.x
            // playerYWhenPathWasBuilt = player.y
        } else {
            debugInfoToRender = ""
        }

        currentPathAlgorithm = newPathAlgorithm
    }

}