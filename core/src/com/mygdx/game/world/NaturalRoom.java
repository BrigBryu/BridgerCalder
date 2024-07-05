package com.mygdx.game.world;


import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;


import java.util.List;
import java.util.Random;


import static com.mygdx.game.util.Constants.FLOOR;
import static com.mygdx.game.util.Constants.WALL;


public class NaturalRoom extends Room {
    private int[][] map;
    private Random random = new Random();


    public NaturalRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture) {
        super(x, y, width, height, floorTexture, wallTexture);
        map = new int[width][height];
        generateRoom();
        validate();
        initializeTiles();
        surroundWithWalls();
    }


    private void generateRoom() {
        initializeMap(0.45); // Initial wall probability
        for (int i = 0; i < 5; i++) { // Number of simulation steps
            simulateStep();
        }
    }


    private void initializeMap(double wallProbability) {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                map[i][j] = random.nextDouble() < wallProbability ? WALL : FLOOR;
            }
        }
    }


    private void simulateStep() {
        int[][] newMap = new int[getWidth()][getHeight()];
        for (int i = 1; i < getWidth() - 1; i++) {
            for (int j = 1; j < getHeight() - 1; j++) {
                int walls = countWalls(i, j);
                if (random.nextDouble() < 0.95) {
                    // Attempt to push wall tiles to the corners with 70% chance
                    if (map[i][j] == WALL) {
                        pushToCorner(newMap, i, j);
                    } else {
                        newMap[i][j] = (walls > 4 || walls == 0) ? WALL : FLOOR;
                    }
                } else {
                    newMap[i][j] = (walls > 4 || walls == 0) ? WALL : FLOOR;
                }
            }
        }
        map = newMap;
    }

    private void pushToCorner(int[][] newMap, int x, int y) {
        // Determine the nearest corner
        int[] target = new int[2];
        if (x < getWidth() / 2 && y < getHeight() / 2) {
            target[0] = 0; target[1] = 0; // Top-left corner
        } else if (x < getWidth() / 2 && y >= getHeight() / 2) {
            target[0] = 0; target[1] = getHeight() - 1; // Bottom-left corner
        } else if (x >= getWidth() / 2 && y < getHeight() / 2) {
            target[0] = getWidth() - 1; target[1] = 0; // Top-right corner
        } else {
            target[0] = getWidth() - 1; target[1] = getHeight() - 1; // Bottom-right corner
        }

        // Move the wall tile towards the corner
        newMap[target[0]][target[1]] = WALL;
        if (target[0] > 0) newMap[target[0] - 1][target[1]] = WALL;
        if (target[1] > 0) newMap[target[0]][target[1] - 1] = WALL;
    }


    private int countWalls(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (map[x + i][y + j] == WALL) count++;
            }
        }
        return count;
    }


    private void validate() {
        ensurePassageForCharacter();
        removeIsolatedWalls();

    }


    private void ensurePassageForCharacter() {
        for (int i = 1; i < getWidth() - 1; i++) {
            for (int j = 1; j < getHeight() - 2; j++) {
                // Ensure vertical passage
                if (map[i][j] != WALL && map[i][j + 1] != WALL) {
                    map[i][j] = FLOOR;
                    map[i][j + 1] = FLOOR;
                }
            }
        }

        for (int i = 1; i < getWidth() - 2; i++) {
            for (int j = 1; j < getHeight() - 1; j++) {
                // Ensure horizontal passage
                if (map[i][j] != WALL && map[i + 1][j] != WALL) {
                    map[i][j] = FLOOR;
                    map[i + 1][j] = FLOOR;
                }
            }
        }
    }

    private void removeIsolatedWalls() {
        for (int i = 1; i < getWidth() - 1; i++) {
            for (int j = 1; j < getHeight() - 1; j++) {
                int wallCount = countWalls(i, j);
                if (map[i][j] == WALL) {
                    if (wallCount == 0 || wallCount == 2 || (wallCount == 3 && random.nextDouble() < 0.5)) {
                        map[i][j] = FLOOR;
                    }
                }
            }
        }
    }

    protected void initializeTiles() {
        List<Tile> tiles = getTiles();
        tiles.clear();


        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (map[i][j] == WALL) {
                    tiles.add(new WallTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, wallTexture));
                } else {
                    tiles.add(new FloorTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, floorTexture));
                }
            }
        }
    }
}




