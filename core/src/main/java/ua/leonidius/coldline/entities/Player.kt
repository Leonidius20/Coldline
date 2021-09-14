package ua.leonidius.coldline.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.screens.GameScreen

class Player(sprite: Sprite,
             private val collisionLayer: TiledMapTileLayer, val gameScreen: GameScreen):
    Sprite(sprite), InputProcessor {

    private val velocity = Vector2()
    private val speed = 60 * 2F

    private val scale = sprite.scaleX

    override fun draw(batch: Batch?) {
        update(Gdx.graphics.deltaTime)
        super.draw(batch)
    }

    private fun update(delta: Float) {
        val newX = x + velocity.x * delta
        val newY = y + velocity.y * delta

        val collidesOnX =
            if (velocity.x < 0)
                /*isTileWithCollisionAt(newX, y + height) // using old Y here so avoid going through corners diagonally
                        || isTileWithCollisionAt(newX, y / 2)
                        ||*/ isTileWithCollisionAt(newX, y)
            else if (velocity.x > 0)
                /*isTileWithCollisionAt(newX + width, y + height)
                        || isTileWithCollisionAt(newX + width, y / 2)
                        ||*/ isTileWithCollisionAt(newX + width, y)
            else false

        if (!collidesOnX) x = newX else velocity.x = 0F

        val collidesOnY =
            if (velocity.y < 0)  // down (or up)
                isTileWithCollisionAt(x, newY)
                        /*|| isTileWithCollisionAt(x + width / 2, newY)
                        || isTileWithCollisionAt(x + width, newY)*/
            else if (velocity.y > 0)
                isTileWithCollisionAt(x, newY + height)
                        /*|| isTileWithCollisionAt(x + width / 2, newY + height)
                        || isTileWithCollisionAt(x + width, newY + height)*/
            else false

        if (!collidesOnY) y = newY else velocity.y = 0F
    }

    private fun isTileWithCollisionAt(mapX: Float, mapY: Float) =
        collisionLayer.run {
            val cell = getCell((mapX / (tileWidth * scale)).toInt(), (mapY / (tileHeight * scale)).toInt())
            cell?.tile?.properties?.containsKey("blocked") ?: true
        }

    /**
     * Set player's position in the world using tile coordinates
     * @param x tile's x coordinate
     * @param y tile's y coordinate
     */
    fun moveToTile(x: Int, y: Int) {
        setPosition((x * collisionLayer.tileWidth * scale).toFloat(),
            (y * collisionLayer.tileHeight * scale).toFloat())
    }

    fun getTileX() = mapToTileX(x)

    fun getTileY() = mapToTileY(y)

    override fun keyDown(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> velocity.y = speed
            Input.Keys.A -> velocity.x = -speed
            Input.Keys.S -> velocity.y = -speed
            Input.Keys.D -> velocity.x = speed
            Input.Keys.Z -> gameScreen.switchPathAlgorithm()
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when(keycode) {
            Input.Keys.W -> velocity.y = 0F
            Input.Keys.A -> velocity.x = 0F
            Input.Keys.S -> velocity.y = 0F
            Input.Keys.D -> velocity.x = 0F
        }
        return true
    }

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false

    /**
     * Convert an X coordinate on the map to a tile X coordinate
     */
    fun mapToTileX(mapX: Float) = (mapX / (collisionLayer.tileWidth * scale)).toInt()

    fun mapToTileY(mapY: Float) = (mapY / (collisionLayer.tileHeight * scale)).toInt()

}