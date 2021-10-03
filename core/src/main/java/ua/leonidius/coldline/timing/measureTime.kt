package ua.leonidius.coldline.timing

import com.badlogic.gdx.utils.TimeUtils

/**
 * Runs a function measuring the time it took to run it
 * @param function function to run
 * @return elapsed time in milliseconds
 */
fun measureTime(function: () -> Unit): Double {
    val timeBefore = TimeUtils.nanoTime().toBigInteger()
    function()
    val timeAfter = TimeUtils.nanoTime().toBigInteger()
    return (timeAfter - timeBefore).toDouble() / 1000000.0
}