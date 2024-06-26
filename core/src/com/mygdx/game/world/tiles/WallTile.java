package com.mygdx.game.world.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WallTile extends Tile {

    public WallTile(float x, float y, float width, float height, Texture texture) {
        super(x, y, width, height, texture);
    }

    public WallTile(int x, int y, Texture wallTexture) {
        super(x,y,wallTexture);
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