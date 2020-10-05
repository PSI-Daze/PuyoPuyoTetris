package com.puyo

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Rectangle
import kotlin.math.roundToInt


class Tetromino(var x: Int, var y: Int, var type: Char, var texture: Texture):
        Block() {

    // Array is two dimensional [row][column]
    var shape: Array<com.badlogic.gdx.utils.Array<TetrisBlock>>
    var isFalling: Boolean
    var width: Float = 0f
    var height: Float = 0f
    var rows: Int = 0
    var columns: Int = 0
    var pivotX: Float = 3f
    var pivotY: Float = 3f

    init {
        shape = Array(7) {com.badlogic.gdx.utils.Array<TetrisBlock>(7)}
        for (array in shape) {
            array.addAll(null, null, null, null, null, null, null)
        }
        when (type) {
            'T' -> createT()
            'O' -> createO()
            'I' -> createI()
            'J' -> createJ()
            'L' -> createL()
            'S' -> createS()
            'Z' -> createZ()
        }
        isFalling = true
        rows = blockRows()
        columns = blockColumns()
        width = columns * 30f
        height = rows * 30f
    }

    fun move(x: Int, y: Int) {
        for (i in shape.indices) {
            for (brick in shape[i]) {
                if (brick != null) {
                    brick.x += x
                    brick.y += y
                }
            }
        }
        this.x += x
        this.y += y
    }

    fun turnLeft() {
        var newShape = Array(7) {com.badlogic.gdx.utils.Array<TetrisBlock>(7)}
        for (array in newShape) {
            array.addAll(null, null, null, null, null, null, null)
        }
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    // matrix formula, just for clarity sake I keep unnecessary steps (3/3 is usually middle)
                    var diffI: Float = i - pivotX
                    var diffJ: Float = j - pivotY
                    var newI = (diffI * 0) + (diffJ * -1)
                    var newJ = (diffI * 1) + (diffJ * 0)

                    var diffX: Int = shape[i][j].x - x
                    var diffY: Int = shape[i][j].y - y
                    var newX: Int = (diffX * 0) + (diffY * -1)
                    var newY: Int = (diffX * 1) + (diffY * 0)

                    newShape[(pivotX + newI).toInt()][(pivotY + newJ).toInt()] = shape[i][j]
                    // I do not know why I have to subtract for coordinates don't ask me it works
                    newShape[(pivotX + newI).toInt()][(pivotY + newJ).toInt()].setPosition(x - newX, y - newY)
                }
            }
        }
        shape = newShape
    }

    fun turnRight() {
        var newShape = Array(7) {com.badlogic.gdx.utils.Array<TetrisBlock>(7)}
        for (array in newShape) {
            array.addAll(null, null, null, null, null, null, null)
        }
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    // matrix formula, just for clarity sake I keep unnecessary steps (3/3 is middle)
                    var diffI: Float = i - pivotX
                    var diffJ: Float = j - pivotY
                    var newI = (diffI * 0) + (diffJ * -1)
                    var newJ = (diffI * 1) + (diffJ * 0)

                    var diffX: Int = shape[i][j].x - x
                    var diffY: Int = shape[i][j].y - y
                    var newX: Int = (diffX * 0) + (diffY * -1)
                    var newY: Int = (diffX * 1) + (diffY * 0)

                    newShape[(pivotX - newI).toInt()][(pivotY - newJ).toInt()] = shape[i][j]
                    // I do not know why I have to add for coordinates don't ask me it works
                    newShape[(pivotX - newI).toInt()][(pivotY - newJ).toInt()].setPosition(x + newX, y + newY)
                }
            }
        }
        shape = newShape
    }

    fun blockRows(): Int {
        var rows: Int = 0
        var indices: IntArray = IntArray(7)
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    if (!indices.contains(i)) {
                        indices[i] = i
                        rows++
                    }
                }
            }
        }
        return rows
    }

    fun blockColumns(): Int {
        var columns: Int = 0
        var indices: IntArray = IntArray(7)
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    if (!indices.contains(j)) {
                        indices[j] = j
                        columns++
                    }
                }
            }
        }
        return columns
    }

    fun firstRow(): Int {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    return i
                }
            }
        }
        return -1
    }
    fun firstColumn(): Int {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[j][i] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun createT() {
        shape[3][2] = TetrisBlock(x - 1, y, texture)
        shape[3][3] = TetrisBlock(x, y, texture) // main brick for T-Block
        shape[3][4] = TetrisBlock(x + 1, y, texture)
        shape[2][3] = TetrisBlock(x, y - 1, texture)
    }

    fun createO() {
        shape[2][2] = TetrisBlock(x - 1, y - 1, texture)
        shape[2][3] = TetrisBlock(x, y - 1, texture)
        shape[3][2] = TetrisBlock(x - 1, y, texture)
        shape[3][3] = TetrisBlock(x, y, texture) // main brick for O-Block
    }

    fun createI() {
        shape[3][2] = TetrisBlock(x - 1, y, texture)
        shape[3][3] = TetrisBlock(x, y, texture) // main brick for I-Block (doesn't matter tho)
        shape[3][4] = TetrisBlock(x + 1, y, texture)
        shape[3][5] = TetrisBlock(x + 2, y, texture)
        //pivotX = 3.5f
        //pivotY = 3.5f
    }

    fun createJ() {
        shape[3][2] = TetrisBlock(x - 1, y, texture) // main brick for J-Block
        shape[3][3] = TetrisBlock(x, y, texture)
        shape[3][4] = TetrisBlock(x + 1, y, texture)
        shape[2][2] = TetrisBlock(x - 1, y - 1, texture)
    }

    fun createL() {
        shape[3][4] = TetrisBlock(x + 1, y, texture) // main brick for L-Block
        shape[3][3] = TetrisBlock(x, y, texture)
        shape[3][2] = TetrisBlock(x - 1, y, texture)
        shape[2][4] = TetrisBlock(x + 1, y - 1, texture)
    }

    fun createS() {
        shape[2][3] = TetrisBlock(x, y - 1, texture)
        shape[2][4] = TetrisBlock(x + 1, y - 1, texture)
        shape[3][2] = TetrisBlock(x - 1, y, texture)
        shape[3][3] = TetrisBlock(x, y, texture) // main brick for S-Block
    }

    fun createZ() {
        shape[2][3] = TetrisBlock(x, y - 1, texture)
        shape[2][2] = TetrisBlock(x - 1, y - 1, texture)
        shape[3][3] = TetrisBlock(x, y, texture) // main brick for Z-Block
        shape[3][4] = TetrisBlock(x + 1, y, texture)
    }
}