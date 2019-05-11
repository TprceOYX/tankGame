package com.jlu.oyx.business

import com.jlu.oyx.Config
import com.jlu.oyx.enums.Direction
import com.jlu.oyx.model.Part

interface Movable : Part {
    var currentDirection: Direction
    val speed: Int
    //判断碰撞发生，与物体或者边界
    fun willCollision(block: Blockable): Direction? {
        var x: Int = this.x
        var y: Int = this.y
        //计算下一次行动将要到达的位置
        when (currentDirection) {
            Direction.RIGHT -> x += speed
            Direction.LEFT -> x -= speed
            Direction.DOWN -> y += speed
            Direction.UP -> y -= speed
        }
        //判断是否碰到边界
        if (x < 0) return Direction.LEFT
        if (y < 0) return Direction.UP
        if (x > Config.gameWidth - width) return Direction.RIGHT
        if (y > Config.gameHeight - height) return Direction.DOWN
        //判断是否与物体发生碰撞
        val flag: Boolean = checkCollision(x, y, width, height, block.x, block.y, block.width, block.height)
        return if (flag) currentDirection else null
    }

    fun notifyBlock(direction: Direction?, block: Blockable?)
}