package com.mygdx.game.util;

/**
 * Abstract class to be extended all interactive hit boxes that have a different job need to be unique subclasses
 * Used when using map not tiled map
 */
public abstract class InteractiveHitBox extends HitBox {
    /**
     * Make a hit box interactive
     * With intersectionAction doing nothing
     *
     * @param x      in pixels not tiles
     * @param y      in pixels not tiles
     * @param width  in pixels not tiles
     * @param height in pixels not tiles
     */
    public InteractiveHitBox(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    /**
     * Calls intersectionInteraction if the HitBoxes overlap
     * @param other hit box
     * @return if the HitBoxes overlap
     *
     */
    @Override
    public boolean overlaps(HitBox other) {
        boolean overlaps = this.bounds.overlaps(other.getBounds());
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
