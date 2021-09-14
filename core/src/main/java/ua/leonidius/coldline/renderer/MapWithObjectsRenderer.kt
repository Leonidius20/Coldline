package ua.leonidius.coldline.renderer

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

class MapWithObjectsRenderer(private val map1: TiledMap,
                             private val unitScale1: Float):
    OrthogonalTiledMapRenderer(map1, unitScale1) {

    override fun renderObject(obj: MapObject) {
        if (obj is TextureMapObject) {
            batch.draw(obj.textureRegion, obj.x * unitScale, obj.y * unitScale,
                16 * unitScale, 16 * unitScale)
        }
    }

}