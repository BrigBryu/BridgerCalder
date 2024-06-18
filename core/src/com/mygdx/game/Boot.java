package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.screens.GameScreen;

public class Boot extends Game {
    public static Boot INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;

    public Boot() {
        INSTANCE = this;
    }

    @Override
    public void create() {
        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();

        // Set the viewport size here
        this.orthographicCamera = new OrthographicCamera(1600 , 1200);
        this.orthographicCamera.setToOrtho(false);

        setScreen(new GameScreen(this.orthographicCamera));
    }

    public int getWidthScreen() {
        return widthScreen;
    }

    public int getHeightScreen() {
        return heightScreen;
    }

    public OrthographicCamera getOrthographicCamera() {
        return orthographicCamera;
    }
}
