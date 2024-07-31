package com.mygdx.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Zone;
import com.mygdx.game.world.rooms.*;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {
    private List<Room> rooms;
    private List<Tile> hallwayTiles;
    private List<Enemy> enemies;
    private Texture floorTexture, wallTexture, hallwayTexture, entryTexture;
    private Random rand;
    private Map map;
    private int startX;
    private int startY;
    private OrthographicCamera camera;

    //Variables used for map generation
    private int minRoomWidth = 30;
    private int minRoomHeight = 30;
    private int numRooms = 4;
    private double hallReduction = 1.1;

    public DungeonGenerator(OrthographicCamera camera) {
        this.camera = camera;
        rooms = new ArrayList<>();
        hallwayTiles = new ArrayList<>();
        rand = new Random();
        floorTexture = new Texture("testFloor.png");
        wallTexture = new Texture("testWall.png");
        hallwayTexture = new Texture("testHallway.png");
        entryTexture = new Texture("testSafe.png");
        map = new Map();
        enemies = new ArrayList<>();
    }

    public void generateDungeon(int width, int height) {

        initializeDungeon(width, height);

        // Define zones
        Zone mainZone = new Zone(0, 0, width, height);

        // Generate rooms
        generateRooms(mainZone);

        // Populate the map with the generated rooms
        populateMap();

        // Generate hallways between rooms
        generateHallways();

        // Populate the map again with hallways to ensure they can override room tiles if necessary
        populateMapWithHallways();

        placeEnemies();

        //map.cleanUpIsolatedWalls();
    }

    private void initializeDungeon(int width, int height) {
        // Initialization
    }

    private void generateRooms(Zone zone) {
        // Start with the first room
        Room startRoom = new StartRoom(0, 0, 7, 7, floorTexture, wallTexture, entryTexture);
        rooms.add(startRoom);

        startX = ((StartRoom) startRoom).getEntryX();
        startY = ((StartRoom) startRoom).getEntryY();

        int lastWidth = 0;
        int lastHeight = 0;

        // Generate other rooms
        for (int i = 1; i < numRooms; i++) {
            Room newRoom = null;
            boolean roomPlaced = false;

            // Attempt to place the new room
            while (!roomPlaced) {
                Room baseRoom = rooms.get(rand.nextInt(rooms.size()));
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
                        System.out.println("end room");
                        newRoom = new EndRoom(newX, newY, 7, 7, floorTexture, wallTexture, entryTexture);
                    }

                    // Check if the room intersects with any existing rooms
                    if (!intersectsAnyRoom(newRoom)) {
                        roomPlaced = true;
                        break;
                    }
                }
            }

            if (newRoom != null) {
                rooms.add(newRoom);
                lastWidth = newRoom.getWidth();
                lastHeight = newRoom.getHeight();
            }
        }




    }

    private Room generateRoom(int x, int y) {
        int roomWidth = minRoomWidth + rand.nextInt(minRoomWidth);
        int roomHeight = minRoomHeight + rand.nextInt(minRoomHeight);
        int roomType = rand.nextInt(4);
        if(roomType == 0){
            return new ClusteredRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

        } else if (roomType == 1) {
            return new MazeRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

        } else if (roomType == 2) {
            return new NaturalRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

        } else if (roomType == 3) {
            return new CircularRoom(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

        }
        return new Room(x, y, roomWidth, roomHeight, floorTexture, wallTexture);

    }

    private boolean intersectsAnyRoom(Room newRoom) {
        for (Room room : rooms) {
            if (intersects(room, newRoom)) {
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
        // Generate hallways between rooms
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room roomA = rooms.get(i);
            Room roomB = rooms.get(i + 1);
            connectRooms(roomA, roomB);
        }
    }


    private void connectRooms(Room roomA, Room roomB) {
        int hallwayWidth = 2; // Set hallway width to 2 tiles

        // Determine the middle points of the rooms
        int roomACenterX = roomA.getX() + roomA.getWidth() / 2;
        int roomACenterY = roomA.getY() + roomA.getHeight() / 2;
        int roomBCenterX = roomB.getX() + roomB.getWidth() / 2;
        int roomBCenterY = roomB.getY() + roomB.getHeight() / 2;

        // Generate vertical hallway from roomA center Y to roomB center Y
        generateVerticalHallway(roomACenterX, roomACenterY, roomACenterX, roomBCenterY, hallwayWidth);

        // Generate horizontal hallway from roomA center X to roomB center X
        generateHorizontalHallway(roomACenterX, roomBCenterY, roomBCenterX, roomBCenterY, hallwayWidth);
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
        // Add all the tiles from the rooms to the map
        for (Room room : rooms) {
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

    private void placeEnemies() {
        List<int[]> potentialPositions = new ArrayList<>();

        // Identify potential positions adjacent to hallways
        for (Tile hallwayTile : hallwayTiles) {
            int x = (int) (hallwayTile.getX() / Constants.TILE_SIZE);
            int y = (int) (hallwayTile.getY() / Constants.TILE_SIZE);

            // Check adjacent positions
            checkAndAddPosition(potentialPositions, x + 1, y);
            checkAndAddPosition(potentialPositions, x - 1, y);
            checkAndAddPosition(potentialPositions, x, y + 1);
            checkAndAddPosition(potentialPositions, x, y - 1);
        }

        // Randomly select positions to place enemies
        int numEnemies = 100; // Adjust the number of enemies as needed
        for (int i = 0; i < numEnemies && !potentialPositions.isEmpty(); i++) {
            int index = rand.nextInt(potentialPositions.size());
            int[] position = potentialPositions.remove(index);
            float x = position[0] * Constants.TILE_SIZE;
            float y = position[1] * Constants.TILE_SIZE;

            // Create and add the enemy
            BasicEnemy enemy = new BasicEnemy(x, y, 100, camera); // Adjust enemy parameters as needed
            enemies.add(enemy);
        }
    }

    private void checkAndAddPosition(List<int[]> positions, int x, int y) {
        if (!map.isHallwayTile(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE) &&
                !map.isWallTile(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE) &&
                !map.isRoomTile(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE)) {
            positions.add(new int[]{x, y});
        }
    }

    public Map getMap() {
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

    public List<Room> getRooms() {
        return  rooms;
    }
    public List<Enemy> getEnemies() {
        return enemies;
    }
}



//// Generate other rooms
//int numRooms = 10;
//        for (int i = 1; i < numRooms; i++) {
//Room newRoom = null;
//boolean roomPlaced = false;
//
//// Attempt to place the new room
//            while (!roomPlaced) {
//Room baseRoom = rooms.get(rand.nextInt(rooms.size()));
//int gapX = Constants.MIN_ROOM_GAP + rand.nextInt(Constants.MAX_ROOM_GAP - Constants.MIN_ROOM_GAP + 1);
//int gapY = Constants.MIN_ROOM_GAP + rand.nextInt(Constants.MAX_ROOM_GAP - Constants.MIN_ROOM_GAP + 1);
//
//// Determine new room position
//int newX = baseRoom.getX() + baseRoom.getWidth() + gapX;
//int newY = baseRoom.getY() + baseRoom.getHeight() + gapY;
//
//// Try placing the room at different positions around the base room
//                for (int j = 0; j < 4; j++) {
//        if (j == 1) {
//newX = baseRoom.getX() - gapX - rand.nextInt(5);
//                    } else if (j == 2) {
//newY = baseRoom.getY() - gapY - rand.nextInt(5);
//                    } else if (j == 3) {
//newX = baseRoom.getX() + gapX + rand.nextInt(5);
//                    }
//
//                            if(i < numRooms - 1){
//newRoom = generateRoom(newX, newY);
//                    } else {
//                            System.out.println("end room");
//newRoom = new EndRoom(newX, newY, 7, 7, floorTexture, wallTexture, entryTexture);
//                    }
//
//                            // Check if the room intersects with any existing rooms
//                            if (!intersectsAnyRoom(newRoom)) {
//            roomPlaced = true;
//        break;
//        }
//        }
//        }
//
//        if (newRoom != null) {
//        rooms.add(newRoom);
//            }
//                    }