package com.mygdx.game.util.InteractiveRectangleMapObjects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.game.Boot;
import com.mygdx.game.screens.DungeonScreen;
import com.mygdx.game.util.InteractiveRectangleMapObject;

public class EndRectangleMapObject extends InteractiveRectangleMapObject {

    Boot game;
    OrthographicCamera camera;

    /**
     * Make a hit box interactive
     * With intersectionAction doing nothing
     *
     * @param x      in pixels not tiles
     * @param y      in pixels not tiles
     * @param width  in pixels not tiles
     * @param height in pixels not tiles
     */
    public EndRectangleMapObject(float x, float y, float width, float height, OrthographicCamera camera, Boot game) {
        super(x, y, width, height);
        this.game = game;
        this.camera = camera;
    }

    public EndRectangleMapObject(RectangleMapObject rec, OrthographicCamera camera, Boot game) {
        super(rec);
        this.camera = camera;
        this.game = game;
    }

    /**
     * Called after intersection with player to rest the dungeon
     */
    @Override
    public void intersectionInteraction() {
        System.out.println("Trying to make new game");
        game.setScreen(new DungeonScreen(camera, game));
    }

    @Override
    public String getHitBoxType(){
        return "End hit box";
    }
}
