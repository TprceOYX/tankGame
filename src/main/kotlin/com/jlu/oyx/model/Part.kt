package com.jlu.oyx.model

/**
 * 显示的元素
 */
interface Part {
    //位置
    val x: Int
    val y: Int
    //宽高
    val width: Int
    val height: Int
    //显示
    fun draw()

    fun checkCollision(
        x1: Int, y1: Int, w1: Int, h1: Int,
        x2: Int, y2: Int, w2: Int, h2: Int
    ): Boolean {
        return when {
            x2 + w2 <= x1 -> //碰撞块在左方
                false
            y2 + h2 <= y1 -> //上方
                false
            y1 + h1 <= y2 -> //下方
                false
            else -> x1 + w1 > x2
        }
    }
}