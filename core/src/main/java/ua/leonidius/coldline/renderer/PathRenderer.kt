package ua.leonidius.coldline.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Polyline
import com.badlogic.gdx.utils.TimeUtils
import ua.leonidius.coldline.pathfinding.Graph
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.bfs
import ua.leonidius.coldline.pathfinding.algorithms.dfs
import ua.leonidius.coldline.pathfinding.algorithms.uniformCostSearch
import ua.leonidius.coldline.screens.game.GameScreen
import java.math.BigInteger

class PathRenderer(private val camera: OrthographicCamera,
                   collisionLayer: TiledMapTileLayer,
                   objectLayer: MapLayer, floorTile: TiledMapTile) {

    enum class PathAlgorithmTypes(val id: Int, val color: Color) {
        NONE(0, Color.CLEAR),
        BFS(1, Color.YELLOW),
        DFS(2, Color.RED),
        UCS(3, Color.GREEN)
    }

    private val shapeRenderer = ShapeRenderer()

    val graph = Graph(collisionLayer, objectLayer, floorTile)

    private var currentPathAlgorithm = PathAlgorithmTypes.NONE

    private lateinit var path: GraphPath<GraphNode>

    var lastUsedAlgorithm = ""
    var lastComputeTime = -1.0

    fun render() {
        if (currentPathAlgorithm != PathAlgorithmTypes.NONE) {
            shapeRenderer.projectionMatrix = camera.combined
            Gdx.gl.glLineWidth(10F)

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            val allVertices = mutableListOf<Float>()
            path.forEachIndexed { index, graphNodeObject ->
                allVertices.add(graphNodeObject.tileX * 16F + 16 / 2)
                allVertices.add(graphNodeObject.tileY * 16F + 16 / 2)
            }
            val pathPolyline = Polyline(allVertices.toFloatArray())
            shapeRenderer.color = currentPathAlgorithm.color
            shapeRenderer.polyline(pathPolyline.transformedVertices)

            shapeRenderer.end()
        }
    }

    fun switchPathAlgorithm() {
        val newPathAlgorithm =  PathAlgorithmTypes.values()[(currentPathAlgorithm.id + 1) % PathAlgorithmTypes.values().size]

        if (newPathAlgorithm != PathAlgorithmTypes.NONE) {
            val nodeStart = graph.startNode
            val nodeEnd = graph.endNode

            var timeBefore = BigInteger.ZERO
            var timeAfter = BigInteger.ZERO

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
        } else {
            lastUsedAlgorithm = ""
        }

        currentPathAlgorithm = newPathAlgorithm
    }

}