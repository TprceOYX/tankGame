package com.jlu.oyx.business

import com.jlu.oyx.enums.Direction
import com.jlu.oyx.model.Part

//自动移动的能力
interface AutoMovable : Part {
    //移动速度
    val speed:Int

    //当前移动方向
    val currentDirection:Direction
    //实现自动移动的函数
    fun autoMove()
}