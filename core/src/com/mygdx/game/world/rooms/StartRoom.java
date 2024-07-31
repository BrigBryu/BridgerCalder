package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.EntryTile;

public class StartRoom extends Room {
    /**
     * The start cords for the player
     */
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

    /**
     * start cord x
     * @return start x
     */
    public int getEntryX() {
        return entryX;
    }

    /**
     * start cord y
     * @return start y
     */
    public int getEntryY() {
        return entryY;
    }
}
