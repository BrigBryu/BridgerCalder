//package com.mygdx.game;
//
//import com.badlogic.gdx.Game;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.mygdx.game.screens.GameScreen;
//import com.mygdx.game.world.Map;
//
//public class GameMain extends Game {
//    public SpriteBatch batch;
//    public Map map;
//
//    @Override
//    public void create() {
//        batch = new SpriteBatch();
//        map = new Map();
//        map.initialize();
//        this.setScreen(new GameScreen(this));
//    }
//
//    @Override
//    public void render() {
//        super.render(); // calls render on the current screen
//    }
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        map.dispose();
//    }
//}