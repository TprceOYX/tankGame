package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.core.Painter

/**
 * 被摧毁的基地
 */
class DestroyCamp(override val x: Int, override val y: Int) :Part {
    override val width: Int = Config.block
    override val height: Int = Config.block

    override fun draw() {
        Painter.drawImage("img/destory.gif", x, y)
    }
}