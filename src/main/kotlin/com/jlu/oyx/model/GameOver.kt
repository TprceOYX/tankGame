package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.core.Painter


/**
 * 游戏结束画面
 */
class GameOver : Part {
    override var x: Int = 0
    override var y: Int = 0
    override var width: Int = 0
    override var height: Int = 0
    private val imgPath = "img/over.gif"

    init {
        val size = Painter.size(imgPath)
        width = size[0]
        height = size[1]
        x = (Config.gameWidth - width) / 2
        y = (Config.gameHeight - height) / 2
    }

    override fun draw() {
        Painter.drawImage(imgPath, x, y)
    }
}