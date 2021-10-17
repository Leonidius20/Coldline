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
import ua.leonidius.coldline.pathfinding.algorithms.aStarWithChests.aStarWithChests
import ua.leonidius.coldline.pathfinding.graph_generators.generateGraphWithChests
import ua.leonidius.coldline.pathfinding.graph_generators.generateGraphWithTiles
import ua.leonidius.coldline.timing.measureTime

class PathRenderer(private val camera: OrthographicCamera,
                   collisionLayer: TiledMapTileLayer,
                   objectLayer: MapLayer, floorTile: TiledMapTile) {

    enum class PathAlgorithmTypes(val id: Int, val color: Color) {
        NONE(0, Color.CLEAR),
        //BFS(1, Color.YELLOW),
        //DFS(2, Color.RED),
        //UCS(3, Color.GREEN),
        A_STAR_WITH_CHESTS_EUCLIDIAN(1, Color.BROWN),
        A_STAR_WITH_CHESTS_MANHATTAN(2, Color.BROWN),
        A_STAR_WITH_CHESTS_GREEDY(3, Color.BROWN),
        A_STAR_EUCLIDIAN(4, Color.TEAL),
        A_STAR_MANHATTAN(5, Color.RED),
        A_STAR_GREEDY(6, Color.YELLOW),

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

    var displayWholePath = true
    var currentDestinationIndex = 1 // index of current node in path that is the destination

    lateinit var path: List<GraphNode>

    var lastUsedAlgorithm = ""
    var lastComputeTime = -1.0

    fun render() {
        if (currentPathAlgorithm != PathAlgorithmTypes.NONE) {
            shapeRenderer.projectionMatrix = camera.combined
            Gdx.gl.glLineWidth(10F)

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = currentPathAlgorithm.color

            if (displayWholePath) {

                var counter = 0

                val allVertices = mutableListOf<Float>()
                path.forEach { node ->
                    allVertices.add(node.mapX + 16 / 2) // adding 16 / 2 to center it in a cell
                    allVertices.add(node.mapY + 16 / 2 + counter++)
                    if (counter == 7) counter = 0
                }
                val pathPolyline = Polyline(allVertices.toFloatArray())

                shapeRenderer.polyline(pathPolyline.transformedVertices)
            } else {
                if (currentDestinationIndex != path.size) {
                    val destination = path[currentDestinationIndex]
                    val start = path[currentDestinationIndex - 1]
                    val vertices = floatArrayOf(start.mapX + 16 / 2, start.mapY + 16 / 2,
                        destination.mapX + 16 / 2, destination.mapY + 16 / 2)
                    val pathPolyline = Polyline(vertices)
                    shapeRenderer.polyline(pathPolyline.transformedVertices)
                }
            }

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
                /*PathAlgorithmTypes.DFS -> {
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
                }*/
                PathAlgorithmTypes.A_STAR_EUCLIDIAN -> {
                    timeElapsed = measureTime {
                        path = aStar(graph, nodeStart, nodeEnd, ::euclidianHeuristic)
                    }
                }
                PathAlgorithmTypes.A_STAR_MANHATTAN -> {
                    timeElapsed = measureTime {
                        path = aStar(graph, nodeStart, nodeEnd, ::manhattanHeuristic)
                    }
                }
                PathAlgorithmTypes.A_STAR_GREEDY -> {
                    timeElapsed = measureTime {
                        path = aStar(graph, nodeStart, nodeEnd, ::greedyHeuristic)
                    }
                }
                PathAlgorithmTypes.A_STAR_WITH_CHESTS_EUCLIDIAN -> {
                    timeElapsed = measureTime {
                        path = aStarWithChests(chestGraph, chestGraphStart, chestGraphEnd, graph, ::euclidianHeuristic)
                        // path = nearestNeighborTsp(chestGraph, chestGraphStart, chestGraphEnd)
                    }
                }
                PathAlgorithmTypes.A_STAR_WITH_CHESTS_MANHATTAN -> {
                    timeElapsed = measureTime {
                        path = aStarWithChests(chestGraph, chestGraphStart, chestGraphEnd, graph, ::manhattanHeuristic)
                    }
                }
                PathAlgorithmTypes.A_STAR_WITH_CHESTS_GREEDY -> {
                    timeElapsed = measureTime {
                        path = aStarWithChests(chestGraph, chestGraphStart, chestGraphEnd, graph, ::greedyHeuristic)
                    }
                }
                else -> {
                    path = dfs(graph, nodeStart, nodeEnd)!!
                }
            }

            lastUsedAlgorithm = newPathAlgorithm.name
            lastComputeTime = timeElapsed

            currentDestinationIndex = 1
        } else {
            lastUsedAlgorithm = ""
        }

        currentPathAlgorithm = newPathAlgorithm
    }

}