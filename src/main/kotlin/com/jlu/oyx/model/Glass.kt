package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.core.Painter


/**
 * 草坪
 */
class Glass(override val x: Int, override val y: Int) : Part {
    override val width: Int = Config.block
    override val height: Int = Config.block

    override fun draw() {
        Painter.drawImage("img/grass.png", x, y)
    }
}