package com.jlu.oyx.ext

import com.jlu.oyx.model.Part

fun Part.checkCollision(part: Part): Boolean {
    return checkCollision(x, y, width, height, part.x, part.y, part.width, part.height)
}