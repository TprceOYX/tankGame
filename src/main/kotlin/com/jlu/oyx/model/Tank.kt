package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.*
import com.jlu.oyx.core.Composer
import com.jlu.oyx.core.Painter
import com.jlu.oyx.enums.Direction


/**
 * 我方坦克
 * 具有移动能力
 * 具备阻挡能力
 * 具备承受攻击的能力
 */
class Tank(override var x: Int, override var y: Int) : Movable, Blockable, Sufferable, Destroyable {


    override val width: Int = Config.block
    override val height: Int = Config.block

    //方向
    override var currentDirection: Direction = Direction.UP
    //速度
    override var speed = 6
    //血量
    override var blood: Int = 3
    //会发生碰撞的方向
    private var badDirection: Direction? = null

    //绘制坦克
    override fun draw() {
        //根据坦克方向绘制
        val imagePath: String = when (currentDirection) {
            Direction.UP -> "img/p1tankU.gif"
            Direction.DOWN -> "img/p1tankD.gif"
            Direction.LEFT -> "img/p1tankL.gif"
            Direction.RIGHT -> "img/p1tankR.gif"
        }
        Painter.drawImage(imagePath, x, y)
    }

    //坦克移动
    fun move(direction: Direction) {
        //先转向后移动


        if (direction == badDirection) {
            return
        }
        if (currentDirection != direction) {
            currentDirection = direction
            return
        }
        when (currentDirection) {
            Direction.RIGHT -> x += speed
            Direction.LEFT -> x -= speed
            Direction.DOWN -> y += speed
            Direction.UP -> y -= speed
        }
        //边界检测
        if (x < 0) x = 0
        if (y < 0) y = 0
        if (x > Config.gameWidth - width) x = Config.gameWidth - width
        if (y > Config.gameHeight - height) y = Config.gameHeight - height
    }

    //通知坦克在direction方向会与block发生碰撞
    override fun notifyBlock(direction: Direction?, block: Blockable?) {
        this.badDirection = direction
    }

    //发射子弹的方法
    fun shot(): Bullet {

        return Bullet(this, currentDirection) { bulletWidth, bulletHeight ->
            //计算子弹的位置
            var bulletX = 0
            var bulletY = 0


            when (currentDirection) {
                Direction.UP -> {
                    bulletX = this.x + (this.width - bulletWidth) / 2
                    bulletY = this.y - bulletHeight / 2
                }
                Direction.DOWN -> {
                    bulletX = this.x + (this.width - bulletWidth) / 2
                    bulletY = this.y + this.height - bulletHeight / 2
                }
                Direction.LEFT -> {
                    bulletX = this.x - bulletHeight / 2
                    bulletY = this.y + (this.height - bulletHeight) / 2
                }
                Direction.RIGHT -> {
                    bulletX = this.x + this.width - bulletWidth / 2
                    bulletY = this.y + (this.height - bulletHeight) / 2
                }
            }
            Pair(bulletX, bulletY)
        }
    }

    override fun notifySuffer(attackable: Attackable): Array<Part>? {
        blood -= attackable.attackPower
        Composer.play("sound/hit.wav")
        return arrayOf(Blast(x, y))
    }

    override fun isDestroy(): Boolean {
        return blood <= 0
    }

    override fun showDestory(): Array<Part>? {
        return arrayOf(Blast(x, y))
    }
}