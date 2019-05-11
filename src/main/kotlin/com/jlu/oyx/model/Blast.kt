package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.Destroyable
import com.jlu.oyx.core.Painter

/**
 * 爆炸效果
 */
class Blast(override val x: Int, override val y: Int) : Destroyable {
    override val width: Int = Config.block
    override val height: Int = Config.block
    private val imagePaths: ArrayList<String> = arrayListOf<String>()
    private var index: Int = 0

    init {
        (1..32).forEach {
            imagePaths.add("img/blast$it.png")
        }
    }

    override fun draw() {
        val i = index % imagePaths.size
        Painter.drawImage(imagePaths[i], x, y)
        index++
    }

    override fun isDestroy(): Boolean {
        return index >= imagePaths.size
    }


}