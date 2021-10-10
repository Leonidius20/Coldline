package ua.leonidius.coldline.pathfinding

// TODO: decouple physical objects line polylines from graph logic
class Graph {

    private lateinit var adjacencyLists: MutableMap<GraphNode, MutableList<GraphNode>>
    val nodes = emptyList<GraphNode>().toMutableList()
    val connections = emptyList<GraphConnection>().toMutableList()

    fun addNode(node: GraphNode) {
        nodes.add(node)
    }

    fun addConnection(from: GraphNode, to: GraphNode, weight: Float = 1F) {
        connections.add(GraphConnection(from, to, weight))
    }

    fun findNode(predicate: (GraphNode) -> Boolean): GraphNode? {
        return nodes.find(predicate)
    }

    fun generateAdjacencyLists() {
        adjacencyLists = nodes.associateWith { emptyList<GraphNode>().toMutableList() }.toMutableMap().apply {
            connections.forEach {
                this[it.fromNode]!!.add(it.toNode)
                this[it.toNode]!!.add(it.fromNode)
            }
        }
    }

    // fun getNodeById(id: Int) = nodes.find { it.index == id }

    fun getConnectionBetween(startNode: GraphNode, endNode: GraphNode) =
        connections.find {
            (it.fromNode == startNode && it.toNode == endNode)
                    || (it.fromNode == endNode && it.toNode == startNode)
        }

    /*override fun getConnections(fromNode: GraphNode): Array<Connection<GraphNode>> =
        Array(connections.filter {
            it.polylineObj.properties["startNode"] == fromNode.getIndex()
        }.toTypedArray())*/

    /*fun findNearestNodeTo(mapX: Float, mapY: Float) = nodes.minByOrNull {
        val score = Vector2.dst(it.getX(), it.getY(), mapX, mapY)
        // add punishment for going through walls
        score
    }*/

    /*fun findNodeAt(x: Int, y: Int): GraphNode? {
        return nodes.find { it.tileX == x && it.tileY == y }
    }*/

    fun getAdjacentNodes(node: GraphNode) = /*connections
        .filter { it.fromNode == node || it.toNode == node }
        .map {
            if (it.fromNode == node) it.toNode else it.fromNode
        }*/ adjacencyLists[node]!!

    fun getEdgeWeight(start: GraphNode, end: GraphNode): Float {
        return getConnectionBetween(start, end)!!.getWeight()
    }

}