package com.puyo

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Rectangle
import kotlin.math.roundToInt


class Tetromino(var column: Int, var row: Int, var type: Char, var texture: Texture):
        Block() {

    // Array is two dimensional [row][column]
    var shape: Array<com.badlogic.gdx.utils.Array<TetrisBlock>>
    var rotationState: Char = '0'
    var isFalling: Boolean

    var width: Float = 0f
    var height: Float = 0f
    var columns: Int = 0
    var rows: Int = 0
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
        columns = blockColumns()
        rows = blockRows()
        width = columns * 30f
        height = rows * 30f
    }

    fun move(x: Int, y: Int) {
        for (i in shape.indices) {
            for (brick in shape[i]) {
                if (brick != null) {
                    brick.column += x
                    brick.row += y
                }
            }
        }
        this.column += x
        this.row += y
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

                    var diffX: Float = shape[i][j].column - column.toFloat()
                    var diffY: Float = shape[i][j].row - row.toFloat()

                    var newX: Float = (diffX * 0) + (diffY * -1)
                    var newY: Float = (diffX * 1) + (diffY * 0)

                    newShape[(pivotX - newI).toInt()][(pivotY - newJ).toInt()] = shape[i][j]
                    newShape[(pivotX - newI).toInt()][(pivotY - newJ).toInt()]
                            .setPosition((column - newX).toInt(), (row - newY).toInt())
                    // I really have no idea why I spinning works like this
                }
            }
        }
        shape = newShape
        updateState('L')
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

                    var fraction: Float = pivotX.roundToInt() - pivotX // is 0.5 if pivot is between blocks, otherwise 0
                    var diffX: Float = shape[i][j].column - column.toFloat()
                    var diffY: Float = shape[i][j].row - row.toFloat()
                    diffX -= fraction
                    diffY -= fraction
                    var newX: Float = (diffX * 0) + (diffY * -1)
                    var newY: Float = (diffX * 1) + (diffY * 0)

                    newShape[(pivotX + newI).toInt()][(pivotY + newJ).toInt()] = shape[i][j]
                    // I do not know why I have to add for coordinates don't ask me it works
                    newShape[(pivotX + newI).toInt()][(pivotY + newJ).toInt()]
                            .setPosition((column - fraction + newX).toInt(), (row - fraction + newY).toInt())
                    // I really have no idea why I spinning works like this
                }
            }
        }
        shape = newShape
        updateState('R')
    }

    fun updateState(rotation: Char) {
        if (rotation == 'L') {
            when(rotationState) {
                '0' -> rotationState = 'L'
                'L' -> rotationState = '2'
                'R' -> rotationState = '0'
                '2' -> rotationState = 'R'
            }
        } else if (rotation == 'R') {
            when(rotationState) {
                '0' -> rotationState = 'R'
                'L' -> rotationState = '0'
                'R' -> rotationState = '2'
                '2' -> rotationState = 'L'
            }
        }
    }

    fun blockRows(): Int {
        var rows: Int = 0
        var indices: IntArray = IntArray(7)
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    if (!indices.contains(j)) {
                        indices[j] = j
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
                    if (!indices.contains(i)) {
                        indices[i] = i
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
                if (shape[j][i] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun firstColumn(): Int {
        for (i in shape.indices) {
            for (j in 0 until shape[i].size) {
                if (shape[i][j] != null) {
                    return i
                }
            }
        }
        return -1
    }

    fun createT() {
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for T-Block
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[3][2] = TetrisBlock(column, row - 1, texture)
    }

    fun createO() {
        shape[2][2] = TetrisBlock(column - 1, row - 1, texture)
        shape[3][2] = TetrisBlock(column, row - 1, texture)
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for O-Block
    }

    fun createI() {
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for I-Block (doesn't matter tho)
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[5][3] = TetrisBlock(column + 2, row, texture)
    }

    fun createJ() {
        shape[2][3] = TetrisBlock(column - 1, row, texture) // main brick for J-Block
        shape[3][3] = TetrisBlock(column, row, texture)
        shape[4][3] = TetrisBlock(column + 1, row, texture)
        shape[2][2] = TetrisBlock(column - 1, row - 1, texture)
    }

    fun createL() {
        shape[4][3] = TetrisBlock(column + 1, row, texture) // main brick for L-Block
        shape[3][3] = TetrisBlock(column, row, texture)
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[4][2] = TetrisBlock(column + 1, row - 1, texture)
    }

    fun createS() {
        shape[3][2] = TetrisBlock(column, row - 1, texture)
        shape[4][2] = TetrisBlock(column + 1, row - 1, texture)
        shape[2][3] = TetrisBlock(column - 1, row, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for S-Block
    }

    fun createZ() {
        shape[3][2] = TetrisBlock(column, row - 1, texture)
        shape[2][2] = TetrisBlock(column - 1, row - 1, texture)
        shape[3][3] = TetrisBlock(column, row, texture) // main brick for Z-Block
        shape[4][3] = TetrisBlock(column + 1, row, texture)
    }
}