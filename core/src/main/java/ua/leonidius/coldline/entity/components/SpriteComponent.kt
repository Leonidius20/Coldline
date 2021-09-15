package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite

data class SpriteComponent(
    var sprite: Sprite = Sprite()
): Component