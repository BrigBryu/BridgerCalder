package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Boot;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.AssetManager;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.DungeonGenerator;
import com.mygdx.game.world.MapRenderer;

import java.util.ArrayList;
import java.util.List;

public class DungeonScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private MapRenderer mapRenderer;
    private Player player;
    private DungeonGenerator dungeonGenerator;
    private List<Enemy> enemies;
    private Boot game;


    public DungeonScreen(OrthographicCamera camera, Boot game) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.dungeonGenerator = new DungeonGenerator(camera);
        this.dungeonGenerator.generateDungeon(50, 50); // Example dimensions for the dungeon
        this.mapRenderer = new MapRenderer(dungeonGenerator.getMap());
        this.player = new Player(dungeonGenerator.getStartX() * Constants.TILE_SIZE, dungeonGenerator.getStartY() * Constants.TILE_SIZE, camera); // Set initial position of the player within the dungeon

        this.game = game;
        // Initialize enemies
        enemies = new ArrayList<>(dungeonGenerator.getEnemies());
        initializeEnemiesNotFromDungeonGenerator();
    }

    private void initializeEnemiesNotFromDungeonGenerator() {
        BasicEnemy enemy = new BasicEnemy(Constants.TILE_SIZE * 5, Constants.TILE_SIZE * 5, 100, camera);
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
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }
        player.update(delta, dungeonGenerator.getMap().getWallTiles(), enemies);
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
    }

    private void updateCamera() {
        camera.position.set(player.getHitbox().getX() + player.getHitbox().getWidth() / 2, player.getHitbox().getY() + player.getHitbox().getHeight() / 2, 0);
        camera.update();
    }

    public boolean isPlayerInRoom() {
        int playerX = (int) player.getHitbox().getX();
        int playerY = (int) player.getHitbox().getY();
        return mapRenderer.getMap().isInRoom(playerX, playerY, dungeonGenerator.getRooms());
    }

    public void updatePlayerSpeed() {
        if (isPlayerInRoom()) {
            player.resetSpeed(); // Reset to original speed if in a room
        } else {
            player.setOutOfRoomSpeed(); // Double the speed if not in a room

        }
    }

    @Override
//    public void resize(int width, int height) {
//        // Adjust the camera viewport size based on the window size
//        camera.viewportWidth = (float) (width * Constants.TILE_SIZE / 2) / 22; // should be based on tile size
//        camera.viewportHeight = (float) (height * Constants.TILE_SIZE / 2) / 22;
//        camera.update();
//    }

    public void resize(int width, int height) {
        float aspectRatio = (float) width / height;

        //Stolen :)
        if (aspectRatio >= 1.0f) {
            // Landscape orientation or square
            camera.viewportWidth = Constants.VIEWPORT_WIDTH;
            camera.viewportHeight = Constants.VIEWPORT_WIDTH / aspectRatio;
        } else {
            // Portrait orientation
            camera.viewportWidth = Constants.VIEWPORT_HEIGHT * aspectRatio;
            camera.viewportHeight = Constants.VIEWPORT_HEIGHT;
        }

        // apply the changes
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
        AssetManager.dispose();
    }



}
