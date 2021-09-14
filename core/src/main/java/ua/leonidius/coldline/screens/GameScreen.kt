package ua.leonidius.coldline.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
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
import ua.leonidius.coldline.renderer.MapWithObjectsRenderer

class GameScreen(private val game: Main) : Screen {

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

    private val player = Player(Sprite(Texture("player.png")).apply { setScale(scale) },
        map.layers[0] as TiledMapTileLayer)

    private val objectLayer = map.layers["objects"]
    private val graph = Graph(objectLayer)

    private var exitTileX = 45
    private var exitTileY = 45

    private val path = with(graph) {
        findPath(getNodeById(0)!!, getNodeById(15)!!)
    }

    override fun show() {
        player.moveToTile(45, 6)
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
                game.bitmapFont.draw(this,
                    "x = ${player.getTileX()}, y = ${player.getTileY()}",
                    0F, 50F)
                game.bitmapFont.draw(this,
                    "doorX = $exitTileX, doorY = $exitTileY",
                    0F, 20F)

                end()
            }
        }

        // rendering path
        shapeRenderer.projectionMatrix = camera.combined
        Gdx.gl.glLineWidth(10F)
        // shapeRenderer.scale(scale, scale, 1F)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        path.forEachIndexed { index, graphNodeObject ->
            if (index != 0) {
                val startNode = path[index - 1]
                graph.getConnectionBetween(startNode, graphNodeObject)!!.render(shapeRenderer)
            }
        }
        shapeRenderer.end()

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

}