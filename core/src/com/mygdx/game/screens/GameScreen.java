package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.entities.Player;
import com.mygdx.game.util.Constants;
import com.mygdx.game.world.Map;
import com.mygdx.game.world.MapRenderer;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private MapRenderer mapRenderer;
    private Player player;
    private Map map;

    public GameScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.map = new Map();
        this.map.initialize(); // Make sure to initialize the map
        this.mapRenderer = new MapRenderer(map);
        this.player = new Player(100, 100); // Set initial position of the player
    }

    @Override
    public void show() {
        // Ensure the viewport is set correctly when the screen is shown
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear the screen with a black color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        checkUserInput(delta);
        updateCamera();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        mapRenderer.render(batch);
        player.render(batch); // Render the player
        batch.end();
    }

    private void checkUserInput(float delta) {
        player.update(delta, map.getWallTiles());
    }

    private void updateCamera() {
        camera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);
        camera.update();
    }

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
        map.dispose();
    }
}




