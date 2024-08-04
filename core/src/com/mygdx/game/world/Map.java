package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.rooms.Room;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.mygdx.game.util.Constants.wallIntersectionsOn;

public class Map {

    public enum MapState {
        GENERATION,  // When the map is being generated
        USAGE        // When the map is in use (after conversion to a 2D array)
    }

    private List<Tile> tiles;
    private List<WallTile> wallTiles;
    private List<HitBox> hitBoxes;
    private Set<String> wallTileSet;
    private Texture floorTexture;
    private Texture wallTexture;
    private Tile[][] tileMap;
    private MapState map = MapState.GENERATION;
    /**
     * for drawing hit boxes
     */
    private ShapeRenderer shapeRenderer;
    private Camera camera;
    private boolean cameraSet = false;

    public Map() {
        tiles = new ArrayList<>();
        wallTiles = new ArrayList<>();
        tileMap = new Tile[0][0];
        wallTileSet = new HashSet<>();
    }

    /**
     * used for testing screen that's broken...
     */
    public void initialize() {
        // simple grid of tiles
        floorTexture = new Texture("testFloor.png");
        wallTexture = new Texture("testWall.png");

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 25; x++) {
                tiles.add(new Tile(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, floorTexture));
            }
        }
        // Add wall tiles
        addWallTile(new WallTile(3 * Constants.TILE_SIZE, 3 * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallTexture));
    }

    public void render(SpriteBatch batch) {
        for (Tile tile : tiles) {
            tile.render(batch);
        }
        for (WallTile wallTile : wallTiles) {
            wallTile.render(batch);
        }

        batch.end();
        if (Constants.DRAW_HIT_BOXES) {
            drawHitboxes();
        }
        batch.begin();
    }

    public void dispose() {
        for (Tile tile : tiles) {
            tile.getTexture().dispose();
        }
        for (WallTile wallTile : wallTiles) {
            wallTile.getTexture().dispose();
        }
    }

    public List<WallTile> getWallTiles() {
        return wallTiles;
    }

    public void addTile(Tile tile) {
        if (tile instanceof WallTile && wallIntersectionsOn) {
            addWallTile((WallTile) tile);
        } else {
            tiles.add(tile);
        }
    }

    public void addWallTileNondestructive(WallTile wallTile) {
        boolean intersects = false;
        for (WallTile existingWallTile : wallTiles) {
            if (existingWallTile.getX() == wallTile.getX() && existingWallTile.getY() == wallTile.getY()) {
                intersects = true;
                break;
            }
        }
        for (Tile existingTiles : tiles) {
            if (existingTiles.getX() == wallTile.getX() && existingTiles.getY() == wallTile.getY()) {
                intersects = true;
                break;
            }
        }
        if (!intersects) {
            if (wallIntersectionsOn) {
                addWallTile(wallTile);
            } else {
                tiles.add(wallTile);
            }
        }
    }

    public void addWallTile(WallTile wallTile) {
        wallTiles.add(wallTile);
        wallTileSet.add(wallTile.getX() / Constants.TILE_SIZE + "," + wallTile.getY() / Constants.TILE_SIZE);
    }

    public void addTileDestructiveRegular(Tile tile) {
        boolean replaced = false;
        // overlaps regular tile
        for (int i = 0; i < tiles.size(); i++) {
            Tile existingTile = tiles.get(i);
            if (existingTile.getX() == tile.getX() && existingTile.getY() == tile.getY()) {
                tiles.set(i, tile); // Replace the existing tile
                replaced = true;
                break;
            }
        }

        // no existing tile was replaced, add
        if (!replaced) {
            if (tile instanceof WallTile) {
                addWallTile((WallTile) tile);
            } else {
                tiles.add(tile);
            }
        }
    }

    public void addTileDestructiveWalls(Tile tile) {
        boolean replaced = false;

        // overlaps wall tile
        for (int i = 0; i < wallTiles.size(); i++) {
            WallTile existingWallTile = wallTiles.get(i);
            if (existingWallTile.getX() == tile.getX() && existingWallTile.getY() == tile.getY()) {
                wallTiles.set(i, (WallTile) tile); // Replace the existing wall tile
                replaced = true;
                break;
            }
        }

        // no existing tile was replaced, add
        if (!replaced) {
            if (tile instanceof WallTile) {
                addWallTile((WallTile) tile);
            } else {
                tiles.add(tile);
            }
        }
    }

    public void addTileDestructiveBoth(Tile tile) {
        // Iterates over every element (existingTile) in collection and removes it if overlap with tile
        tiles.removeIf(existingTile -> existingTile.getX() == tile.getX() && existingTile.getY() == tile.getY());
        wallTiles.removeIf(existingWallTile -> existingWallTile.getX() == tile.getX() && existingWallTile.getY() == tile.getY());

        // Add the new tile to the appropriate list
        if (tile instanceof WallTile) {
            addWallTile((WallTile) tile);
        } else {
            tiles.add(tile);
        }
    }

    public boolean isHallwayTile(int x, int y) {
        for (Tile tile : tiles) {
            if (tile instanceof HallwayTile && tile.getX() == x && tile.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public boolean isInRoom(int x, int y, List<Room> rooms) {
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWallTile(int x, int y) {
        return wallTileSet.contains(x + "," + y);
    }

    /**
     * Need to set camera before you can draw hitboxes
     * @param camera OrthographicCamera
     */
    public void setCamera(OrthographicCamera camera){
        this.camera = camera;
        cameraSet = true;
    }

    /**
     * Draws hit boxes if camera is set
     */
    private void drawHitboxes() {
        if(cameraSet) {
            shapeRenderer.setProjectionMatrix(camera.combined); // Set the projection matrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Draw enemy hitbox
            shapeRenderer.setColor(0.5f, 0, 1, 1); // Yellow for enemy hitbox
            for(HitBox hitbox:hitBoxes) {
                shapeRenderer.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
            }

            shapeRenderer.end();
        }
    }


    /**
     * Set the tileMap 2d array then null the lists of tiles
     * Run the game on the 2d array
     */
    public void setTileMap() {
        //TODO might want to start with a bigger map to minimize moving everything over as often
        tileMap = new Tile[50][50];
        for (Tile tile : tiles) {
            int x = (int) (tile.getX() / Constants.TILE_SIZE);
            int y = (int) (tile.getY() / Constants.TILE_SIZE);
            if(x >= tileMap.length || y >= tileMap.length){
                doubleTileMap();
            }
            tileMap[y][x] = tile;
        }
        for (WallTile wallTile : wallTiles) {
            int x = (int) (wallTile.getX() / Constants.TILE_SIZE);
            int y = (int) (wallTile.getY() / Constants.TILE_SIZE);
            if(x >= tileMap.length || y >= tileMap.length){
                doubleTileMap();
            }
            tileMap[y][x] = wallTile;
        }
        tiles = null;
        wallTiles = null;
    }
    private void doubleTileMap(){
        Tile[][] hold = new Tile[tileMap.length * 2][tileMap.length * 2];
        for(int i = 0; i < tileMap.length; i++){
            //IDE changed instead of another for loop can look for errors here TODO
            System.arraycopy(tileMap[i], 0, hold[i], 0, tileMap[i].length);
        }
    }

    public class TileMapConversionException extends RuntimeException {
        /**
         * error for list use after conversion
         * @param message the list that was used
         */
        public TileMapConversionException(String message) {
            super("Tried to use " + message + " instead of the 2d array after conversion");
        }
    }


}
