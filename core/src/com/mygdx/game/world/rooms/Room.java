package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.InteractiveHitBox;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;

public class Room {
    /**
     * Not in pixels but in tiles
     * I think is at the bottom left of room
     */
    private int x, y;
    /**
     * Also not in pixles but in tiles
     */
    private int  width, height;
    protected List<Tile> tiles;
    protected Texture floorTexture;
    protected Texture wallTexture;
    protected List<InteractiveHitBox> hitBoxes;


    public Room(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floorTexture = floorTexture;
        this.wallTexture = wallTexture;
        tiles = new ArrayList<>();
        hitBoxes = new ArrayList<>();
        initializeTiles();
    }

    private void initializeTiles() {
        tiles.clear();

        // Create floor tiles
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles.add(new FloorTile((x + i) * Constants.TILE_SIZE, (y + j) * Constants.TILE_SIZE, floorTexture));
            }
        }

        // Create wall tiles around the edges
        surroundWithWalls();
    }

    protected void surroundWithWalls(){

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
    /**
     * in tiles
     */
    public int getX() {
        return x;
    }
    /**
     * in tiles
     */
    public int getY() {
        return y;
    }
    /**
     * in tiles
     */
    public int getWidth() {
        return width;
    }

    /**
     * in tiles
     */
    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
        initializeTiles();
    }

    public void setY(int y) {
        this.y = y;
        initializeTiles();
    }

    public void render(SpriteBatch batch) {
        for (Tile tile : tiles) {
            tile.render(batch);
        }
    }


    public boolean intersects(Tile tile) {
        return this.getX() < tile.getX() + tile.getWidth() &&
                this.getX() + this.getWidth() > tile.getX() &&
                this.getY() < tile.getY() + tile.getHeight() &&
                this.getY() + this.getHeight() > tile.getY();
    }

    public boolean intersects(int x, int y) {
        return this.getX() < x  &&
                this.getX() + this.getWidth() > x &&
                this.getY() < y &&
                this.getY() + this.getHeight() > y;
    }

    public boolean contains(int px, int py) {
        return (px/ Constants.TILE_SIZE) >= x && (px/ Constants.TILE_SIZE) < x + width && (py/ Constants.TILE_SIZE) >= y && (py/ Constants.TILE_SIZE) < y + height;
    }

    public List<InteractiveHitBox> getHitBoxes(){
        return hitBoxes;
    }
}
