package com.mygdx.game.world.rooms;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;
import java.util.Random;
import java.util.Stack;

import static com.mygdx.game.util.Constants.*;

//DFS and as going make halls but then shuffle  the order of direction choice
public class MazeRoom extends Room {
    private Random random = new Random();
    private int[][] maze;

    public MazeRoom(int x, int y, int width, int height, Texture floorTexture, Texture wallTexture) {
        super(x, y, width, height, floorTexture, wallTexture);
        generateMaze();
        initializeTiles();
        surroundWithWalls();
    }

    private void generateMaze() {
        int mazeWidth = getWidth();
        int mazeHeight = getHeight();

        maze = new int[mazeWidth][mazeHeight];

        // Initialize maze with walls
        for (int i = 0; i < mazeWidth; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                maze[i][j] = WALL;
            }
        }

        Stack<int[]> stack = new Stack<>();
        int[] start = {random.nextInt((mazeWidth / 3)) * 3 + 1, random.nextInt((mazeHeight / 3)) * 3 + 1};
        maze[start[0]][start[1]] = FLOOR;
        maze[start[0] + 1][start[1]] = FLOOR;
        maze[start[0]][start[1] + 1] = FLOOR;
        maze[start[0] + 1][start[1] + 1] = FLOOR;
        stack.push(start);

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];
            int[][] directions = {{3, 0}, {-3, 0}, {0, 3}, {0, -3}};
            shuffleArray(directions);

            boolean moved = false;
            for (int[] direction : directions) {
                int nx = x + direction[0];
                int ny = y + direction[1];

                if (nx > 0 && nx < mazeWidth - 2 && ny > 0 && ny < mazeHeight - 2 && maze[nx][ny] == WALL) {
                    maze[nx][ny] = FLOOR;
                    maze[nx + 1][ny] = FLOOR;
                    maze[nx][ny + 1] = FLOOR;
                    maze[nx + 1][ny + 1] = FLOOR;
                    maze[x + direction[0] / 3][y + direction[1] / 3] = FLOOR;
                    maze[x + direction[0] / 3 + 1][y + direction[1] / 3] = FLOOR;
                    maze[x + direction[0] / 3][y + direction[1] / 3 + 1] = FLOOR;
                    maze[x + direction[0] / 3 + 1][y + direction[1] / 3 + 1] = FLOOR;
                    stack.push(new int[]{nx, ny});
                    moved = true;
                    break;
                }
            }
            if (!moved) {
                stack.pop();
            }
        }
    }

    private void shuffleArray(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    protected void initializeTiles() {
        List<Tile> tiles = getTiles();
        tiles.clear();

        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (maze[i][j] == WALL) {
                    tiles.add(new WallTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, wallTexture));
                } else {
                    tiles.add(new FloorTile((getX() + i) * Constants.TILE_SIZE, (getY() + j) * Constants.TILE_SIZE, floorTexture));
                }
            }
        }
    }
}