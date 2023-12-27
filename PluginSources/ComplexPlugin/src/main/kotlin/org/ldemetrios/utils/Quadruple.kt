package org.ldemetrios.utils

import org.ldemetrios.plugin.Main

data class Quadruple(val w: Int, val x: Int, val y: Int, val z: Int) {
    fun sum() = w + x + y + z
}

fun Number.toHexAndHash() = toInt().toString(16).hashCode()
