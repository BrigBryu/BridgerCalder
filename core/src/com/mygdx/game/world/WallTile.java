package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WallTile extends Tile {

    public WallTile(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height, texture);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch); // Use the render method from Tile
    }

    @Override
    public void dispose() {
        super.dispose(); // Use the dispose method from Tile
    }
}