package com.puyo.desktop

import com.badlogic.gdx.Gdx
import kotlin.jvm.JvmStatic
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.utils.viewport.Viewport
import drop.PuyoPuyoTetris

object DesktopLauncher {

    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = "Puyo Puyo Tetris"
        config.width = 750
        config.height = 750
        LwjglApplication(PuyoPuyoTetris(), config)
    }
}