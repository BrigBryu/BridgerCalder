package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MapRenderer {
    private Map map;

    public MapRenderer(Map map) {
        this.map = map;
    }

    public void render(SpriteBatch batch) {
        map.render(batch);
    }

    public Map getMap(){
        return map;
    }
}
