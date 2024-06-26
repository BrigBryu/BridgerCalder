package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Boot;

public class MenuScreen implements Screen {
    private Boot game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture gameButtonTexture;
    private Texture dungeonButtonTexture;
    private int buttonWidth = 200;
    private int buttonHeight = 100;
    private int buttonY = 300;
    private int gameButtonX = 100;
    private int dungeonButtonX = 400;

    public MenuScreen(Boot game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 800, 600);
        this.batch = new SpriteBatch();

        // Create simple textures for the buttons
        Pixmap pixmap = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGB888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();
        gameButtonTexture = new Texture(pixmap);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        dungeonButtonTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(gameButtonTexture, gameButtonX, buttonY, buttonWidth, buttonHeight);
        batch.draw(dungeonButtonTexture, dungeonButtonX, buttonY, buttonWidth, buttonHeight);
        batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            y = Gdx.graphics.getHeight() - y; // Invert y-coordinate since screen coordinates start from top-left

            if (x > gameButtonX && x < gameButtonX + buttonWidth && y > buttonY && y < buttonY + buttonHeight) {
                game.setScreen(new GameScreen(game.getOrthographicCamera()));
            }

            if (x > dungeonButtonX && x < dungeonButtonX + buttonWidth && y > buttonY && y < buttonY + buttonHeight) {
                game.setScreen(new DungeonScreen(game.getOrthographicCamera()));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
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
        gameButtonTexture.dispose();
        dungeonButtonTexture.dispose();
    }
}
