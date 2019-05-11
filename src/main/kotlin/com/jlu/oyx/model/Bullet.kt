package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.Attackable
import com.jlu.oyx.business.AutoMovable
import com.jlu.oyx.business.Destroyable
import com.jlu.oyx.business.Sufferable
import com.jlu.oyx.core.Composer
import com.jlu.oyx.core.Painter
import com.jlu.oyx.enums.Direction
import com.jlu.oyx.ext.checkCollision

/**
 * 子弹
 * 具有自动移动能力
 * 具有被摧毁的能力
 * 具有攻击能力（造成伤害的能力）
 * 具有被攻击的能力
 */
//create函数返回子弹位置
class Bullet(
    override val owner: Part,
    override val currentDirection: Direction,
    create: (width: Int, height: Int) -> Pair<Int, Int>
) :
    AutoMovable, Destroyable, Attackable, Sufferable {
    override var blood: Int = 1

    override val speed: Int = 8

    override var width: Int = Config.block
    override var height: Int = Config.block

    //子弹发射的位置
    override var x: Int = 0
    override var y: Int = 0
    override var attackPower: Int = 1
    private var isDestroyed = false
    private val imgPath: String = when (currentDirection) {
        Direction.UP -> "img/bullet_u.png"
        Direction.RIGHT -> "img/bullet_r.png"
        Direction.LEFT -> "img/bullet_l.png"
        Direction.DOWN -> "img/bullet_d.png"
    }

    init {
        //计算宽度、高度

        val size = Painter.size(imgPath)
        val pair = create.invoke(size[0], size[1])
        x = pair.first
        y = pair.second
        width = size[0]
        height = size[1]
        Composer.play("sound/fire.wav")

    }

    override fun draw() {
        Painter.drawImage(imgPath, x, y)
    }

    override fun autoMove() {
        when (currentDirection) {
            Direction.UP -> y -= speed
            Direction.DOWN -> y += speed
            Direction.LEFT -> x -= speed
            Direction.RIGHT -> x += speed
        }
    }

    //判断子弹是否要被销毁
    override fun isDestroy(): Boolean {
        if (isDestroyed) return true
        if (x < -width) return true
        if (x > Config.gameWidth) return true
        if (y < -height) return true
        if (y > Config.gameHeight) return true
        return false
    }

    override fun isCollision(sufferable: Sufferable): Boolean {
        return checkCollision(sufferable)
    }

    override fun notifyAttack(sufferable: Sufferable) {
        //子弹打到墙上，子弹需要被销毁
        isDestroyed = true
        //println("子弹接收到碰撞")
    }

    override fun notifySuffer(attackable: Attackable): Array<Part>? {
        return arrayOf(Blast(x,y))
    }

}