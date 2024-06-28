package com.mygdx.game.world.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;

public class EntryTile extends Tile {
    public EntryTile(float x, float y, Texture texture) {
        super(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, texture);
    }
}
