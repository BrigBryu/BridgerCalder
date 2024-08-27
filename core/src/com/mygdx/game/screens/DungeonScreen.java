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
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.tiles.WallTile;

import java.util.ArrayList;
import java.util.List;

public class DungeonScreen implements Screen {
    int print = 0;
    private OrthographicCamera camera;
    private SpriteBatch batch;
   // private MapRenderer mapRenderer;
    private GameMap map;
    private Player player;
    private DungeonGenerator dungeonGenerator;
    private List<Enemy> enemies;
    private Boot game;


    public DungeonScreen(OrthographicCamera camera, Boot game) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.dungeonGenerator = new DungeonGenerator(camera, game);
        this.dungeonGenerator.generateDungeon();
        this.map = dungeonGenerator.getMap();
        if(Constants.DRAW_HIT_BOXES) {
            this.map.setCamera(camera);
        }


        this.player = new Player(dungeonGenerator.getStartX() * Constants.TILE_SIZE, dungeonGenerator.getStartY() * Constants.TILE_SIZE, camera); // Set initial position of the player within the dungeon

        this.game = game;

        // Initialize for running
        enemies = new ArrayList<>(dungeonGenerator.getEnemies());
        //Need to uncomment when want to test running on 2d map TODO
        map.setTileMap(dungeonGenerator.getRoomsExcludingStartRoom());
        System.out.println(map.toString());
    }

    @Override
    public void show() {
        // viewport is set correctly when the screen is shown
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
        //mapRenderer.render(batch);
        map.render(batch);
        player.render(batch); // Render the player
        for (Enemy enemy : enemies) {
            enemy.render(batch, ((BasicEnemy) enemy).getDirection());
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
//        player.update(delta, dungeonGenerator.getMap().getWallTiles(), enemies);
        print++;
        if(print == 50) {
            System.out.println("player x pixles " + player.getHitbox().getX());
            System.out.println("player y pixles " + player.getHitbox().getX());

            System.out.println("player x tiles " + (int) (player.getHitbox().getX() / Constants.TILE_SIZE));
            System.out.println("player y tiles " + (int) (player.getHitbox().getX() / Constants.TILE_SIZE));
        }

        //Correctly has collisions
        //List<WallTile> walls = map.getWallTiles((int) (player.getHitbox().getX() / Constants.TILE_SIZE) -  (4 * Constants.TILE_SIZE + 1), (int) (player.getHitbox().getY() / Constants.TILE_SIZE) -  (4 * Constants.TILE_SIZE + 1), 19, print == 50);
        //Correct hit box
        //List<WallTile> walls = map.getWallTiles((int) (player.getHitbox().getX()  / Constants.TILE_SIZE)  - (6), (int) (player.getHitbox().getY()  / Constants.TILE_SIZE) - (6), 12, print == 50);

        int playerTileX = (int) (player.getHitbox().getX() / Constants.TILE_SIZE);
        int playerTileY = (int) (player.getHitbox().getY() / Constants.TILE_SIZE);

        List<WallTile> walls = map.getWallTiles(playerTileX - 3, playerTileY - 3, 7, print == 50);



        if(print == 50) {
             print = 0;
        }
        int death = player.updateMap(delta, walls, enemies, map.getHitboxes());
        if(death == -1) {
            game.setScreen(new TestScreen(camera,game));
        }
    }

    private void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta, player);
        }
    }

    private void updateCamera() {
        camera.position.set(player.getHitbox().getX() + player.getHitbox().getWidth() / 2, player.getHitbox().getY() + player.getHitbox().getHeight() / 2, 0);
        camera.update();
    }

    public boolean isPlayerInRoom() {
        int playerX = (int) player.getHitbox().getX();
        int playerY = (int) player.getHitbox().getY();
       // return mapRenderer.getMap().isInRoom(playerX, playerY, dungeonGenerator.getRooms());
        return map.isInRoom(playerX, playerY, dungeonGenerator.getRooms());
    }

    public void updatePlayerSpeed() {
        if (isPlayerInRoom()) {
            player.resetSpeed(); // Reset to original speed if in a room
        } else {
            player.setOutOfRoomSpeed(); // Double the speed if not in a room

        }
    }

//    public void resize(int width, int height) {
//        // Adjust the camera viewport size based on the window size
//        camera.viewportWidth = (float) (width * Constants.TILE_SIZE / 2) / 22; // should be based on tile size
//        camera.viewportHeight = (float) (height * Constants.TILE_SIZE / 2) / 22;
//        camera.update();
//    }


    @Override
    public void resize(int width, int height) {
        // Adjust the camera viewport size based on the window size
        camera.viewportWidth = (float) (width * Constants.TILE_SIZE/2) / 22; // should be based on tile size is fucked up
        camera.viewportHeight = (float) (height * Constants.TILE_SIZE/2) / 22;
//        camera.viewportWidth = (float) (width * 1.2); // should be based on tile size is fucked up
//        camera.viewportHeight = (float) (height * 1.5);
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
