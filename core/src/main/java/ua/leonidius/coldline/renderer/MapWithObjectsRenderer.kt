package ua.leonidius.coldline.renderer

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import ua.leonidius.coldline.level.Level

class MapWithObjectsRenderer(level: Level, unitScale1: Float):
    OrthogonalTiledMapRenderer(level.map, unitScale1) {

    override fun renderObject(obj: MapObject) {
        if (obj is TextureMapObject) {
            batch.draw(obj.textureRegion, obj.x * unitScale, obj.y * unitScale,
                16 * unitScale, 16 * unitScale)
        }
    }

}