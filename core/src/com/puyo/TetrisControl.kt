package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import kotlin.random.Random


class TetrisControl() {

    var textures: com.badlogic.gdx.utils.Array<Texture> = com.badlogic.gdx.utils.Array(7)
    var nextTetrominos: com.badlogic.gdx.utils.Array<Tetromino> = com.badlogic.gdx.utils.Array(5)
    lateinit var currentTetromino: Tetromino

    // y-offsets have to be reversed from srs system
    var offsets02: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))
    var offsetsL: Array<Pair<Int, Int>> = arrayOf(Pair(-1, 0), Pair(-1, 1), Pair(0, -2), Pair(-1, -2))
    var offsetsR: Array<Pair<Int, Int>> = arrayOf(Pair(1, 0), Pair(1, 1), Pair(0, -2), Pair(-1, -2))

    var offsetsMap: MutableMap<Char, Array<Pair<Int, Int>>> = mutableMapOf('0' to offsets02, 'L' to offsetsL, 'R' to offsetsR, '2' to offsets02)

    // I has own offsets (what a troublesome tetromino)
    var iOffsets02: Array<Pair<Int, Int>> = arrayOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))
    var iOffsetsL: Array<Pair<Int, Int>> = arrayOf(Pair(-1, 0), Pair(-1, 1), Pair(0, -2), Pair(-1, -2))
    var iOffsetsR: Array<Pair<Int, Int>> = arrayOf(Pair(1, 0), Pair(1, 1), Pair(0, -2), Pair(-1, -2))

    var iOffsetsMap: MutableMap<Char, Array<Pair<Int, Int>>> = mutableMapOf('0' to iOffsets02, 'L' to iOffsetsL, 'R' to iOffsetsR, '2' to iOffsets02)


    var dropTetrominoTimer: Float = 0f
    var downKeyHeldTimer: Float = 0f

    var columns: Int = 10
    var rows: Int = 25
    var cells: Array<com.badlogic.gdx.utils.Array<TetrisBlock>> = Array(columns) {com.badlogic.gdx.utils.Array<TetrisBlock>(rows)}

    init {
        textures.addAll(Texture("blue_tile.png"), Texture("dark_blue_tile.png"), Texture("green_tile.png"),
                Texture("orange_tile.png"), Texture("purple_tile.png"), Texture("red_tile.png"), Texture("yellow_tile.png"))

        for (row in cells) {
            for (i in 0 until rows) {
                row.add(null)
            }
        }
        createNextTetrominos()
        spawnTetromino()
    }

    fun handleInputs(delta: Float) {
        if (dropTetrominoTimer > 0.6f) {
            if (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
            } else if(!isFull()) {
                updateRows()
                spawnTetromino()
            }
            dropTetrominoTimer = 0f
        }
        else dropTetrominoTimer += delta

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            moveLeft(currentTetromino)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            moveRight(currentTetromino)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (!tetrominoLanded(currentTetromino)) dropTetromino(currentTetromino)
            if (currentTetromino.isFalling) dropTetrominoTimer = 0f
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!tetrominoLanded(currentTetromino)) {
                downKeyHeldTimer += delta + 0.02f
                if (downKeyHeldTimer > 0.6f) dropTetrominoTimer += 0.2f
            }
        } else downKeyHeldTimer = 0f


        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentTetromino.isFalling) dropTetrominoTimer = 0.5f
            while (currentTetromino.isFalling) {
                dropTetromino(currentTetromino)
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            if (currentTetromino.isFalling) turnLeft(currentTetromino)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            if (currentTetromino.isFalling) turnRight(currentTetromino)
        }
    }
    fun spawnTetromino(){
        currentTetromino = nextTetrominos.pop()
        var tetrominos: CharArray = charArrayOf('T', 'O', 'I', 'J', 'L', 'S', 'Z')
        nextTetrominos.insert(0, Tetromino(4, 1, tetrominos[Random.nextInt(6)], textures[Random.nextInt(6)]))
        addTetromino(currentTetromino)
    }

    fun createNextTetrominos() {
        nextTetrominos.addAll(null, null, null, null, null)
        var tetrominos: CharArray = charArrayOf('T', 'O', 'I', 'J', 'L', 'S', 'Z')
        for (i in 0 until nextTetrominos.size) {
            nextTetrominos[i] = Tetromino(4, 1, tetrominos[Random.nextInt(6)], textures[Random.nextInt(6)])
        }
    }

    fun addTetromino (block: Tetromino) {
        for (i in block.shape.indices) {
            for (cell in block.shape[i]) {
                if (cell != null) {
                    cells[cell.column][cell.row] = cell
                }
            }
        }
        if (tetrominoLanded(block)) block.isFalling = false

    }

    fun dropTetromino (block: Tetromino) {
        if (block.isFalling && !tetrominoLanded(block)) {
            for (i in block.shape.size - 1 downTo 0) {
                for (j in block.shape[i].size - 1 downTo 0) {
                    if (block.shape[j][i] != null) {
                        cells[block.shape[j][i].column][block.shape[j][i].row + 1] = block.shape[j][i]
                        cells[block.shape[j][i].column][block.shape[j][i].row] = null
                    }
                }

            }
            block.move(0, 1)
        } else if (tetrominoLanded(block)) block.isFalling = false
    }

    fun tetrominoLanded (block: Tetromino): Boolean { // only gives back if can't move down
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].row < rows - 1) {
                        if (cells[block.shape[i][j].column][block.shape[i][j].row + 1] != null &&
                                block.shape[i][j + 1] == null) {
                            return true
                        }
                    } else {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun moveRight(block: Tetromino) {
        if (block.isFalling && !rightIsBlocked(block)) {
            for (i in block.shape[0].size - 1 downTo 0) { // reverse order is necessary
                for (j in block.shape.size - 1 downTo 0) {
                    if(block.shape[i][j] != null) {
                        cells[block.shape[i][j].column + 1][block.shape[i][j].row] = block.shape[i][j]
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            block.move(1, 0)
        }
    }

    fun rightIsBlocked (block: Tetromino): Boolean {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].column < columns - 1) {
                        if (cells[block.shape[i][j].column + 1][block.shape[i][j].row] != null &&
                                cells[block.shape[i][j].column + 1][block.shape[i][j].row] != block.shape[i + 1][j]) {
                            return true
                        }
                    } else return true
                }
            }
        }
        return false
    }

    fun moveLeft(block: Tetromino) {
        if (block.isFalling && !leftIsBlocked(block)) {
            for (i in 0 until block.shape[0].size) {
                for (j in 0 until block.shape.size) {
                    if(block.shape[i][j] != null) {
                        cells[block.shape[i][j].column - 1][block.shape[i][j].row] = block.shape[i][j]
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            block.move(-1, 0)
        }
    }

    fun leftIsBlocked (block: Tetromino): Boolean {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].column > 0) {
                        if (cells[block.shape[i][j].column - 1][block.shape[i][j].row] != null &&
                                cells[block.shape[i][j].column - 1][block.shape[i][j].row] != block.shape[i - 1][j]) {
                            return true
                        }
                    } else return true
                }
            }
        }
        return false
    }

    fun turnLeft(block: Tetromino){ // might change for I
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            block.turnLeft()
            if (wrongState(block)) {
                block.turnRight()
                wallKickDef(block, 'L')
            }
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = block.shape[i][j]
                    }
                }
            }
        }
    }

    fun wallKickDef(block: Tetromino, rotation: Char) {
        var oldState: Char = block.rotationState
        if (rotation == 'L') block.turnLeft()
        else if (rotation == 'R') block.turnRight()

        for (i in 0..3) {
            block.move(offsetsMap.get(oldState)!!.get(i).first - offsetsMap.get(block.rotationState)!!.get(i).first,
                    offsetsMap.get(oldState)!!.get(i).second - offsetsMap.get(block.rotationState)!!.get(i).second)
            if (wrongState(block)) {
                block.move(-(offsetsMap.get(oldState)!!.get(i).first - offsetsMap.get(block.rotationState)!!.get(i).first),
                        -(offsetsMap.get(oldState)!!.get(i).second - offsetsMap.get(block.rotationState)!!.get(i).second))
            } else break
        }
        if (wrongState(block)) {
            if (rotation == 'L') block.turnRight() else block.turnLeft()
        }
        println(oldState)
        println(block.rotationState)
        println(offsetsMap.get(oldState)!!.get(0).first - offsetsMap.get(block.rotationState)!!.get(0).first)
        println(offsetsMap.get(oldState)!!.get(0).second - offsetsMap.get(block.rotationState)!!.get(0).second)
    }

    fun wallKickI() {

    }

    /*fun leftTurnPossible(block: Tetromino): Boolean { // I-BLOCK ROTATION STILL NEEDS UPDATED COORDINATES
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        var diffI: Float = i - block.pivotX
                        var diffJ: Float = j - block.pivotY
                        var newI = (diffI * 0) + (diffJ * -1)
                        var newJ = (diffI * 1) + (diffJ * 0)
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        var diffX: Int = block.shape[i][j].x - block.x
                        var diffY: Int = block.shape[i][j].y - block.y
                        var newX: Int = (diffX * 0) + (diffY * -1)
                        var newY: Int = (diffX * 1) + (diffY * 0)

                        if ((block.x - newX) < 0 || (block.x - newX) >= columns ||
                                (block.y - newY) < 0 || (block.y - newY) >= rows) {
                            return false
                        } else if (cells[((block.y - newY))]
                                        [((block.x - newX))] != null &&
                                cells[((block.y - newY))]
                                        [((block.x - newX))] != block.shape[(block.pivotX + newI).toInt()][(block.pivotY + newJ).toInt()]) {
                            return false
                        }
                    }
                }
            }
        } else return false
        return true
    }*/

    fun turnRight(block: Tetromino){ // not compatible with I
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        cells[block.shape[i][j].column][block.shape[i][j].row] = null
                    }
                }
            }
            block.turnRight()
            if (wrongState(block)) {
                block.turnLeft()
                wallKickDef(block, 'R')
            }
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].column][block.shape[i][j].row] = block.shape[i][j]
                    }
                }
            }
        }
    }

    /* fun rightTurnPossible(block: Tetromino): Boolean { // might need to change I-Block rotation
        if (block.type != 'O') {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        var diffI: Float = i - block.pivotX
                        var diffJ: Float = j - block.pivotY
                        var newI = (diffI * 0) + (diffJ * -1)
                        var newJ = (diffI * 1) + (diffJ * 0)
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        var diffX: Int = block.shape[i][j].x - block.x
                        var diffY: Int = block.shape[i][j].y - block.y
                        var newX: Int = (diffX * 0) + (diffY * -1)
                        var newY: Int = (diffX * 1) + (diffY * 0)

                        if ((block.x + newX) < 0 || (block.x + newX) >= columns ||
                                (block.y + newY) < 0 || (block.y + newY) >= rows) {
                            return false
                        } else if (cells[((block.y + newY))]
                                        [((block.x + newX))]!= null &&
                                cells[((block.y + newY))]
                                        [((block.x + newX))] != block.shape[(block.pivotX - newI).toInt()][(block.pivotY - newJ).toInt()]) {
                            return false
                        }
                    }
                }
            }
        } else return false
        return true
    }*/

    fun wrongState(block: Tetromino): Boolean {
        for (i in block.shape.indices) {
            for (j in 0 until block.shape[i].size) {
                if (block.shape[i][j] != null) {
                    if (block.shape[i][j].column < 0 || block.shape[i][j].column >= columns ||
                            block.shape[i][j].row < 0 || block.shape[i][j].row >= rows) {
                        return true
                    } else if (cells[block.shape[i][j].column][block.shape[i][j].row] != null) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun updateRows() {
        var fullRows: com.badlogic.gdx.utils.Array<Int> = getFullRows()
        if (fullRows.size > 0) {
            for (row in fullRows) {
                for (j in row downTo 1) {
                    for (i in cells.size - 1 downTo 0) {
                        cells[i][j] = cells[i][j - 1]
                    }
                }
                for (i in 0 until cells.size) {
                    cells[i][0] = null
                }
            }
        }
    }

    fun getFullRows(): com.badlogic.gdx.utils.Array<Int> {
        var rowIsFull: Boolean = true
        // LibGDX Array because apparently ArrayList takes memory space
        var fullRows: com.badlogic.gdx.utils.Array<Int> = com.badlogic.gdx.utils.Array()
        for (i in 0 until cells[0].size) {
            for (j in 0 until cells.size) {
                if (cells[j][i] == null) rowIsFull = false
            }
            if (rowIsFull) {
                fullRows.add(i)
            } else rowIsFull = true
        }
        return fullRows
    }

    fun isFull(): Boolean {
        for (i in 2 until cells.size - 2) { // 1 instead of 0 because the first row is invisible
            if (cells[i][1] != null) {
                return true
            }
        }
        return false
    }

}