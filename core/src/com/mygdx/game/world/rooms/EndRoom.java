package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Boot;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.util.InteractiveHitBox;
import com.mygdx.game.util.InteractiveHitBoxes.EndHitBox;
import com.mygdx.game.world.tiles.EntryTile;

import java.util.List;

public class EndRoom extends Room {
    private int exitX, exitY;
    private OrthographicCamera camera;

    public EndRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture, Texture entryTexture, OrthographicCamera camera, Boot game) {
        super(x, y, width, height, floorTexture, wallTexture);
        addEntryTile(entryTexture);
        hitBoxes.add(new EndHitBox((exitX - 1) * Constants.TILE_SIZE - 1,(exitY- 1) * Constants.TILE_SIZE - 1, Constants.TILE_SIZE * 3 + 4, Constants.TILE_SIZE * 3 + 4,camera, game));
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
