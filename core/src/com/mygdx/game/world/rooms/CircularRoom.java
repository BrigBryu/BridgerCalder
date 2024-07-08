package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;

public class CircularRoom extends Room {
    public CircularRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture) {
        super(x, y, width, height, floorTexture, wallTexture);
        initializeTiles();
        surroundWithWalls();
    }

    private void initializeTiles() {
        List<Tile> tiles = getTiles();
        tiles.clear();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY);

        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                int dx = i - centerX;
                int dy = j - centerY;
                if (dx * dx + dy * dy <= radius * radius) {
                    tiles.add(new FloorTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, floorTexture));
                } else {
                    tiles.add(new WallTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, wallTexture));
                }
            }
        }
    }
}

