package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a room within the dungeon.
 */
public class Room {
    private int x, y, width, height;
    private List<Tile> tiles;
    private Texture texture;

    public Room(int x, int y, int width, int height, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
        tiles = new ArrayList<>();
        initializeTiles();
    }

    private void initializeTiles() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles.add(new Tile(x + i * Constants.TILE_SIZE, y + j * Constants.TILE_SIZE, texture));
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (Tile tile : tiles) {
            tile.render(batch);
        }
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void connect(Room other) {
        int midX1 = this.x + this.width / 2;
        int midY1 = this.y + this.height / 2;
        int midX2 = other.x + other.width / 2;
        int midY2 = other.y + other.height / 2;

        int deltaX = midX2 - midX1;
        int deltaY = midY2 - midY1;
        int stepX = deltaX > 0 ? 1 : -1;
        int stepY = deltaY > 0 ? 1 : -1;

        for (int x = midX1; x != midX2; x += stepX) {
            tiles.add(new Tile(x * Constants.TILE_SIZE, midY1 * Constants.TILE_SIZE, texture));
        }
        for (int y = midY1; y != midY2; y += stepY) {
            tiles.add(new Tile(midX2 * Constants.TILE_SIZE, y * Constants.TILE_SIZE, texture));
        }
    }

    public int getX() {
        return x;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }
}
