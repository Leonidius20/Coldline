package ua.leonidius.coldline.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.utils.TimeUtils
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.bfs
import ua.leonidius.coldline.pathfinding.algorithms.dfs
import ua.leonidius.coldline.pathfinding.algorithms.uniformCostSearch
import ua.leonidius.coldline.screens.game.GameScreen
import java.math.BigInteger

class PathRenderer(private val gameScreen: GameScreen,
                   private val camera: OrthographicCamera,
                   objectLayer: MapLayer) {

    enum class PathAlgorithmTypes(val id: Int, val color: Color) {
        NONE(0, Color.CLEAR),
        BFS(1, Color.YELLOW),
        DFS(2, Color.RED),
        UCS(3, Color.GREEN)
    }

    private val shapeRenderer = ShapeRenderer()

    private val graph = Graph(objectLayer)

    private var currentPathAlgorithm = PathAlgorithmTypes.NONE

    private lateinit var path: GraphPath<GraphNode>
    private var playerXWhenPathWasBuilt = -1F
    private var playerYWhenPathWasBuilt = -1F

    var lastUsedAlgorithm = ""
    var lastComputeTime = -1.0

    fun render() {
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
            // shapeRenderer.line(playerXWhenPathWasBuilt, playerYWhenPathWasBuilt, startX, startY)
            shapeRenderer.end()
        }
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
                lastUsedAlgorithm = newPathAlgorithm.name
                lastComputeTime = timeElapsed

            }

            playerXWhenPathWasBuilt = gameScreen.getPlayerX()
            playerYWhenPathWasBuilt = gameScreen.getPlayerY()
        } else {
            lastUsedAlgorithm = ""
        }

        currentPathAlgorithm = newPathAlgorithm
    }

}