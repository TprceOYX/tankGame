package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.Attackable
import com.jlu.oyx.business.Blockable
import com.jlu.oyx.business.Destroyable
import com.jlu.oyx.business.Sufferable
import com.jlu.oyx.core.Painter

/**
 * 大本营
 * 具有阻挡功能
 * 具有被攻击的功能
 * 具有被销毁的能力
 */
class Camp(override var x: Int, override var y: Int) : Blockable, Sufferable, Destroyable {
    override var blood: Int = 12

    override fun notifySuffer(attackable: Attackable): Array<Part>? {
        //被攻击时做出的反应
        blood -= attackable.attackPower
        if (blood == 3 || blood == 6) {
            val x = x - 30
            val y = y - 30
            return arrayOf(
                Blast(x, y),
                Blast(x + Config.block / 2, y),
                Blast(x + Config.block, y),
                Blast(x + Config.block + Config.block / 2, y),
                Blast(x + Config.block * 2, y),
                Blast(x, y + Config.block / 2),
                Blast(x, y + Config.block),
                Blast(x, y + Config.block + Config.block / 2),
                Blast(x + Config.block * 2, y + Config.block / 2),
                Blast(x + Config.block * 2, y + Config.block),
                Blast(x + Config.block * 2, y + Config.block + Config.block / 2)
            )
        }
        return null
    }

    override var width: Int = Config.block * 2
    override var height: Int = Config.block + 32

    override fun draw() {
        //血量低于6时，外围变为砖墙
        if (blood <= 3) {
            width = Config.block
            height = Config.block
            x = (Config.gameWidth - width) / 2
            y = Config.gameHeight - height
            Painter.drawImage("img/base.png", x, y)
        } else if (blood <= 6) {
            Painter.drawImage("img/wall.png", x, y)
            Painter.drawImage("img/wall.png", x + 30, y)
            Painter.drawImage("img/wall.png", x + 60, y)
            Painter.drawImage("img/wall.png", x + 90, y)

            Painter.drawImage("img/wall.png", x, y + 30)
            Painter.drawImage("img/wall.png", x, y + 60)

            Painter.drawImage("img/wall.png", x + 90, y + 30)
            Painter.drawImage("img/wall.png", x + 90, y + 60)

            Painter.drawImage("img/base.png", x + 30, y + 30)
        } else {
            Painter.drawImage("img/steel.gif", x, y)
            Painter.drawImage("img/steel.gif", x + 30, y)
            Painter.drawImage("img/steel.gif", x + 60, y)
            Painter.drawImage("img/steel.gif", x + 90, y)

            Painter.drawImage("img/steel.gif", x, y + 30)
            Painter.drawImage("img/steel.gif", x, y + 60)

            Painter.drawImage("img/steel.gif", x + 90, y + 30)
            Painter.drawImage("img/steel.gif", x + 90, y + 60)

            Painter.drawImage("img/base.png", x + 30, y + 30)
        }
    }

    override fun isDestroy(): Boolean {
        return blood <= 0
    }

    override fun showDestory(): Array<Part>? {
        return arrayOf(
            Blast(x - Config.block / 2, y - Config.block / 2),
            Blast(x, y - Config.block / 2),
            Blast(x + Config.block / 2, y - Config.block / 2),

            Blast(x - Config.block / 2, y),
            Blast(x, y),
            Blast(x + Config.block / 2, y),
            Blast(x - Config.block / 2, y + Config.block / 2),
            Blast(x, y + Config.block / 2),
            Blast(x + Config.block / 2, y + Config.block / 2),
            DestroyCamp(x, y)
        )
    }
}