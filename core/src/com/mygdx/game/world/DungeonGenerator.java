package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Zone;
import com.mygdx.game.world.tiles.HallwayTile;
import com.mygdx.game.world.tiles.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {
    private List<Room> rooms;
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

        // Generate rooms and hallways
        generateRooms(mainZone);
        generateHallways();

        // Populate the map with the generated rooms and paths
        populateMap();
    }

    private void initializeDungeon(int width, int height) {
        // Initialization logic cant think of any
    }

    private void generateRooms(Zone zone) {
        // Start with the first room
        Room startRoom = new StartRoom(0, 0, 7, 7, floorTexture, wallTexture, entryTexture);
        rooms.add(startRoom);

        startX = ((StartRoom)startRoom).getEntryX();
        startY = ((StartRoom)startRoom).getEntryY();

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
        // Connect two rooms with a hallway
        int startX = MathUtils.floor(roomA.getX() + roomA.getWidth() / 2);
        int startY = MathUtils.floor(roomA.getY() + roomA.getHeight() / 2);
        int endX = MathUtils.floor(roomB.getX() + roomB.getWidth() / 2);
        int endY = MathUtils.floor(roomB.getY() + roomB.getHeight() / 2);

//        if((endX - startX > minRoomWidth && endY - startY > minRoomWidth) || (endY - startY > minRoomWidth && endX - startX > minRoomWidth)){
//            System.out.println("Making hall not crazy");
//            for(int i = 0; i < 5; i++){
//                int roomWidth = minRoomWidth + rand.nextInt(endX - startX - (minRoomWidth + minRoomDistApart));
//                int roomHeight = minRoomWidth + rand.nextInt(endX - startX - (minRoomHeight + minRoomDistApart));
//                Room newRoom = new Room(endX - startX + ((i - 2) * Constants.TILE_SIZE), endY - startY + (i - 2) * Constants.TILE_SIZE, roomWidth, roomHeight, floorTexture, wallTexture);
//                // Check if the room intersects with any existing tiles
//                if (!map.intersectsWithRoom(newRoom)) {
//                    newRoom = null;
//                }
//                if (newRoom != null) {
//                   // rooms.add(newRoom);
//                    System.out.println("Success making hall not crazy");
//                }
//                }
//
//        }
        List<Tile> ignoreTiles = new ArrayList<>();
        if (startX != endX) {
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                Tile hall = new HallwayTile(x * Constants.TILE_SIZE, startY * Constants.TILE_SIZE, hallwayTexture);
                map.addTile(hall);
                ignoreTiles.add(hall);
            }
        }

        if (startY != endY) {
            for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
                Tile hall = new HallwayTile(endX * Constants.TILE_SIZE, y * Constants.TILE_SIZE, hallwayTexture);
                map.addTile(hall);
                ignoreTiles.add(hall);
            }
        }

        if ((endX - startX > minRoomWidth && endY - startY > minRoomWidth) || (endY - startY > minRoomWidth && endX - startX > minRoomWidth)) {
            System.out.println("Making hall not crazy");
            for (int i = 0; i < 5; i++) {
                int roomWidth = minRoomWidth + rand.nextInt(endX - startX - (minRoomDistApart));
                int roomHeight = minRoomWidth + rand.nextInt(endX - startX - (minRoomDistApart));
                Room newRoom = new Room(endX - startX + ((i - 2) * Constants.TILE_SIZE), endY - startY + (i - 2) * Constants.TILE_SIZE, roomWidth, roomHeight, floorTexture, wallTexture);

                // Check if the room intersects with any existing tiles
                if (!map.intersectsWithRoom(newRoom, ignoreTiles)) {
                    newRoom = null;
                }
                if (newRoom != null) {
                    // Add the tiles of the new room to the ignore list
                    rooms.add(newRoom);
                    System.out.println("Success making hall not crazy");
                }
            }
        }

    }

    private void populateMap() {
        // Add all the tiles from the rooms and hallways to the map
        for (Room room : rooms) {
            for (Tile tile : room.getTiles()) {
                map.addTile(tile);
            }
        }
    }

    public Map getMap() {
        return map;
    }

    public int getStartX(){
        return startX;
    }

    public int getStartY(){
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