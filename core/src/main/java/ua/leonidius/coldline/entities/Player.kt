package ua.leonidius.coldline.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2

class Player(sprite: Sprite, private val collisionLayer: TiledMapTileLayer): Sprite(sprite) {

    private val velocity = Vector2()
    private val speed = 60 * 2

    override fun draw(batch: Batch?) {
        update(Gdx.graphics.deltaTime)
        super.draw(batch)
    }

    private fun update(delta: Float) {
        val newX = x + velocity.x * delta
        val newY = y + velocity.y * delta

        val collidesOnX =
            if (velocity.x < 0)
                isTileWithCollisionAt(newX, y + height) // using old Y here so avoid going through corners diagonally
                        || isTileWithCollisionAt(newX, y / 2)
                        || isTileWithCollisionAt(newX, y)
            else if (velocity.x > 0)
                isTileWithCollisionAt(newX + width, y + height)
                        || isTileWithCollisionAt(newX + width, y / 2)
                        || isTileWithCollisionAt(newX + width, y)
            else false

        if (!collidesOnX) x = newX else velocity.x = 0F

        val collidesOnY =
            if (velocity.y < 0)  // down (or up)
                isTileWithCollisionAt(x, newY)
                        || isTileWithCollisionAt(x + width / 2, newY)
                        || isTileWithCollisionAt(x + width, newY)
            else if (velocity.y > 0)
                isTileWithCollisionAt(x, newY + height)
                        || isTileWithCollisionAt(x + width / 2, newY + height)
                        || isTileWithCollisionAt(x + width, newY + height)
            else false

        if (!collidesOnY) y = newY else velocity.y = 0F
    }

    private fun isTileWithCollisionAt(mapX: Float, mapY: Float) =
        collisionLayer.run {
            getCell((mapX / tileWidth).toInt(), (mapY / tileHeight).toInt())
                .tile.properties.containsKey("blocked")
        }

    /**
     * Set player's position in the world using tile coordinates
     * @param x tile's x coordinate
     * @param y tile's y coordinate
     */
    fun moveToTile(x: Int, y: Int) {
        setPosition((x * collisionLayer.tileWidth).toFloat(),
            (y * collisionLayer.tileHeight).toFloat())
    }

}