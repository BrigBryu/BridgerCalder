package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;
import java.util.Random;

import static com.mygdx.game.util.Constants.*;

//Could be good for groves
public class ClusteredRoom extends Room {
    private Random random = new Random();
    private int[][] roomLayout;

    public ClusteredRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture) {
        super(x, y, width, height, floorTexture, wallTexture);
        generateClusteredWalls();
        initializeTiles();
        surroundWithWalls();
    }

    private void generateClusteredWalls() {
        int roomWidth = getWidth();
        int roomHeight = getHeight();

        roomLayout = new int[roomWidth][roomHeight];

        int numberOfClusters = (roomWidth * roomHeight) / 100;

        for (int n = 0; n < numberOfClusters; n++) {
            int clusterSize = random.nextInt(5) + 3; // size between 3 and 7
            int startX = random.nextInt(roomWidth - clusterSize);
            int startY = random.nextInt(roomHeight - clusterSize);

            for (int i = startX; i < startX + clusterSize; i++) {
                for (int j = startY; j < startY + clusterSize; j++) {
                    if (i < roomWidth && j < roomHeight) {
                        roomLayout[i][j] = WALL;
                    }
                }
            }
        }

        // rest of the room is floor
        for (int i = 0; i < roomWidth; i++) {
            for (int j = 0; j < roomHeight; j++) {
                if (roomLayout[i][j] != WALL) {
                    roomLayout[i][j] = FLOOR;
                }
            }
        }
    }

    private void initializeTiles() {
        List<Tile> tiles = getTiles();
        tiles.clear();

        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (roomLayout[i][j] == WALL) {
                    tiles.add(new WallTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, wallTexture));
                } else {
                    tiles.add(new FloorTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, floorTexture));
                }
            }
        }
    }
}
