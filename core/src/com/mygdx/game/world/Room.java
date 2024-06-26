package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int x, y, width, height;
    private List<Tile> tiles;
    private Texture floorTexture;
    private Texture wallTexture;

    public Room(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floorTexture = floorTexture;
        this.wallTexture = wallTexture;
        tiles = new ArrayList<>();
        initializeTiles();
    }

    private void initializeTiles() {
        // Create floor tiles
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles.add(new FloorTile((x + i) * Constants.TILE_SIZE, (y + j) * Constants.TILE_SIZE, floorTexture));
            }
        }

        // Create wall tiles around the edges
        for (int i = 0; i < width; i++) {
            tiles.add(new WallTile((x + i) * Constants.TILE_SIZE, y * Constants.TILE_SIZE, wallTexture));
            tiles.add(new WallTile((x + i) * Constants.TILE_SIZE, (y + height - 1) * Constants.TILE_SIZE, wallTexture));
        }
        for (int j = 0; j < height; j++) {
            tiles.add(new WallTile(x * Constants.TILE_SIZE, (y + j) * Constants.TILE_SIZE, wallTexture));
            tiles.add(new WallTile((x + width - 1) * Constants.TILE_SIZE, (y + j) * Constants.TILE_SIZE, wallTexture));
        }
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void render(SpriteBatch batch) {
        for (Tile tile : tiles) {
            tile.render(batch);
        }
    }
}
