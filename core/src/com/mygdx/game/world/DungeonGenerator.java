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
    private Texture floorTexture, wallTexture, hallwayTexture, safeTexture;
    private Random rand;
    private Map map;

    public DungeonGenerator() {
        rooms = new ArrayList<>();
        rand = new Random();
        floorTexture = new Texture("testFloor.png");
        wallTexture = new Texture("testWall.png");
        hallwayTexture = new Texture("testHallway.png");
        safeTexture = new Texture("testSafe.png");
        map = new Map();
    }

    public void generateDungeon(int width, int height) {
        initializeDungeon(width, height);

        // Define zones
        Zone mainZone = new Zone(0, 0, width, height);

        // Generate rooms and hallways
        generateRooms(mainZone);
        generateHallways();

        // Generate safe zones
        generateSafeZones(1 + rand.nextInt(3)); // 1-3 additional safe zones

        // Populate the map with the generated rooms and paths
        populateMap();
    }

    private void initializeDungeon(int width, int height) {
        // Initialization logic here
    }

    private void generateRooms(Zone zone) {
        // Generate rooms with floor and wall tiles within a given zone
        for (int i = 0; i < 10; i++) {
            int roomWidth = 5 + rand.nextInt(5);
            int roomHeight = 5 + rand.nextInt(5);
            int x = rand.nextInt(zone.width - roomWidth);
            int y = rand.nextInt(zone.height - roomHeight);
            Room room = new Room(x, y, roomWidth, roomHeight, floorTexture, wallTexture);
            rooms.add(room);
        }
    }

    private void generateHallways() {
        // Generate hallways between rooms
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room roomA = rooms.get(i);
            Room roomB = rooms.get(i + 1);
            connectRooms(roomA, roomB);
        }
    }

    private void generateSafeZones(int count) {
        // Generate a specified number of safe zones
        for (int i = 0; i < count; i++) {
            int roomWidth = 5;
            int roomHeight = 5;
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            Room safeZone = new Room(x, y, roomWidth, roomHeight, safeTexture, wallTexture);
            rooms.add(safeZone);
        }
    }

    private void connectRooms(Room roomA, Room roomB) {
        // Connect two rooms with a hallway
        int startX = MathUtils.floor(roomA.getX() + roomA.getWidth() / 2);
        int startY = MathUtils.floor(roomA.getY() + roomA.getHeight() / 2);
        int endX = MathUtils.floor(roomB.getX() + roomB.getWidth() / 2);
        int endY = MathUtils.floor(roomB.getY() + roomB.getHeight() / 2);

        if (startX != endX) {
            for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
                map.addTile(new HallwayTile(x * Constants.TILE_SIZE, startY * Constants.TILE_SIZE, hallwayTexture));
            }
        }

        if (startY != endY) {
            for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
                map.addTile(new HallwayTile(endX * Constants.TILE_SIZE, y * Constants.TILE_SIZE, hallwayTexture));
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

    public Map getMap() {
        return map;
    }

    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        hallwayTexture.dispose();
        safeTexture.dispose();
        map.dispose();
    }
}
