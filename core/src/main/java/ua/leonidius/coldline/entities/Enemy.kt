package ua.leonidius.coldline.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import ua.leonidius.coldline.screens.GameScreen

class Enemy(private val collisionLayer: TiledMapTileLayer, val gameScreen: GameScreen)
    : Sprite(Sprite(Texture("enemy.png")
)) {

    // todo: put this in common superclass
    /**
     * Set player's position in the world using tile coordinates
     * @param x tile's x coordinate
     * @param y tile's y coordinate
     */
    fun moveToTile(x: Int, y: Int) {
        setPosition((x * collisionLayer.tileWidth).toFloat(),
            (y * collisionLayer.tileHeight).toFloat())
    }

    override fun draw(batch: Batch?) {
        update(Gdx.graphics.deltaTime)
        super.draw(batch)
    }

    private fun update(delta: Float) {

    }

    private fun playerInRange() = Vector2.dst(x, y, gameScreen.player.x, gameScreen.player.y) < 4

}