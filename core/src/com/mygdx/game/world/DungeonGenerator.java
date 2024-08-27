package com.mygdx.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Boot;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.rooms.*;
import com.mygdx.game.world.tiles.FloorTile;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Make the dungeon then should be able to be null.
 * The rest is handled in the map with a 2d array and a list of hitboxes to check
 */
public class DungeonGenerator {
    private List<Room> generalRooms;
    private List<Room> specialRooms;
    private List<Tile> hallwayTiles;
    private List<Enemy> enemies;

    private Texture floorTexture, wallTexture, hallwayTexture, entryTexture;
    private Random rand;
    private GameMap map;
    private int startX;
    private int startY;
    private OrthographicCamera camera;
    private Boot game;

    //Variables used for map generation
    /**
     * Both width and height should not be under 4 because hallways can merge together creating holes
     */
    private int minRoomWidth = 20;
    /**
     * Both width and height should not be under 4 because hallways can merge together creating holes
     */
    private int minRoomHeight = 20;
    /**
     * Must be greater than or equal to 3 otherwise generateHallways throws IndexOutOfBoundsException
     */
    private int numRooms = 3;
    /**
     * Is the number of rooms from the last that are considered for baseRoom when generating.
     * The index of that room is numberOfRooms - rand.nextInt(closerEndChance)
     * Higher considers more rooms from the end
     */
    private int closerEndChance = 2;
    /**
     * Not sure if it should be an option to change
     * It is kinda confusing and might not be fun to change ether way
     */
    private double hallReduction = 1.1;


    public DungeonGenerator(OrthographicCamera camera, Boot game) {
        this.camera = camera;
        generalRooms = new ArrayList<>();
        specialRooms = new ArrayList<>();
        hallwayTiles = new ArrayList<>();
        rand = new Random();
        floorTexture = new Texture("testFloor.png");
        wallTexture = new Texture("testWall.png");
        hallwayTexture = new Texture("testHallway.png");
        entryTexture = new Texture("testSafe.png");
        map = new GameMap();
        enemies = new ArrayList<>();
        this.game = game;
    }

    /**
     * Generates rooms
     * Populate map with rooms
     * Generate Hallways
     * Populate map with halls
     * Place Enemies
     * Convert to map to tileMap from lists of tiles
     */
    public void generateDungeon() {

        // Generate rooms
        generateRooms();

        // Populate the map with the generated rooms
        populateMap();

        // Generate hallways between rooms
        generateHallways();

        // Populate the map again with hallways to ensure they can override room tiles if necessary
        populateMapWithHallways();

        markSpawnableTiles();

        placeEnemies();

        validateEnemyPositions();


        //map.cleanUpIsolatedWalls();

    }

    private void generateRooms() {
        // Start with the first room
        Room startRoom = new StartRoom(0, 0, 9, 9, floorTexture, wallTexture, entryTexture);
        specialRooms.add(startRoom);

        startX = ((StartRoom) startRoom).getEntryX();
        startY = ((StartRoom) startRoom).getEntryY();

        int lastWidth = 0;
        int lastHeight = 0;
        boolean firstGeneralRoomPlaced = true;

        // Generate other rooms
        for (int i = 1; i < numRooms; i++) {
            Room newRoom = null;
            boolean roomPlaced = false;
            boolean endRoomPlaced = false;

            // Attempt to place the new room

            while (!roomPlaced) {

                //If the first general room has not been placed baseRoom should be the start room otherwise baseRoom is random
                Room baseRoom = null;
                if(firstGeneralRoomPlaced){
                    baseRoom = startRoom;
                    firstGeneralRoomPlaced = false;
                } else {
                    int indexChange = rand.nextInt(closerEndChance);
                    //If its out of bounds do the most recent
                    if(generalRooms.size() - indexChange >= generalRooms.size() - 1){
                        baseRoom = generalRooms.get(generalRooms.size() - 1);
                    } else {
                        baseRoom = generalRooms.get(generalRooms.size() - indexChange);
                    }
                }

                int gapX = Constants.MIN_ROOM_GAP + rand.nextInt(Constants.MAX_ROOM_GAP - Constants.MIN_ROOM_GAP + 1);
                int gapY = Constants.MIN_ROOM_GAP + rand.nextInt(Constants.MAX_ROOM_GAP - Constants.MIN_ROOM_GAP + 1);


                //Try to reduce hall length
                if(gapX > lastWidth){
                    gapX = (int) (gapX * ((double) lastWidth / (gapX * hallReduction)));
                }
                if(gapY > lastHeight){
                    //gapY *= (lastWidth - gapY);
                    gapY = (int) (gapY * ((double) lastWidth / (gapY * hallReduction)));

                }

                // Determine new room position
                int newX = baseRoom.getX() + baseRoom.getWidth() + gapX;
                int newY = baseRoom.getY() + baseRoom.getHeight() + gapY;

                // Try placing the room at different positions around the base room
                for (int j = 0; j < 4; j++) {
                    if (j == 1) {
                        newX = baseRoom.getX() - gapX - rand.nextInt(5);
                    } else if (j == 2) {
                        newY = baseRoom.getY() - gapY - rand.nextInt(5);
                    } else if (j == 3) {
                        newX = baseRoom.getX() + gapX + rand.nextInt(5);
                    }

                    if(i < numRooms - 1){
                        newRoom = generateRoom(newX, newY);
                    } else {
                        endRoomPlaced = true;
                        newRoom = new EndRoom(newX, newY, 9, 9, floorTexture, wallTexture, entryTexture, camera, game);
                    }

                    // Check if the room intersects with any existing rooms
                    if (!intersectsAnyOfBothRoom(newRoom)) {
                        roomPlaced = true;
                        break;
                    }
                }
            }

            //catch when the end room is placed
            if (endRoomPlaced && newRoom != null) {
                specialRooms.add(newRoom);
                lastWidth = newRoom.getWidth();
                lastHeight = newRoom.getHeight();

            //put the rest in the general rooms
            } else if (newRoom != null) {
                generalRooms.add(newRoom);
                lastWidth = newRoom.getWidth();
                lastHeight = newRoom.getHeight();
            }
        }




    }

