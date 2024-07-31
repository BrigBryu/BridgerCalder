package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.EntryTile;

public class EndRoom extends Room {
    private int exitX, exitY;

    public EndRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture, Texture entryTexture) {
        super(x, y, width, height, floorTexture, wallTexture);
        addEntryTile(entryTexture);
    }

    private void addEntryTile(Texture entryTexture) {
        exitX = getX() + getWidth() / 2;
        exitY = getY() + getHeight() / 2;
        getTiles().add(new EntryTile((exitX + 1) * Constants.TILE_SIZE, exitY * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile((exitX - 1 )* Constants.TILE_SIZE, exitY * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile(exitX * Constants.TILE_SIZE, (exitY - 1) * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile(exitX * Constants.TILE_SIZE, (exitY + 1) * Constants.TILE_SIZE, entryTexture));
        getTiles().add(new EntryTile(exitX * Constants.TILE_SIZE, exitY * Constants.TILE_SIZE, entryTexture));
    }

    public int getExitX() {
        return exitX;
    }

    public int getExitY() {
        return exitY;
    }
}
