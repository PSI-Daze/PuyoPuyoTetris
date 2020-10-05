package drop

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class PuyoPuyoTetris : Game() {

    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        this.setScreen(TetrisScreen(this))
    }

    override fun render() {
        super.render()  // important!
    }

    override fun dispose() {
        this.getScreen().dispose()

        batch.dispose()
        font.dispose()
    }
}