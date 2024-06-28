package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.EntryTile;
import com.mygdx.game.world.tiles.Tile;

public class StartRoom extends Room {
    private int entryX, entryY;

    public StartRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture, Texture entryTexture) {
        super(x, y, width, height, floorTexture, wallTexture);
        addEntryTile(entryTexture);
    }

    private void addEntryTile(Texture entryTexture) {
        entryX = getX() + getWidth() / 2;
        entryY = getY() + getHeight() / 2;
        getTiles().add(new EntryTile((entryX + 1) * Constants.TILE_SIZE, entryY * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile((entryX - 1 )* Constants.TILE_SIZE, entryY * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile(entryX * Constants.TILE_SIZE, (entryY - 1) * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile(entryX * Constants.TILE_SIZE, (entryY + 1) * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile(entryX * Constants.TILE_SIZE, entryY * Constants.TILE_SIZE, entryTexture));
    }

    public int getEntryX() {
        return entryX;
    }

    public int getEntryY() {
        return entryY;
    }
}
