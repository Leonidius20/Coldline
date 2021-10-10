package ua.leonidius.coldline.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Polyline
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.*
import ua.leonidius.coldline.pathfinding.graph_generators.generateGraphWithChests
import ua.leonidius.coldline.pathfinding.graph_generators.generateGraphWithTiles
import ua.leonidius.coldline.timing.measureTime

class PathRenderer(private val camera: OrthographicCamera,
                   collisionLayer: TiledMapTileLayer,
                   objectLayer: MapLayer, floorTile: TiledMapTile) {

    enum class PathAlgorithmTypes(val id: Int, val color: Color) {
        NONE(0, Color.CLEAR),
        BFS(1, Color.YELLOW),
        DFS(2, Color.RED),
        UCS(3, Color.GREEN),
        A_STAR_DIST(4, Color.TEAL),
    }

    private val shapeRenderer = ShapeRenderer()

    private val _trio = generateGraphWithTiles(collisionLayer, objectLayer, floorTile)
    val graph = _trio.first
    private val startNode = _trio.second
    private val endNode = _trio.third

    private val _chestTriple = generateGraphWithChests(objectLayer)
    private val chestGraph = _chestTriple.first
    private val chestGraphStart = _chestTriple.second
    private val chestGraphEnd = _chestTriple.third

    private var currentPathAlgorithm = PathAlgorithmTypes.NONE

    private lateinit var path: List<GraphNode>

    var lastUsedAlgorithm = ""
    var lastComputeTime = -1.0

    fun render() {
        if (currentPathAlgorithm != PathAlgorithmTypes.NONE) {
            shapeRenderer.projectionMatrix = camera.combined
            Gdx.gl.glLineWidth(10F)

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            val allVertices = mutableListOf<Float>()
            path.forEach { node ->
                allVertices.add(node.mapX + 16 / 2) // adding 16 / 2 to center it in a cell
                allVertices.add(node.mapY + 16 / 2)
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
            val nodeStart = startNode
            val nodeEnd = endNode

            var timeElapsed = -1.0

            when(newPathAlgorithm) {
                PathAlgorithmTypes.DFS -> {
                    timeElapsed = measureTime {
                        path = dfs(graph, nodeStart, nodeEnd)!!
                    }
                }
                PathAlgorithmTypes.BFS -> {
                    timeElapsed = measureTime {
                        path = bfs(graph, nodeStart, nodeEnd)!!
                    }
                }
                PathAlgorithmTypes.UCS -> {
                    timeElapsed = measureTime {
                        path = uniformCostSearch(graph, nodeStart, nodeEnd)
                    }
                }
                PathAlgorithmTypes.A_STAR_DIST -> {
                    timeElapsed = measureTime {
                        path = aStar(graph, nodeStart, nodeEnd, ::distanceHeuristic)
                    }
                }
                else -> {
                    path = dfs(graph, nodeStart, nodeEnd)!!
                }
            }

            lastUsedAlgorithm = newPathAlgorithm.name
            lastComputeTime = timeElapsed
        } else {
            lastUsedAlgorithm = ""
        }

        currentPathAlgorithm = newPathAlgorithm
    }

}