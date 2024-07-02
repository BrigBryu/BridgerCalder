package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Zone;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {
    private List<Room> rooms;
    private List<Tile> hallwayTiles;
    private Texture floorTexture, wallTexture, hallwayTexture, entryTexture;
    private Random rand;
    private Map map;
    private int startX;
    private int startY;
    private int minRoomWidth = 10;
    private int minRoomHeight = 10;
    private int minRoomDistApart = 2;

    public DungeonGenerator() {
        rooms = new ArrayList<>();
        hallwayTiles = new ArrayList<>();
        rand = new Random();
        floorTexture = new Texture("testFloor.png");
        wallTexture = new Texture("testWall.png");
        hallwayTexture = new Texture("testHallway.png");
        entryTexture = new Texture("testSafe.png");
        map = new Map();
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
    }

    private void initializeDungeon(int width, int height) {
        // Initialization logic
    }

    private void generateRooms(Zone zone) {
        // Start with the first room
        Room startRoom = new StartRoom(0, 0, 7, 7, floorTexture, wallTexture, entryTexture);
        rooms.add(startRoom);

        startX = ((StartRoom) startRoom).getEntryX();
        startY = ((StartRoom) startRoom).getEntryY();

        // Generate subsequent rooms
        for (int i = 1; i < 10; i++) {
            Room newRoom = null;
            boolean roomPlaced = false;

            // Attempt to place the new room
            while (!roomPlaced) {
                Room baseRoom = rooms.get(rand.nextInt(rooms.size()));
                int gapX = Constants.MIN_ROOM_GAP + rand.nextInt(Constants.MAX_ROOM_GAP - Constants.MIN_ROOM_GAP + 1);
                int gapY = Constants.MIN_ROOM_GAP + rand.nextInt(Constants.MAX_ROOM_GAP - Constants.MIN_ROOM_GAP + 1);

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

                    newRoom = generateRoom(newX, newY);

                    // Check if the room intersects with any existing rooms
                    if (!intersectsAnyRoom(newRoom)) {
                        roomPlaced = true;
                        break;
                    }
                }
            }

            if (newRoom != null) {
                rooms.add(newRoom);
            }
        }
    }

    private Room generateRoom(int x, int y) {
        int roomWidth = minRoomWidth + rand.nextInt(minRoomWidth);
        int roomHeight = minRoomHeight + rand.nextInt(minRoomHeight);
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
}


//private void connectRooms(Room roomA, Room roomB) {
//    int hallwayWidth = 2; // Set hallway width to 2 tiles
//
//    // Determine the edges of the rooms
//    int roomAEdgeXLeft = roomA.getX();
//    int roomAEdgeXRight = roomA.getX() + roomA.getWidth();
//    int roomAEdgeYBottom = roomA.getY();
//    int roomAEdgeYTop = roomA.getY() + roomA.getHeight();
//
//    int roomBEdgeXLeft = roomB.getX();
//    int roomBEdgeXRight = roomB.getX() + roomB.getWidth();
//    int roomBEdgeYBottom = roomB.getY();
//    int roomBEdgeYTop = roomB.getY() + roomB.getHeight();
//
//    //generateHallway(int startX, int startY, int endX, int endY, int hallwayWidth)
//    // Generate horizontal hallway
//    if (roomAEdgeXRight <= roomBEdgeXLeft) {
//        // Left-to-right
//        generateHorizontalHallway(roomAEdgeXRight, roomAEdgeYBottom + (roomA.getHeight() / 2) - 1, roomBEdgeXLeft, roomBEdgeYBottom + (roomB.getHeight() / 2) - 1, hallwayWidth);
//    } else {
//        // Right-to-left
//        generateHorizontalHallway(roomAEdgeXLeft, roomAEdgeYBottom + (roomA.getHeight() / 2) - 1, roomBEdgeXRight, roomBEdgeYBottom + (roomB.getHeight() / 2) - 1, hallwayWidth);
//    }
//
//    // Generate vertical hallway
//    if (roomAEdgeYTop <= roomBEdgeYBottom) {
//        // Top-to-bottom
//        generateVerticalHallway(roomAEdgeXLeft + (roomA.getWidth() / 2) - 1, roomAEdgeYTop, roomBEdgeXLeft + (roomB.getWidth() / 2) - 1, roomBEdgeYBottom, hallwayWidth);
//    } else {
//        // Bottom-to-top
//        generateVerticalHallway(roomAEdgeXLeft + (roomA.getWidth() / 2) - 1, roomAEdgeYBottom, roomBEdgeXLeft + (roomB.getWidth() / 2) - 1, roomBEdgeYTop, hallwayWidth);
//    }
//}
