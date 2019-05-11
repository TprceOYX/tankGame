package com.jlu.oyx

import com.jlu.oyx.business.*
import com.jlu.oyx.core.Window
import com.jlu.oyx.enums.Direction
import com.jlu.oyx.model.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CopyOnWriteArrayList

class GameWindow : Window(
    title = "坦克大战",
    icon = "img/logo.jpg",
    width = Config.gameWidth,
    height = Config.gameHeight
) {


    //管理地图元素的集合
    private val parts: CopyOnWriteArrayList<Part> = CopyOnWriteArrayList<Part>()
    //玩家坦克
    private lateinit var tank: Tank
    //游戏是否结束
    private var gameOver: Boolean = false
    //敌方总共有多少坦克
    private var enemyNum = 20
    //地图上同时能有多少坦克
    private var enemyMax = 4
    //敌方坦克出生点
    private val enemyBornLocation = arrayListOf<Pair<Int, Int>>()
    //敌方坦克出生点索引
    private var index = 0
    //现在为第几关
    private var currentLevel: Int = 1
    //总的关卡数
    private val mapNum = 2

    override fun onCreate() {
        createMap()
    }

    private fun createMap() {
        //利用map文件绘制地图
        val resourceAsStream = javaClass.getResourceAsStream("/map/$currentLevel.map")
        val reader = BufferedReader(InputStreamReader(resourceAsStream, "utf-8"))
        val lines: List<String> = reader.readLines()
        var lineNum = 0
        lines.forEach { line ->
            var colNum = 0
            line.toCharArray().forEach { column ->
                when (column) {
                    //1表示砖
                    '1' -> parts.add(Wall(colNum * Config.block, lineNum * Config.block))
                    //2表示草
                    '2' -> parts.add(Glass(colNum * Config.block, lineNum * Config.block))
                    //3表示铁墙
                    '3' -> parts.add(Steel(colNum * Config.block, lineNum * Config.block))
                    //4表示水
                    '4' -> parts.add(Water(colNum * Config.block, lineNum * Config.block))
                    //5表示敌方坦克出生点
                    '5' -> enemyBornLocation.add(Pair(colNum * Config.block, lineNum * Config.block))
                }
                colNum++
            }
            lineNum++
        }


        //添加我方坦克
        tank = Tank(Config.block * 10, Config.block * 12)
        parts.add(tank)

        //添加大本营
        parts.add(Camp(Config.gameWidth / 2 - Config.block, Config.gameHeight - 90))
    }

    override fun onDisplay() {
        //绘制地图中的元素
        parts.forEach {
            it.draw()
        }
    }

    override fun onKeyPressed(event: KeyEvent) {
        if (!gameOver) {
            when (event.code) {
                KeyCode.W -> {
                    tank.move(Direction.UP)//;println("W")
                }
                KeyCode.S -> {
                    tank.move(Direction.DOWN)//;println("S")
                }
                KeyCode.A -> {
                    tank.move(Direction.LEFT)//;println("A")
                }
                KeyCode.D -> {
                    tank.move(Direction.RIGHT)//;println("D")
                }
                KeyCode.ENTER -> {
                    val bullet: Bullet = tank.shot()
                    parts.add(bullet)
                }
                else -> {
                }
            }
        }
    }

    //在onDisplay前执行
    override fun onRefresh() {
        //移动碰撞检测(不能与自己碰撞)
        //println("fresh")
        //销毁物体
        parts.filter { it is Destroyable }.forEach {
            if ((it as Destroyable).isDestroy()) {
                parts.remove(it)
                if (it is Enemy) {
                    //敌方坦克被摧毁，敌方坦克剩余数减一
                    enemyNum--
                }
                if (it is Tank) {
                    //玩家坦克被摧毁，游戏结束
                    parts.add(GameOver())
                    gameOver = true
                }
                if(it is Camp){
                    //己方大本营被摧毁，游戏结束
                    parts.add(GameOver())
                    gameOver = true
                }
                val destroy = it.showDestory()
                destroy?.let {
                    parts.addAll(destroy)
                }
            }
        }
        if (gameOver) {
            return
        }
        //玩家坦克移动
        parts.filter { it is Movable }.forEach { move ->
            move as Movable
            var badDirection: Direction? = null
            var badBlock: Blockable? = null
            parts.filter { (it is Blockable) and (move != it) }.forEach blockTag@{ block ->
                block as Blockable
                val direction: Direction? = move.willCollision(block)
                direction?.let {
                    badDirection = direction
                    badBlock = block
                    return@blockTag
                }
            }
            move.notifyBlock(badDirection, badBlock)

        }
        //自动移动模型移动(子弹，敌方坦克)
        parts.filter { it is AutoMovable }.forEach {
            (it as AutoMovable).autoMove()
        }
        //检测攻击是否发生
        parts.filter { it is Attackable }.forEach { attack ->
            attack as Attackable
            //子弹不能攻击将其发射出去的物体
            parts.filter { (it is Sufferable) and (attack.owner != it) and (it != attack) }
                .forEach sufferTag@{ suffer ->
                    suffer as Sufferable
                    if (attack.isCollision(suffer)) {
                        //产生碰撞
                        //通知攻击产生碰撞
                        attack.notifyAttack(suffer)
                        //通知被攻击者产生碰撞
                        val sufferView = suffer.notifySuffer(attack)
                        sufferView?.let {
                            //产生攻击效果
                            parts.addAll(sufferView)
                        }
                        return@sufferTag
                    }
                }
        }
        //检测自动射击
        parts.filter { it is AutoShot }.forEach {
            it as AutoShot
            val shot = it.autoShot()
            shot?.let {
                parts.add(shot)
            }
        }
        //检测是否进入下一关(当前关卡敌人被全部消灭)
        if(enemyNum <= 0){
            currentLevel++
            if(currentLevel <= mapNum) {
                parts.clear()
                enemyNum = 1
                createMap()
            }
        }
        //检测游戏是否结束(大本营被摧毁，或者打通所有关)
        if ((currentLevel > mapNum)) {
            parts.add(GameOver())
            gameOver = true
        }

        //敌方出生,当地图上敌人数量小于enemyMax
        if ((enemyNum > 0) and (parts.filter { it is Enemy }.size < enemyMax)) {
            val i = index % enemyBornLocation.size
            val pair = enemyBornLocation[i]
            parts.add(Enemy(pair.first, pair.second))
            index++
        }
    }
}