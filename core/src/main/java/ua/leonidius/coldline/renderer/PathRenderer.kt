package ua.leonidius.coldline.renderer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Polyline
import ua.leonidius.coldline.level.Level
import ua.leonidius.coldline.pathfinding.GraphNode
import ua.leonidius.coldline.pathfinding.algorithms.AStarStrategy
import ua.leonidius.coldline.pathfinding.algorithms.aStarWithChests.AStarTSPStrategy
import ua.leonidius.coldline.pathfinding.algorithms.heuristics.EuclideanHeuristic
import ua.leonidius.coldline.pathfinding.algorithms.heuristics.GreedyHeuristic
import ua.leonidius.coldline.pathfinding.algorithms.heuristics.ManhattanHeuristic
import ua.leonidius.coldline.timing.measureTime

class PathRenderer(private val camera: OrthographicCamera,
                   private val level: Level) {

    private val shapeRenderer = ShapeRenderer()

    /**
     * Whether to display the complete path or pointers to the next node
     * while the player is walking along the path
     */
    var displayWholePath = true
    var currentDestinationIndex = 1 // index of current node in path that is the destination

    lateinit var path: List<GraphNode>

    private val algorithms = arrayOf(
        Triple("None", Color.CLEAR, null),
        Triple("A* with chests/Euclidean", Color.BROWN, AStarTSPStrategy(level.chestGraph, level.spawnChestGraphNode, level.doorChestGraphNode, EuclideanHeuristic())),
        Triple("A* with chests/Manhattan", Color.BROWN, AStarTSPStrategy(level.chestGraph, level.spawnChestGraphNode, level.doorChestGraphNode, ManhattanHeuristic())),
        Triple("A* with chests/Greedy", Color.BROWN, AStarTSPStrategy(level.chestGraph, level.spawnChestGraphNode, level.doorChestGraphNode, GreedyHeuristic())),
        Triple("A*/Euclidean", Color.TEAL, AStarStrategy(EuclideanHeuristic())),
        Triple("A*/Manhattan", Color.RED, AStarStrategy(ManhattanHeuristic())),
        Triple("A*/Greedy", Color.YELLOW, AStarStrategy(GreedyHeuristic())),
    )

    private var currentAlgorithmIndex = 0

    fun render() {
        if (currentAlgorithmIndex != 0) { // 0 = no algorithm
            shapeRenderer.projectionMatrix = camera.combined
            Gdx.gl.glLineWidth(10F)

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            shapeRenderer.color = algorithms[currentAlgorithmIndex].second

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

    /**
     * @return a pair of the name of the algorithm used and time it took to find a way in ms
     */
    fun switchPathAlgorithm(): Pair<String?, Double> {
        currentAlgorithmIndex = (currentAlgorithmIndex + 1) % algorithms.size

        if (currentAlgorithmIndex != 0) { // 0 = no algorithm

            val computeTime = measureTime {
                path = algorithms[currentAlgorithmIndex].third!!
                    .findPath(level.tileGraph, level.spawnTileGraphNode, level.doorTileGraphNode)
            }

            currentDestinationIndex = 1

            val algorithmName = algorithms[currentAlgorithmIndex].first
            return Pair(algorithmName, computeTime)
        }

        return Pair(null, -1.0)
    }

}