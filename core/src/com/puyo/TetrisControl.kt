package com.puyo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import kotlin.random.Random


class TetrisControl() {

    val CELL_SIZE: Int = 30

    var textures: com.badlogic.gdx.utils.Array<Texture> = com.badlogic.gdx.utils.Array(7)
    var nextTetrominos: com.badlogic.gdx.utils.Array<Tetromino> = com.badlogic.gdx.utils.Array(5)
    lateinit var currentTetromino: Tetromino

    var dropTetrominoTimer: Float = 0f
    var downKeyHeldTimer: Float = 0f

    var rows: Int = 25
    var columns: Int = 10
    var cells: Array<com.badlogic.gdx.utils.Array<TetrisBlock>> = Array(rows) {com.badlogic.gdx.utils.Array<TetrisBlock>(columns)}

    init {
        textures.addAll(Texture("blue_tile.png"), Texture("dark_blue_tile.png"), Texture("green_tile.png"),
                Texture("orange_tile.png"), Texture("purple_tile.png"), Texture("red_tile.png"), Texture("yellow_tile.png"))

        for (column in cells) {
            for (i in 0 until columns) {
                column.add(null)
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
                    cells[cell.y][cell.x] = cell
                }
            }
        }
        if (tetrominoLanded(block)) block.isFalling = false

    }

    fun dropTetromino (block: Tetromino) {
        if (block.isFalling && !tetrominoLanded(block)) {
            for (i in block.shape.size - 1 downTo 0) {
                for (j in block.shape[i].size - 1 downTo 0) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].y + 1][block.shape[i][j].x] = block.shape[i][j]
                        cells[block.shape[i][j].y][block.shape[i][j].x] = null
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
                    if (block.shape[i][j].y < rows - 1) {
                        if (cells[block.shape[i][j].y + 1][block.shape[i][j].x] != null &&
                                block.shape[i + 1][j] == null) {
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
            for (j in block.shape[0].size - 1 downTo 0) { // reverse order is necessary
                for (i in block.shape.size - 1 downTo 0) {
                    if(block.shape[i][j] != null) {
                        cells[block.shape[i][j].y][block.shape[i][j].x + 1] = block.shape[i][j]
                        cells[block.shape[i][j].y][block.shape[i][j].x] = null
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
                    if (block.shape[i][j].x != columns - 1) {
                        if (cells[block.shape[i][j].y][block.shape[i][j].x + 1] != null &&
                                cells[block.shape[i][j].y][block.shape[i][j].x + 1] != block.shape[i][j + 1]) {
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
            for (j in 0 until block.shape[0].size) {
                for (i in 0 until block.shape.size) {
                    if(block.shape[i][j] != null) {
                        cells[block.shape[i][j].y][block.shape[i][j].x - 1] = block.shape[i][j]
                        cells[block.shape[i][j].y][block.shape[i][j].x] = null
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
                    if (block.shape[i][j].x != 0) {
                        if (cells[block.shape[i][j].y][block.shape[i][j].x - 1] != null &&
                                cells[block.shape[i][j].y][block.shape[i][j].x - 1] != block.shape[i][j - 1]) {
                            return true
                        }
                    } else return true
                }
            }
        }
        return false
    }

    fun turnLeft(block: Tetromino){ // might change for I
        if (leftTurnPossible(block)) {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        cells[block.shape[i][j].y][block.shape[i][j].x] = null
                    }
                }
            }
            block.turnLeft()
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].y][block.shape[i][j].x] = block.shape[i][j]
                    }
                }
            }
        }
    }

    fun leftTurnPossible(block: Tetromino): Boolean { // I-BLOCK ROTATION STILL NEEDS UPDATED COORDINATES
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
    }

    fun turnRight(block: Tetromino){ // not compatible with I
        if (rightTurnPossible(block)) {
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        // matrix formula, just for clarity sake I keep unnecessary steps
                        cells[block.shape[i][j].y][block.shape[i][j].x] = null
                    }
                }
            }
            block.turnRight()
            for (i in block.shape.indices) {
                for (j in 0 until block.shape[i].size) {
                    if (block.shape[i][j] != null) {
                        cells[block.shape[i][j].y][block.shape[i][j].x] = block.shape[i][j]
                    }
                }
            }
        }
    }

    fun rightTurnPossible(block: Tetromino): Boolean { // might need to change I-Block rotation
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
    }

    fun updateRows() {
        var fullRows: com.badlogic.gdx.utils.Array<Int> = getFullRows()
        if (fullRows.size > 0) {
            for (row in fullRows) {
                for (i in row downTo 1) {
                    for (j in 0 until cells[i].size) {
                        cells[i][j] = cells[i - 1][j]
                    }
                }
                for (i in 0 until cells[0].size) {
                    cells[0][i] = null
                }
            }
        }
    }

    fun getFullRows(): com.badlogic.gdx.utils.Array<Int> {
        var rowIsFull: Boolean = true
        // LibGDX Array because apparently ArrayList takes memory space
        var fullRows: com.badlogic.gdx.utils.Array<Int> = com.badlogic.gdx.utils.Array()
        for (i in cells.indices) {
            for (j in 0 until cells[i].size) {
                if (cells[i][j] == null) rowIsFull = false
            }
            if (rowIsFull) {
                fullRows.add(i)
            } else rowIsFull = true
        }
        return fullRows
    }

    fun isFull(): Boolean {
        for (i in 2 until cells[1].size - 2) { // 1 instead of 0 because the first row is invisible
            if (cells[1][i] != null) {
                return true
            }
        }
        return false
    }

}