package com.mygdx.game.world.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HallwayTile extends Tile {

    public HallwayTile(float x, float y, Texture texture) {
        super(x, y, texture);
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