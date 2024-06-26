package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.util.Zone;

import java.util.*;

public class DungeonGenerator {
    private List<Room> rooms;
    private Texture floorTexture, wallTexture;
    private Random rand = new Random();
    private RandomizationController randController;
    private Map map;  // The map object to hold the generated dungeon
    private boolean[] visited;  // To track visited rooms

    public DungeonGenerator() {
        rooms = new ArrayList<>();
        randController = new RandomizationController();
        floorTexture = new Texture("testFloor.png");
        wallTexture = new Texture("testWall.png");
        map = new Map();  // Initialize the map
    }

    public void generateDungeon(int width, int height) {
        initializeDungeon(width, height);

        // Define zones for DFS and BFS (values need to be defined based on your map size)
        Zone dfsZone = new Zone(0, 0, width / 2, height); // Example zone for DFS
        Zone bfsZone = new Zone(width / 2, 0, width / 2, height); // Example zone for BFS

        // Generate rooms in each zone
        generateRooms(dfsZone);
        generateRooms(bfsZone);

        // Connect rooms within zones
        generateDFSZone(dfsZone);
        generateBFSZone(bfsZone);

        // Optionally, ensure connectivity between zones
        //connectZones(dfsZone, bfsZone);

        // Populate the map with the generated rooms and paths
        populateMap();
    }

    private void initializeDungeon(int width, int height) {
        visited = new boolean[width * height]; // Assuming each cell in a grid could theoretically hold a room
        // More initialization logic can be added here if necessary
    }

    private void generateRooms(Zone zone) {
        // Generate rooms within a given zone
        for (int i = zone.startX; i < zone.startX + zone.width; i += 10) {
            for (int j = zone.startY; j < zone.startY + zone.height; j += 10) {
                int roomWidth = rand.nextInt(3) + 5; // Random width from 5 to 7
                int roomHeight = rand.nextInt(3) + 5; // Random height from 5 to 7
                Room room = new Room(i, j, roomWidth, roomHeight, floorTexture);
                rooms.add(room);
            }
        }
    }

    private void generateDFSZone(Zone zone) {
        Room startRoom = pickStartingRoom(rooms);
        connectRoomsDFS(startRoom);
    }

    private void generateBFSZone(Zone zone) {
        Room startRoom = pickStartingRoom(rooms);
        connectRoomsBFS(startRoom);
    }

    private Room pickStartingRoom(List<Room> rooms) {
        return rooms.get(rand.nextInt(rooms.size()));
    }

    private void connectRoomsDFS(Room startRoom) {
        Stack<Room> stack = new Stack<>();
        stack.push(startRoom);
        visited[rooms.indexOf(startRoom)] = true;

        while (!stack.isEmpty()) {
            Room currentRoom = stack.pop();
            List<Room> neighbors = getUnvisitedNeighbors(currentRoom);

            for (Room neighbor : neighbors) {
                currentRoom.connect(neighbor);
                visited[rooms.indexOf(neighbor)] = true;
                stack.push(neighbor);
            }
        }
    }

    private void connectRoomsBFS(Room startRoom) {
        Queue<Room> queue = new LinkedList<>();
        queue.add(startRoom);
        visited[rooms.indexOf(startRoom)] = true;

        while (!queue.isEmpty()) {
            Room currentRoom = queue.poll();
            List<Room> neighbors = getUnvisitedNeighbors(currentRoom);

            for (Room neighbor : neighbors) {
                currentRoom.connect(neighbor);
                visited[rooms.indexOf(neighbor)] = true;
                queue.add(neighbor);
            }
        }
    }

    public List<Room> getUnvisitedNeighbors(Room room) {
        List<Room> neighbors = new ArrayList<>();
        for (Room potentialNeighbor : rooms) {
            if (!visited[rooms.indexOf(potentialNeighbor)] && isAdjacent(room, potentialNeighbor)) {
                neighbors.add(potentialNeighbor);
            }
        }
        return neighbors;
    }

    private boolean isAdjacent(Room room, Room potentialNeighbor) {
        // Simple adjacency check, could be improved based on actual room positions and sizes
        return Math.abs(room.getX() - potentialNeighbor.getX()) < room.getWidth() + potentialNeighbor.getWidth() &&
                Math.abs(room.getY() - potentialNeighbor.getY()) < room.getHeight() + potentialNeighbor.getHeight();
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
        map.dispose();
    }
}
