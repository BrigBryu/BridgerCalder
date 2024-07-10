package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.DungeonGenerator;
import com.mygdx.game.world.MapRenderer;
import com.mygdx.game.world.rooms.Room;

import java.util.ArrayList;
import java.util.List;

public class DungeonScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private MapRenderer mapRenderer;
    private Player player;
    private DungeonGenerator dungeonGenerator;
    private List<Enemy> enemies;


    public DungeonScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.dungeonGenerator = new DungeonGenerator();
        this.dungeonGenerator.generateDungeon(50, 50); // Example dimensions for the dungeon
        this.mapRenderer = new MapRenderer(dungeonGenerator.getMap());
        this.player = new Player(dungeonGenerator.getStartX() * Constants.TILE_SIZE, dungeonGenerator.getStartY() * Constants.TILE_SIZE); // Set initial position of the player within the dungeon

        // Initialize enemies
        enemies = new ArrayList<>();
        initializeEnemies();
    }

    private void initializeEnemies() {
        BasicEnemy enemy = new BasicEnemy(Constants.TILE_SIZE * 5, Constants.TILE_SIZE * 5, 100);
        enemies.add(enemy);

        // Add more enemies as needed
    }

    @Override
    public void show() {
        // Ensure the viewport is set correctly when the screen is shown
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear the screen with a black color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        mapRenderer.render(batch);
        player.render(batch); // Render the player
        for (Enemy enemy : enemies) {
            enemy.render(batch, ((BasicEnemy) enemy).getDirection()); // Render each enemy with its direction
        }
        batch.end();
    }

    private void update(float delta) {
        checkUserInput(delta);
        updatePlayer(delta);
        updateEnemies(delta);
        updateCamera();
    }

    private void updatePlayer(float delta) {
        updatePlayerSpeed();
    }

    private void checkUserInput(float delta) {
        player.update(delta, dungeonGenerator.getMap().getWallTiles(), enemies);
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
    }

    private void updateCamera() {
        camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        // Adjust the camera viewport size based on the window size
        camera.viewportWidth = (float) (width * Constants.TILE_SIZE / 2) / 22; // should be based on tile size
        camera.viewportHeight = (float) (height * Constants.TILE_SIZE / 2) / 22;
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        dungeonGenerator.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }

    public boolean isPlayerInRoom() {
        int playerX = (int) player.getX();
        int playerY = (int) player.getY();
        return mapRenderer.getMap().isInRoom(playerX, playerY, dungeonGenerator.getRooms());
    }

    public void updatePlayerSpeed() {
        if (isPlayerInRoom()) {
            System.out.println("in room");
            player.resetSpeed(); // Reset to original speed if in a room
        } else {
            player.setOutOfRoomSpeed(); // Double the speed if not in a room
            System.out.println("not in room");

        }
    }

}
