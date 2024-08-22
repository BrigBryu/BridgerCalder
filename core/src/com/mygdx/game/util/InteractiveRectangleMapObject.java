package com.mygdx.game.util;

import com.badlogic.gdx.maps.objects.RectangleMapObject;

/**
 * Used when using tiled map
 */
public abstract class InteractiveRectangleMapObject extends RectangleMapObject {

    /**
     * Make a rectangle object interactive
     * With intersectionAction doing nothing
     *
     * @param x      in pixels not tiles
     * @param y      in pixels not tiles
     * @param width  in pixels not tiles
     * @param height in pixels not tiles
     */
    public InteractiveRectangleMapObject(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    /**
     * Make a rectangle object interactive
     * With intersectionAction doing nothing
     *
     * @param rec RectangleMapObject used to make into a InteractiveRectangleMapObject
     */
    public InteractiveRectangleMapObject(RectangleMapObject rec) {
        super(rec.getRectangle().x, rec.getRectangle().y, rec.getRectangle().width, rec.getRectangle().height);
    }


    /**
     * Calls intersectionInteraction if the HitBox overlaps with this rectangle
     * @param other hit box
     * @return if the HitBoxes overlap
     *
     */
    public boolean overlaps(HitBox other) {
        boolean overlaps = this.getRectangle().overlaps(other.getBounds());
        if(overlaps) {
            System.out.println("Calling intersection interaction");
            intersectionInteraction();
        }
        return overlaps;
    }

    /**
     * Called after intersection with player
     */
    public abstract void intersectionInteraction();

    public abstract String getHitBoxType();

}
