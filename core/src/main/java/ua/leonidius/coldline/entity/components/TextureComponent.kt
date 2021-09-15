package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion

data class TextureComponent(
    var textureRegion: TextureRegion = TextureRegion()
): Component