    private Room generateRoom(int x, int y) {
        int roomWidth = minRoomWidth + rand.nextInt(minRoomWidth);
        int roomHeight = minRoomHeight + rand.nextInt(minRoomHeight);
        int roomType = rand.nextInt(4);
        //TODO
        return new NaturalRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

//        if(roomType == 0){
//            return new ClusteredRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);
//        } else if (roomType == 1) {
//            return new MazeRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);
//        } else if (roomType == 2) {
//            return new NaturalRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);
//
//        } else if (roomType == 3) {
//            return new CircularRoom(x, y, roomWidth, floorTexture, wallTexture);
//        }
//        return new Room(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

    }

    private boolean intersectsAnyOfBothRoom(Room newRoom) {
        return intersectsGeneralRoom(newRoom) && intersectsSpecialRoom(newRoom);
    }

    private boolean intersectsGeneralRoom(Room newRoom) {
        for (Room room : generalRooms) {
            if (intersects(room, newRoom)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsGeneralRoom(int x, int y) {
        for (Room room : generalRooms) {
            if (room.intersects(x,y)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsSpecialRoom(Room newRoom) {
        for (Room room : specialRooms) {
            if (intersects(room, newRoom)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersectsSpecialRoom(int x, int y) {
        for (Room room : specialRooms) {
            if (room.intersects(x,y)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersects(Room roomA, Room roomB) {
        return roomA.getX() < roomB.getX() + roomB.getWidth() &&
                roomA.getX() + roomA.getWidth() > roomB.getX() &&
                roomA.getY() < roomB.getY() + roomB.getHeight() &&
                roomA.getY() + roomA.getHeight() > roomB.getY();
    }

    private void generateHallways() {
        int limitedHallDepth = 4;
        // Generate hallways between rooms
        for (int i = 0; i < generalRooms.size() - 1; i++) {
            Room roomA = generalRooms.get(i);
            Room roomB = generalRooms.get(i + 1);
            boolean limitA = roomA instanceof MazeRoom;
            boolean limitB = roomB instanceof MazeRoom;
            //RoomA and RoomB limit depth
            if(limitA && limitB) {
                connectRooms(roomA, roomB, limitedHallDepth, limitedHallDepth);
            //RoomA limit depth
            } else if(limitA) {
                connectRooms(roomA, roomB, limitedHallDepth, -1);
            //RoomB limit dpeth
            } else if (limitB) {
                connectRooms(roomA, roomB, -1, limitedHallDepth);
            //Both normal depth
            } else {
                connectRooms(roomA, roomB, -1, -1);
            }
        }

        // Generate hallways for start and end rooms
        Room roomAfterStart = generalRooms.get(0);
        Room roomBeforeEnd = generalRooms.get(generalRooms.size() - 1);
        boolean limitStart = roomAfterStart instanceof MazeRoom;
        boolean limitEnd = roomBeforeEnd instanceof MazeRoom;
        //RoomA and RoomB limit depth
        if(limitStart && limitEnd) {
            connectRooms(roomAfterStart, specialRooms.get(0), limitedHallDepth, limitedHallDepth);
            connectRooms(roomBeforeEnd, specialRooms.get(specialRooms.size() - 1), limitedHallDepth, limitedHallDepth - 1);
            //RoomA limit depth
        } else if(limitStart) {
            connectRooms(roomAfterStart, specialRooms.get(0), limitedHallDepth, limitedHallDepth);
            connectRooms(roomBeforeEnd, specialRooms.get(specialRooms.size() - 1), -1, limitedHallDepth - 1);
            //RoomB limit depth
        } else if (limitEnd) {
            connectRooms(roomAfterStart, specialRooms.get(0), -1, limitedHallDepth);
            connectRooms(roomBeforeEnd, specialRooms.get(specialRooms.size() - 1), limitedHallDepth, limitedHallDepth - 1);
            //Both normal depth
        } else {
            connectRooms(roomAfterStart, specialRooms.get(0), -1, limitedHallDepth);
            connectRooms(roomBeforeEnd, specialRooms.get(specialRooms.size() - 1), -1, limitedHallDepth - 1);
        }
    }

    /**
     * Connects roomA and roomB with hallways.
     * If depth is -1, connects rooms with a continuous hallway.
     * Otherwise, the connecting hallway will go into each room depth number of tiles.
     * @param roomA the first room to connect
     * @param roomB the second room to connect
     * @param depthA depth of hallway into roomA
     * @param depthB depth of hallway into roomB
     */
    private void connectRooms(Room roomA, Room roomB, int depthA, int depthB) {
        int hallwayWidth = 2; // Set hallway width to 2 tiles

        // find mid points
        int roomACenterX = roomA.getX() + roomA.getWidth() / 2;
        int roomACenterY = roomA.getY() + roomA.getHeight() / 2;
        int roomBCenterX = roomB.getX() + roomB.getWidth() / 2;
        int roomBCenterY = roomB.getY() + roomB.getHeight() / 2;

        int hallwayStartX = roomACenterX;
        int hallwayStartY = roomACenterY;
        int hallwayEndX = roomBCenterX;
        int hallwayEndY = roomBCenterY;

        // find if roomB is to the left or right of roomA
        if (roomBCenterX < roomACenterX) {  // roomB is to the left of roomA
            if (depthA != -1) {
                hallwayStartX = roomA.getX() + depthA;
            }
            if (depthB != -1) {
                hallwayEndX = roomB.getX() + roomB.getWidth() - depthB;
            }
        } else if (roomBCenterX > roomACenterX) {  // roomB is to the right of roomA
            if (depthA != -1) {
                hallwayStartX = roomA.getX() + roomA.getWidth() - depthA;
            }
            if (depthB != -1) {
                hallwayEndX = roomB.getX() + depthB;
            }
        }

        // find if roomB is above or below roomA
        if (roomBCenterY < roomACenterY) {  // roomB is below roomA
            if (depthA != -1) {
                hallwayStartY = roomA.getY() + depthA;
            }
            if (depthB != -1) {
                hallwayEndY = roomB.getY() + roomB.getHeight() - depthB;
            }
        } else if (roomBCenterY > roomACenterY) {  // roomB is above roomA
            if (depthA != -1) {
                hallwayStartY = roomA.getY() + roomA.getHeight() - depthA;
            }
            if (depthB != -1) {
                hallwayEndY = roomB.getY() + depthB;
            }
        }

        // Generate hallways based on the positions
        if (hallwayStartX != hallwayEndX) {
            generateHorizontalHallway(hallwayStartX, hallwayStartY, hallwayEndX, hallwayStartY, hallwayWidth);
        }

        if (hallwayStartY != hallwayEndY) {
            generateVerticalHallway(hallwayEndX, hallwayStartY, hallwayEndX, hallwayEndY, hallwayWidth);
        }
    }


    private void generateHorizontalHallway(int startX, int startY, int endX, int endY, int hallwayWidth) {
        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX) + 2; x++) {
            if(x <= Math.max(startX, endX) + 1) {
                for (int w = 0; w < hallwayWidth; w++) {
                    // Add hallway tiles
                    Tile hall = new HallwayTile(x * Constants.TILE_SIZE, (startY + w) * Constants.TILE_SIZE, hallwayTexture);
                    map.addTileDestructiveBoth(hall);
                    hallwayTiles.add(hall);
                }
            }
            // Add walls on the top and bottom of the hallway
            WallTile wallAbove = new WallTile(x * Constants.TILE_SIZE, (startY - 1) * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallTexture);
            WallTile wallBelow = new WallTile(x * Constants.TILE_SIZE, (startY + hallwayWidth) * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallTexture);

            // Check if there's a hallway on the other side of the wall
            boolean hallAbove = map.isHallwayTile(x * Constants.TILE_SIZE, (startY - 2) * Constants.TILE_SIZE);
            boolean hallBelow = map.isHallwayTile(x * Constants.TILE_SIZE, (startY + hallwayWidth + 1) * Constants.TILE_SIZE);

            if (hallAbove) {
                // Add an extra row of hallway tiles above
                for (int w = 0; w < hallwayWidth; w++) {
                    Tile hall = new HallwayTile(x * Constants.TILE_SIZE, (startY + w - 1) * Constants.TILE_SIZE, hallwayTexture);
                    map.addTileDestructiveBoth(hall);
                    hallwayTiles.add(hall);
                }
            } else {
                map.addWallTileNondestructive(wallAbove);
            }

            if (hallBelow) {
                // Add an extra row of hallway tiles below
                for (int w = 0; w < hallwayWidth; w++) {
                    Tile hall = new HallwayTile(x * Constants.TILE_SIZE, (startY + w + hallwayWidth) * Constants.TILE_SIZE, hallwayTexture);
                    map.addTileDestructiveBoth(hall);
                    hallwayTiles.add(hall);
                }
            } else {
                map.addWallTileNondestructive(wallBelow);
            }
        }
    }

    private void generateVerticalHallway(int startX, int startY, int endX, int endY, int hallwayWidth) {
        for (int y = Math.min(startY, endY); y <= Math.max(startY, endY) + 2; y++) {
            if(y <= Math.max(startY, endY) + 1) {
                for (int w = 0; w < hallwayWidth; w++) {
                    // Add hallway tiles
                    Tile hall = new HallwayTile((startX + w) * Constants.TILE_SIZE, y * Constants.TILE_SIZE, hallwayTexture);
                    map.addTileDestructiveBoth(hall);
                    hallwayTiles.add(hall);
                }
            }
            // Add walls on the left and right of the hallway
            WallTile wallLeft = new WallTile((startX - 1) * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallTexture);
            WallTile wallRight = new WallTile((startX + hallwayWidth) * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallTexture);

            // Check if there's a hallway on the other side of the wall
            boolean hallLeft = map.isHallwayTile((startX - 2) * Constants.TILE_SIZE, y * Constants.TILE_SIZE);
            boolean hallRight = map.isHallwayTile((startX + hallwayWidth + 1) * Constants.TILE_SIZE, y * Constants.TILE_SIZE);

            if (hallLeft) {
                // Add an extra row of hallway tiles to the left
                for (int w = 0; w < hallwayWidth; w++) {
                    Tile hall = new HallwayTile((startX + w - 1) * Constants.TILE_SIZE, y * Constants.TILE_SIZE, hallwayTexture);
                    map.addTileDestructiveBoth(hall);
                    hallwayTiles.add(hall);
                }
            } else {
                map.addWallTileNondestructive(wallLeft);
            }

            if (hallRight) {
                // Add an extra row of hallway tiles to the right
                for (int w = 0; w < hallwayWidth; w++) {
                    Tile hall = new HallwayTile((startX + w + hallwayWidth) * Constants.TILE_SIZE, y * Constants.TILE_SIZE, hallwayTexture);
                    map.addTileDestructiveBoth(hall);
                    hallwayTiles.add(hall);
                }
            } else {
                map.addWallTileNondestructive(wallRight);
            }
        }
    }

    private void populateMap() {
        // Add all the tiles from the both room types to the map
        for (Room room : generalRooms) {
            for (Tile tile : room.getTiles()) {
                map.addTile(tile);
            }
        }
        for (Room room : specialRooms) {
            for (Tile tile : room.getTiles()) {
                map.addTile(tile);
            }
        }
    }

    private void populateMapWithHallways() {
        for (Tile hallwayTile : hallwayTiles) {
            map.addTileDestructiveBoth(hallwayTile);
        }
    }

    /**
     * Marks all tiles that can potentially spawn enemies.
     */
    public void markSpawnableTiles() {
        for (Room room : generalRooms) {
            for (Tile tile : room.getTiles()) {
                if (tile instanceof FloorTile) {
                    tile.setCanSpawn(true);
                }
            }
        }
    }

    private void placeEnemies() {
        List<Tile> spawnableTiles = new ArrayList<>();

        // use tiles that are  spawnable in general rooms only
        for (Room room : generalRooms) {
            for (Tile tile : room.getTiles()) {
                if (tile.getCanSpawn() && tile instanceof FloorTile) {
                    spawnableTiles.add(tile);
                }
            }
        }

        //TODO can change
        int numEnemies = spawnableTiles.size() / 15;
        for (int i = 0; i < numEnemies && !spawnableTiles.isEmpty(); i++) {
            int index = rand.nextInt(spawnableTiles.size());
            Tile spawnTile = spawnableTiles.remove(index);
            float x = spawnTile.getX();
            float y = spawnTile.getY();

            BasicEnemy enemy = new BasicEnemy(x, y, 100, camera, map);

            if (!isIntersectingWall(enemy.getHitBox()) && !isIntersectingHall(enemy.getHitBox())) {
                enemies.add(enemy);
            }
        }
    }

    private boolean isIntersectingHall(HitBox enemyHitBox) {
        for (Room room : getRooms()) {
            for (Tile tile : room.getTiles()) {
                if (tile instanceof HallwayTile) {
                    HitBox hallHitBox = new HitBox(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
                    if (enemyHitBox.overlaps(hallHitBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isIntersectingWall(HitBox enemyHitBox) {
        for (Room room : getRooms()) {
            for (Tile tile : room.getTiles()) {
                if (tile instanceof WallTile) {
                    HitBox wallHitBox = new HitBox(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
                    if (enemyHitBox.overlaps(wallHitBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    private void validateEnemyPositions() {
        for (Enemy enemy : enemies) {
            int x = (int) (enemy.getX() / Constants.TILE_SIZE);
            int y = (int) (enemy.getY() / Constants.TILE_SIZE);
            Tile tile = tileAt(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE);

            if (!(tile instanceof FloorTile)) {
                System.err.println("Enemy placed on non-FloorTile at (" + x + ", " + y + ")");
            }
        }
    }


    private Tile tileAt(int x, int y) {
        // Normalize
        x = (x / Constants.TILE_SIZE) * Constants.TILE_SIZE;
        y = (y / Constants.TILE_SIZE) * Constants.TILE_SIZE;

        for (Room room : specialRooms) {
            if (room.contains(x, y)) {
                for (Tile tile : room.getTiles()) {
                    if (tile.getX() == x && tile.getY() == y) {
                        return tile;
                    }
                }
            }
        }

        for (Room room : generalRooms) {
            if (room.contains(x, y)) {
                for (Tile tile : room.getTiles()) {
                    if (tile.getX() == x && tile.getY() == y) {
                        return tile;
                    }
                }
            }
        }
        return null;
    }


    private void checkAndAddPosition(List<int[]> positions, int x, int y) {
        Tile tile = tileAt(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE);

        // Check if the tile is a FloorTile and is not part of a hallway or wall
        if (tile instanceof FloorTile &&
                !map.isHallwayTile(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE) &&
                !map.isWallTile(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE)) {

            if (!isOnRoomEdge(x, y)) {
                positions.add(new int[]{x, y});
            }
        }
    }

    private boolean isOnRoomEdge(int x, int y) {
        for (Room room : generalRooms) {
            // Get room boundaries in tile coordinates
            int roomX = room.getX();
            int roomY = room.getY();
            int roomWidth = room.getWidth();
            int roomHeight = room.getHeight();


            int tileBorderSize = 2;
            boolean withinLeftEdge = x >= roomX - tileBorderSize && x <= roomX + tileBorderSize - 1;
            boolean withinRightEdge = x >= roomX + roomWidth - 3 && x <= roomX + roomWidth + tileBorderSize - 1;
            boolean withinTopEdge = y >= roomY - tileBorderSize && y <= roomY + tileBorderSize - 1;
            boolean withinBottomEdge = y >= roomY + roomHeight - tileBorderSize && y <= roomY + roomHeight + tileBorderSize - 1;

            if (withinLeftEdge || withinRightEdge || withinTopEdge || withinBottomEdge) {
                return true;
            }
        }
        return false;
    }

    public GameMap getMap() {
        return map;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        hallwayTexture.dispose();
        entryTexture.dispose();
        map.dispose();
    }

    public List<Room> getRoomsExcludingStartRoom() {
        List<Room> rooms = new ArrayList<>();
        rooms.addAll(generalRooms);
        //Exclude that start room
        boolean skippedStartRoom = false;
        for(Room room:specialRooms) {
            if(!skippedStartRoom) {
                skippedStartRoom = true;
            } else {
                rooms.add(room);
            }
        }

        return rooms;
    }

    public List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();
        rooms.addAll(generalRooms);
        rooms.addAll(specialRooms);

        return rooms;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}