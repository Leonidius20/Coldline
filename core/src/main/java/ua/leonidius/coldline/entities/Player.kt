package ua.leonidius.coldline.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2

class Player(sprite: Sprite, val collisionLayer: TiledMapTileLayer): Sprite(sprite) {

    private val velocity = Vector2()
    private val speed = 60 * 2

    override fun draw(batch: Batch?) {
        update(Gdx.graphics.deltaTime)
        super.draw(batch)
    }

    private fun update(delta: Float) {
        //x += velocity.x * delta
        //y += velocity.y * delta

        val newX = velocity.x * delta
        val newY = velocity.y * delta


        if (velocity.x < 0) { // left

            if (isTileWithCollisionAt(newX, y + height) // using old Y here so avoid going through corners diagonally
                || isTileWithCollisionAt(newX, y / 2)
                || isTileWithCollisionAt(newX, y)) {
                // move i guess
            }
        } else if (velocity.x > 0) { // right
            if (isTileWithCollisionAt(newX + width, y + height)
                || isTileWithCollisionAt(newX + width, y / 2)
                || isTileWithCollisionAt(newX + width, y)) {
                // move
            }
        }

        if (velocity.y < 0) { // down (or up)

        } else if (velocity.y > 0) { // up

        }
    }

    private fun isTileWithCollisionAt(mapX: Float, mapY: Float) =
        collisionLayer.run {
            getCell((mapX / tileWidth).toInt(), (mapY / tileHeight).toInt())
                .tile.properties.containsKey("blocked")
        }

}