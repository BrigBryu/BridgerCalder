package com.mygdx.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.util.InteractiveHitBox;
import com.mygdx.game.world.rooms.Room;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mygdx.game.util.Constants.wallIntersectionsOn;

public class GameMap {



    public enum MapState {
        GENERATION,  // When the map is being generated
        USAGE        // When the map is in use (after conversion to a 2D array)
    }

    private List<Tile> tiles;
    private List<WallTile> wallTiles;
    /**
     * For debugging never actually used in running the game
     */
    protected List<HitBox> hitBoxesToRender;
    protected List<InteractiveHitBox> hitBoxes;
    private Set<String> wallTileSet;
    private Texture floorTexture;
    private Texture wallTexture;
    private Tile[][] tileMap;
    private MapState mapState = MapState.GENERATION;
    /**
     * for drawing hit boxes
     */
    protected ShapeRenderer shapeRenderer;
    protected OrthographicCamera camera;
    private boolean cameraSet = false;

    public GameMap() {
        tiles = new ArrayList<>();
        wallTiles = new ArrayList<>();
        tileMap = new Tile[0][0];
        wallTileSet = new HashSet<>();
        hitBoxesToRender = new ArrayList<>();
        hitBoxes = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();

    }

    /**
     * used for testing screen that's broken...So ignore
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

    //TODO needs tileMap implementation
    public void render(SpriteBatch batch) {
        for(Tile[] tiles1: tileMap) {
            for(Tile tile: tiles1) {
                if(tile != null) {
                    tile.render(batch);
                }
            }
        }

//        for (Tile tile : tiles) {
//            tile.render(batch);
//        }
//        for (WallTile wallTile : wallTiles) {
//            wallTile.render(batch);
//        }

        batch.end();
        if (Constants.DRAW_HIT_BOXES) {
            drawHitBoxes();
        }
        batch.begin();
    }

    //TODO needs tileMap implementation
    public void dispose() {

        for(Tile[] tiles1: tileMap) {
            for(Tile tile: tiles1) {
                tile.dispose();
            }
        }
//        for (Tile tile : tiles) {
//            tile.getTexture().dispose();
//        }
//        for (WallTile wallTile : wallTiles) {
//            wallTile.getTexture().dispose();
//        }
    }

    //TODO tileMap used
    /**
     * Used for collision detection (so uses tileMap not lists)
     * Note all parameters are in tiles
     * @param x bottom cord in tiles
     * @param y left side cord in tiles
     * @param size of square to check for wall tiles in tiles
     * @return the wall tiles in the square created from (x,y) bottom left of size, size
     */
    public List<WallTile> getWallTiles(int x, int y, int size, boolean print) {
        List<WallTile> wallTileList = new ArrayList<>();
        for(int i = x; i < x + size; i++) {
            for(int j = y; j < y + size; j++) {
                if(i >= 0 && j >= 0 && tileMap[j][i] instanceof WallTile){
                    wallTileList.add((WallTile) tileMap[j][i]);
                }
            }
        }
        // Debugging hitbox rendering
        if(Constants.DRAW_HIT_BOXES) {
            hitBoxesToRender.add(new HitBox(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, size * Constants.TILE_SIZE, size * Constants.TILE_SIZE));
            if(hitBoxesToRender.size() > 1) {
                hitBoxesToRender.remove(hitBoxesToRender.size() - 2);
            }
        }
        if(print) {
            System.out.println("Making new hitBox (" + x + ", " + y + ")" + " size " + size + " getting: " + wallTileList.size() + " Num of hit boxes " + hitBoxesToRender.size());
        }

        return wallTileList;
    }

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param tile to add
     */
    public void addTile(Tile tile) {
        if (tile instanceof WallTile && wallIntersectionsOn) {
            addWallTile((WallTile) tile);
        } else {
            tiles.add(tile);
        }
    }
    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param wallTile to add
     */
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

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param wallTile to add
     */
    public void addWallTile(WallTile wallTile) {
        wallTiles.add(wallTile);
        wallTileSet.add(wallTile.getX() / Constants.TILE_SIZE + "," + wallTile.getY() / Constants.TILE_SIZE);
    }

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param tile to add
     */
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

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param tile to add
     */
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

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param tile to add
     */
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

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param x cord
     * @param y cord
     * @return if in hall
     */
    public boolean isHallwayTile(int x, int y) {
        for (Tile tile : tiles) {
            if (tile instanceof HallwayTile && tile.getX() == x && tile.getY() == y) {
                return true;
            }
        }
        return false;
    }


