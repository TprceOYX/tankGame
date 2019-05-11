@file:Suppress("UnusedImport")

package com.jlu.oyx.core


import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
//import java.awt.event.KeyEvent
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

abstract class Window(
    val title: String = "坦克大战"
    , val icon: String = "icon/logo.png"
    , val width: Int = 800
    , val height: Int = 600
) : Application() {
    internal val canvas = MyCanvas(width, height)
    private val looper: Looper = Looper(this)
    internal var fps = 0L

    private var running = false

    private val keyRecorder = mutableMapOf<KeyCode, KeyEvent>()
    private var currentKey: KeyCode? = null

    override fun start(primaryStage: Stage?) {
        val group = Group()
        group.children.add(canvas)

        val scene = Scene(group, width.toDouble(), height.toDouble())

        //为画笔设置源
        Painter.set(canvas.graphicsContext2D)

        primaryStage?.let {
            with(primaryStage) {
                this.title = this@Window.title
                this.scene = scene
                this.isResizable = false
                this.icons.add(Image(icon))

                this.setOnCloseRequest {
                    looper.stop()
                    running = false
                    System.exit(0)
                }
                show()
            }
        }

        scene.onKeyPressed = EventHandler() { event ->

            Thread {
                Thread.currentThread().name = "hm-key"
                currentKey = event.code
                //记录
                keyRecorder[event.code] = event
                this@Window.onKeyPressed(event)
            }.start()
        }

        scene.onKeyReleased = EventHandler() { event ->
            keyRecorder.remove(event.code)
            if (currentKey == event.code) {
                currentKey = null
            }
        }

        //初始化回调
        onCreate()

        looper.start()
        running = true

        Thread {
            Thread.sleep(200)
            while (true) {
                Thread.sleep(80)
                if (!running) break

                keyRecorder.filter { entry ->
                    entry.key != currentKey
                }.forEach { _, u ->
                    onKeyPressed(u)
                }
            }
        }.start()


        Thread {
            Thread.sleep(200)
            while (true) {
                Thread.sleep(20)
                if (!running) break

                this@Window.onRefresh()
            }
        }.let {
            it.isDaemon = true
            it.priority = Thread.MAX_PRIORITY
            it.start()
        }

    }


    abstract fun onCreate()

    abstract fun onDisplay()

    abstract fun onRefresh()

    abstract fun onKeyPressed(event: KeyEvent)
}

class Looper(private val window: Window) : AnimationTimer() {
    private val width = window.width.toDouble()
    private val height = window.height.toDouble()
    private var lastTime = 0L

    override fun handle(now: Long) {
        if (lastTime == 0L) {
            lastTime = now
        } else {
            window.fps = 1000 * 1000 * 1000 / (now - lastTime)
        }
        lastTime = now

        val gc = window.canvas.graphicsContext2D
        //清屏

        gc.fill = Color.BLACK
        gc.fillRect(0.0, 0.0, width, height)

        window.onDisplay()

        System.gc()
    }

}

class MyCanvas(
    width: Int = 800
    , height: Int = 600
) : Canvas() {

    init {
        setWidth(width.toDouble())
        setHeight(height.toDouble())
    }

}