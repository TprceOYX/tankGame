package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.Blockable
import com.jlu.oyx.core.Painter

/**
 * 水
 */
class Water(override val x: Int, override val y: Int) : Blockable {
    override val width: Int = Config.block
    override val height: Int = Config.block

    override fun draw() {
        Painter.drawImage("img/water.gif", x, y)
    }
}