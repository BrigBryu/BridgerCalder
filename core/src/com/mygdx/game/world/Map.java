package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.rooms.Room;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.mygdx.game.util.Constants.wallIntersectionsOn;

public class Map {

    private List<Tile> tiles;
    private List<WallTile> wallTiles;
    private Texture floorTexture;
    private Texture wallTexture;

    public Map() {
        tiles = new ArrayList<>();
        wallTiles = new ArrayList<>();
    }

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
        wallTiles.add(new WallTile(3 * Constants.TILE_SIZE, 3 * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallTexture));
    }

    public void render(SpriteBatch batch) {
        for (Tile tile : tiles) {
            tile.render(batch);
        }
        for (WallTile wallTile : wallTiles) {
            wallTile.render(batch);
        }
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
        if(tile instanceof WallTile && wallIntersectionsOn){
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
                wallTiles.add(wallTile);
            } else {
                tiles.add(wallTile);
            }
        }
    }


    public void addWallTile(WallTile wallTile) {
        wallTiles.add(wallTile);
    }

    public boolean intersectsWithRoom(Room room) {
        for (Tile tile : tiles) {
            if (room.intersects(tile)) {
                return true;
            }
        }
        for (WallTile wallTile : wallTiles) {
            if (room.intersects(wallTile)) {
                return true;
            }
        }
        return false;
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
                wallTiles.add((WallTile) tile);
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
                wallTiles.add((WallTile) tile);
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
            wallTiles.add((WallTile) tile);
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

    public boolean intersectsWithRoom(Room room, List<Tile> ignoreTiles) {
        for (Tile tile : tiles) {
            if (ignoreTiles != null && ignoreTiles.contains(tile)) {
                continue;
            }
            if (room.intersects(tile)) {
                return true;
            }
        }
        for (WallTile wallTile : wallTiles) {
            if (ignoreTiles != null && ignoreTiles.contains(wallTile)) {
                continue;
            }
            if (room.intersects(wallTile)) {
                return true;
            }
        }
        return false;
    }
//
//    public void cleanUpIsolatedWalls() {
//        for (int i = 0; i < wallTiles.size(); i++) {
//            WallTile wallTile = wallTiles.get(i);
//            int wallCount = countAdjacentWalls(wallTile);
//
//            if (wallCount == 0 || wallCount == 2 || (wallCount == 3 && random.nextDouble() < 0.5)) {
//                wallTiles.remove(i);
//                tiles.add(new FloorTile(wallTile.getX(), wallTile.getY(), floorTexture));
//                i--;
//            }
//        }
//    }

    private int countAdjacentWalls(WallTile wallTile) {
        int count = 0;
        int x = (int) wallTile.getX();
        int y = (int) wallTile.getY();

        for (WallTile wt : wallTiles) {
            if (Math.abs(wt.getX() - x) <= Constants.TILE_SIZE && Math.abs(wt.getY() - y) <= Constants.TILE_SIZE) {
                if (wt != wallTile) {
                    count++;
                }
            }
        }
        return count;
    }

    public Tile[][] to2DArray() {
        Tile[][] tileArray = new Tile[20][25];
        for (Tile tile : tiles) {
            int x = (int) (tile.getX() / Constants.TILE_SIZE);
            int y = (int) (tile.getY() / Constants.TILE_SIZE);
            tileArray[y][x] = tile;
        }
        for (WallTile wallTile : wallTiles) {
            int x = (int) (wallTile.getX() / Constants.TILE_SIZE);
            int y = (int) (wallTile.getY() / Constants.TILE_SIZE);
            tileArray[y][x] = wallTile;
        }
        return tileArray;
    }

    public boolean isInRoom(int x, int y, List<Room> rooms) {
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

}