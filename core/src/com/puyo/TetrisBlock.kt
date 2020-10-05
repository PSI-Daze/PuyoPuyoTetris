package com.puyo

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Array
import java.awt.Rectangle

class TetrisBlock(var x: Int, var y: Int, var texture: Texture) {

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}