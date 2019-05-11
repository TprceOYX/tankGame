package com.jlu.oyx.business

import com.jlu.oyx.model.Part


//造成伤害的能力
interface Attackable : Part {
    //判断是否碰撞
    //所有者
    val owner:Part
    var attackPower:Int
    fun isCollision(sufferable: Sufferable): Boolean

    fun notifyAttack(sufferable: Sufferable)
}