    /**
     * Used to check if player is in room. Then will update player based on that.
     * @param x cord
     * @param y cord
     * @param rooms list to check if contains (x,y)
     * @return if in room true
     */
    public boolean isInRoom(int x, int y, List<Room> rooms) {
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used for dungeon generation (so uses list implementation and should not be called after setTileMap)
     * @param x cord
     * @param y cord
     * @return if in wall
     */
    public boolean isWallTile(int x, int y) {
        if(mapState == MapState.GENERATION) {
            return wallTileSet.contains(x + "," + y);
        } else {
            throw new TileMapConversionException("wallTileSet");
        }
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
    private void drawHitBoxes() {

        if(Constants.DRAW_HIT_BOXES && cameraSet) {

            shapeRenderer.setProjectionMatrix(camera.combined); // Set the projection matrix
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            // Draw enemy hitbox
            shapeRenderer.setColor(0.5f, 0, 1, 1); // Yellow for enemy hitbox
            for(HitBox hitbox: hitBoxesToRender) {
                shapeRenderer.rect(hitbox.getX() - 1, hitbox.getY() - 1, hitbox.getWidth() + 1, hitbox.getHeight() + 1);
            }
            shapeRenderer.setColor(1f, 0.5f, 0.5f, 1); // Yellow for enemy hitbox
            for(InteractiveHitBox hitBox: hitBoxes) {
                shapeRenderer.rect(hitBox.getX() - 1, hitBox.getY() - 1, hitBox.getWidth() + 1, hitBox.getHeight() + 1);
            }
            shapeRenderer.end();
        }
    }


    /**
     * Set the tileMap 2d array then null the lists of tiles
     * Run the game on the 2d array
     */
    public void setTileMap(List<Room> rooms) {
        //TODO might want to start with a bigger map to minimize moving everything over as often
        tileMap = new Tile[50][50];
        for (Tile tile : tiles) {
            int x = (int) (tile.getX() / Constants.TILE_SIZE);
            int y = (int) (tile.getY() / Constants.TILE_SIZE);
            if (x > tileMap.length - 1 || y > tileMap.length - 1){
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
        setHitBoxesFromRooms(rooms);
    }

    private void setHitBoxesFromRooms(List<Room> rooms){
        for(Room room:rooms){
            if(room.getHitBoxes() != null) {
                hitBoxes.addAll(room.getHitBoxes());
            }
        }
    }

    private void doubleTileMap(){
        Tile[][] hold = new Tile[tileMap.length * 2][tileMap.length * 2];
        for(int i = 0; i < tileMap.length; i++){
            //IDE changed instead of another for loop can look for errors here TODO
            System.arraycopy(tileMap[i], 0, hold[i], 0, tileMap[i].length);
        }
        tileMap = hold;
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

    public List<InteractiveHitBox> getHitboxes() {
        return hitBoxes;
    }

    public String toString(){
        StringBuilder r = new StringBuilder();
        for(Tile[] tiles1: tileMap){
            for(Tile tile: tiles1){
                if(tile instanceof FloorTile) {
                    r.append(" F ");
                } else if(tile instanceof WallTile) {
                    r.append(" W ");
                } else if(tile instanceof HallwayTile) {
                    r.append(" H ");
                } else {
                    r.append("   ");
                }
            }
            r.append("\n");
        }
        return r.toString();
    }

    public Tile[][] getTileMap(){
        return tileMap;
    }


    public Tile getTileAt(float x, float y) {
        if(x < tileMap.length && x >= 0 && y < tileMap[0].length && y >=0) {
            return tileMap[((int)x)][((int)y)];
        } else {
            return null;
        }
    }

}
