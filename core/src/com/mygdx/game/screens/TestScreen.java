package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.Boot;
import com.mygdx.game.entities.Player;
import com.mygdx.game.entities.enemies.BasicEnemy;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.InteractiveRectangleMapObject;
import com.mygdx.game.world.TiledMapHandler;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

import java.util.ArrayList;
import java.util.List;

public class TestScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Player player;
    private List<InteractiveRectangleMapObject> interactiveObjects;
    private TiledMapHandler tiledMapHandler;
    private List<Enemy> enemies;
    private Boot game;

    public TestScreen(OrthographicCamera camera, Boot game) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        //TODO need an update
        this.tiledMapHandler = new TiledMapHandler("TiledMaps/maps/untitled.tmx", camera, game);
        this.game = game;
        this.player = new Player(100, 100, camera);
        this.interactiveObjects = tiledMapHandler.getInteractiveObjects();
        enemies = new ArrayList<>();
        initializeEnemies();
    }

    private void initializeEnemies() {
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        tiledMapHandler.render(batch);
        player.render(batch);
        for (Enemy enemy : enemies) {
            enemy.render(batch, ((BasicEnemy) enemy).getDirection());
        }
        batch.end();

        if(Constants.DRAW_HIT_BOXES) {
            renderInteractiveAndCollisionObjects();
        }
    }

    private void update(float delta) {
        checkUserInput(delta);
        updateEnemies(delta);
        updateCamera();
    }

    private void checkUserInput(float delta) {
        List<RectangleMapObject> collisionObjects = tiledMapHandler.getCollisionObjects();
        player.updateTiled(delta, collisionObjects, enemies, interactiveObjects);
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

    @Override
    public void resize(int width, int height) {
        // Adjust the camera viewport size based on the window size
        camera.viewportWidth = (float) (width * Constants.TILE_SIZE/2) / 22; // should be based on tile size is fucked up
        camera.viewportHeight = (float) (height * Constants.TILE_SIZE/2) / 22;
//        camera.viewportWidth = (float) (width * 1.2); // should be based on tile size is fucked up
//        camera.viewportHeight = (float) (height * 1.5);
        camera.update();
    }

    private void renderInteractiveAndCollisionObjects() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Render Collision Objects
        shapeRenderer.setColor(1, 0, 0, 1);
        for (RectangleMapObject collisionObject : tiledMapHandler.getCollisionObjects()) {
            shapeRenderer.rect(collisionObject.getRectangle().getX(), collisionObject.getRectangle().getY(),
                    collisionObject.getRectangle().getWidth(), collisionObject.getRectangle().getHeight());
        }

        // Render Interactive Objects
        shapeRenderer.setColor(0, 1, 0, 1);
        for (InteractiveRectangleMapObject interactiveObject : interactiveObjects) {
            shapeRenderer.rect(interactiveObject.getRectangle().getX(), interactiveObject.getRectangle().getY(),
                    interactiveObject.getRectangle().getWidth(), interactiveObject.getRectangle().getHeight());
        }

        shapeRenderer.end();
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
        tiledMapHandler.dispose();
        shapeRenderer.dispose();
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }
    }
}


