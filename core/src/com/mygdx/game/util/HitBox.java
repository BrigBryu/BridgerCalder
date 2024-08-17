package com.mygdx.game.util;

import com.badlogic.gdx.math.Rectangle;

public class HitBox {
    protected float x, y;
    protected float width, height;
    protected Rectangle bounds;

    /**
     * Make a hit box
     * @param x in pixels not tiles
     * @param y in pixels not tiles
     * @param width in pixels not tiles
     * @param height in pixels not tiles
     */
    public HitBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public boolean overlaps(HitBox other) {
        return this.bounds.overlaps(other.getBounds());
    }

    public boolean overlaps(Rectangle otherRectangle) {
        return this.bounds.overlaps(otherRectangle);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.bounds.setSize(width, height);
    }

    // Getters and setters

    /**
     * in pixels not tile size
     * @return x from bottom left
     */
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        this.bounds.setX(x);
    }

    /**
     * in pixels not tile size
     * @return y from bottom left
     */
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        this.bounds.setY(y);
    }

    public float getWidth() {
        return width;
    }

    public void set(float hitboxX, float hitboxY, float hitboxWidth, float hitboxHeight){
        setX(hitboxX);
        setY(hitboxY);
        setWidth(hitboxWidth);
        setHeight(hitboxHeight);

    }

    public void setWidth(float width) {
        this.width = width;
        this.bounds.setWidth(width);
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
        this.bounds.setHeight(height);
    }
}