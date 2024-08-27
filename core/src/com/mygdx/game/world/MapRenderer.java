package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MapRenderer {
    private GameMap map;

    public MapRenderer(GameMap map) {
        this.map = map;
    }

    public void render(SpriteBatch batch) {
        map.render(batch);
    }

    public GameMap getMap(){
        return map;
    }
}
