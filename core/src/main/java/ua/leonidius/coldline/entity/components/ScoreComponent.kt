package ua.leonidius.coldline.entity.components

import com.badlogic.ashley.core.Component

data class ScoreComponent(
    var distanceTraversed: Float = 0F,
    var chestsCollected: Int = 0,
): Component