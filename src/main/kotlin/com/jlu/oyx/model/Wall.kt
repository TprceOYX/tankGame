package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.Attackable
import com.jlu.oyx.business.Blockable
import com.jlu.oyx.business.Destroyable
import com.jlu.oyx.business.Sufferable
import com.jlu.oyx.core.Composer
import com.jlu.oyx.core.Painter

/**
 * 整块墙壁
 * 具备销毁能力
 * 具备阻挡能力
 * 具备遭受攻击能力
 */
class Wall(override val x: Int, override val y: Int) : Blockable, Sufferable, Destroyable {
    //    override val x: Int = 100
//   override val y: Int = 100
    override val width: Int = Config.block
    override val height: Int = Config.block
    override var blood: Int = 3
    override fun draw() {
        Painter.drawImage("img/walls.gif", x, y)
    }

    override fun isDestroy(): Boolean = blood <= 0

    override fun notifySuffer(attackable: Attackable):Array<Part>? {
        //砖墙被破坏了
        blood -= attackable.attackPower
        //墙被攻击发出声音
        Composer.play("sound/hit.wav")

        return arrayOf(Blast(x,y))
    }
}