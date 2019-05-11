package com.jlu.oyx.business

import com.jlu.oyx.model.Part


//被销毁
interface Destroyable:Part {
    //判断是否销毁
    fun isDestroy(): Boolean

    //显示销毁的效果

    fun showDestory():Array<Part>?{
        return null
    }
}