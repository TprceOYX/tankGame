package com.jlu.oyx.model

import com.jlu.oyx.Config
import com.jlu.oyx.business.*
import com.jlu.oyx.core.Composer
import com.jlu.oyx.core.Painter
import com.jlu.oyx.enums.Direction
import kotlin.random.Random

/**
 * 敌方坦克
 * 具有移动能力
 * 具有自动移动能力
 * 具有承受攻击能力
 * 具有被销毁的能力
 */
class Enemy(override var x: Int, override var y: Int) : Movable, AutoMovable, Blockable, AutoShot, Sufferable,
    Destroyable {

    override var currentDirection: Direction = Direction.DOWN
    override val speed: Int = 6
    override val width: Int = Config.block
    override val height: Int = Config.block
    //血量
    override var blood: Int = 2
    //如果朝着该方向移动，则必定会发生碰撞
    private var badDirection: Direction? = null
    //上一次射击的时间
    private var lastShotTime = 0L
    //射击的频率
    private val shotFrequency = 800
    //上一次移动的时间
    private var lastMoveTime = 0L
    //移动的频率
    private val moveFrequency = 25
    //坦克类型
    private var type: Int = 0
    //图片路径
    private lateinit var path: String

    init {
        //随机生成坦克类型
        type = Random.nextInt(6) + 1
        path = "enemy$type"
    }

    override fun draw() {
        //根据坦克方向绘制
        val imagePath: String = when (currentDirection) {
            Direction.UP -> "img/${path}U.gif"
            Direction.DOWN -> "img/${path}D.gif"
            Direction.LEFT -> "img/${path}L.gif"
            Direction.RIGHT -> "img/${path}R.gif"
        }
        Painter.drawImage(imagePath, x, y)
    }

//    override fun willCollision(block: Blockable): Direction? {
//        return null
//    }

    //通知即将发生碰撞，direction为发生碰撞的方向
    override fun notifyBlock(direction: Direction?, block: Blockable?) {
        badDirection = direction
    }

    //即将发生碰撞时，随机选择另外三个方向中的一个
    private fun rdm(bad: Direction?): Direction {
        val i: Int = Random.nextInt(4)
        val direction = when (i) {
            0 -> Direction.UP
            1 -> Direction.DOWN
            2 -> Direction.RIGHT
            3 -> Direction.LEFT
            else -> Direction.UP
        }
        if (direction == bad) {
            return rdm(bad)
        }
        return direction
    }

    //自动移动函数的实现
    override fun autoMove() {
        val cTime = System.currentTimeMillis()
        if (cTime - lastMoveTime < moveFrequency) return
        lastMoveTime = cTime
        if (currentDirection == badDirection) {
            currentDirection = rdm(this.badDirection)
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

    //自动射击实现
    override fun autoShot(): Part? {
        //频率检测
        val cTime = System.currentTimeMillis()
        if (cTime - lastShotTime < shotFrequency) return null
        lastShotTime = cTime
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

    //敌方坦克被攻击
    override fun notifySuffer(attackable: Attackable): Array<Part>? {
        if (attackable.owner is Enemy) {
            //敌方坦克互相攻击，则攻击不生效
            return null
        }
        blood -= attackable.attackPower
        Composer.play("sound/hit.wav")
        return arrayOf(Blast(x, y))
    }

    //判断是否被摧毁，血量小于等于0时被摧毁
    override fun isDestroy(): Boolean {
        return blood <= 0
    }

    //展示被摧毁效果
    override fun showDestory(): Array<Part>? {
        return arrayOf(Blast(x,y))
    }
}