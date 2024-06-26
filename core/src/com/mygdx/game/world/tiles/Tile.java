package com.mygdx.game.world.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.HitBox;

public class Tile extends HitBox {
    protected Texture texture;

    public Tile(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height);
        this.texture = texture;
    }

    public Tile(float x, float y, Texture texture) {
        super(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);
        this.texture = texture;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void dispose() {
        texture.dispose();
    }

    public Texture getTexture() {
        return texture;
    }
}
