package com.jlu.oyx.business

import com.jlu.oyx.model.Part


//承受攻击的能力
interface Sufferable : Part {
    //生命值
    var blood: Int

    fun notifySuffer(attackable: Attackable): Array<Part>?
}