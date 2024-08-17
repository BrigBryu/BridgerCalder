package com.mygdx.game.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading, rendering, and managing a Tiled map using built in stuff
 */
public class TiledMapHandler implements Disposable {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private float unitScale;
    private List<RectangleMapObject> collisionObjects;
    private List<RectangleMapObject> interactiveObjects;


    public TiledMapHandler(String mapFilePath, OrthographicCamera camera) {
        this.tiledMap = new TmxMapLoader().load(mapFilePath);
        this.unitScale = (float) 1 / Constants.TILE_SIZE;
        this.camera = camera;
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
        this.collisionObjects = new ArrayList<>();
        this.interactiveObjects = new ArrayList<>();
        initializeCollisionObjects();
        initializeInteractiveCollisionObjects();
    }

    public void render(SpriteBatch batch) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    /**
     * Initializes collision objects from the "Collisions" layer in the map.
     */
    private void initializeCollisionObjects() {
        MapLayer collisionLayer = tiledMap.getLayers().get("Collisions");
        if (collisionLayer != null) {
            MapObjects solids = collisionLayer.getObjects();
            //Note must be rectangles
            for (RectangleMapObject rectangleObject : solids.getByType(RectangleMapObject.class)) {
                collisionObjects.add(rectangleObject);
            }
        }
    }

    /**
     * Initializes collision objects from the "Interactive" layer in the map.
     */
    private void initializeInteractiveCollisionObjects() {
        MapLayer interactiveLayer = tiledMap.getLayers().get("Interactive");
        if (interactiveLayer != null) {
            MapObjects interactiveLayerObjects = interactiveLayer.getObjects();
            //Note must be rectangles
            for (RectangleMapObject rectangleObject : interactiveLayerObjects.getByType(RectangleMapObject.class)) {
                if(rectangleObject.getName().equals("Start")) {
                    interactiveObjects.add(rectangleObject);
                }
            }
        }
    }

    public TiledMapTileLayer getTileLayer(int index) {
        return (TiledMapTileLayer) tiledMap.getLayers().get(index);
    }

    public TiledMapTileLayer getTileLayer(String name) {
        return (TiledMapTileLayer) tiledMap.getLayers().get(name);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        mapRenderer.dispose();
    }

    public List<RectangleMapObject> getCollisionObjects() {
        return collisionObjects;
    }
}
