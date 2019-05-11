package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.Attackable
import com.jlu.oyx.business.Blockable
import com.jlu.oyx.business.Sufferable
import com.jlu.oyx.core.Composer
import com.jlu.oyx.core.Painter

/**
 * 整块铁墙
 * 具有阻塞能力
 * 具有承受攻击能力
 */
class Steel(override val x: Int, override val y: Int): Blockable,Sufferable {

    override var blood: Int = 6

    override fun notifySuffer(attackable: Attackable): Array<Part>? {
        Composer.play("sound/hit.wav")
        return null
    }

    override val width: Int = Config.block
    override val height: Int = Config.block

    override fun draw() {
        Painter.drawImage("img/steels.gif",x,y)
    }
}