package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.util.managers.AnimationManager;
import com.mygdx.game.util.managers.HealthManager;
import com.mygdx.game.util.HitBox;

public abstract class Entity {
    protected HitBox hitbox;
    protected AnimationManager animationManager;
    protected HealthManager healthManager;

    public Entity(float x, float y, float width, float height, float maxHealth) {
        // Initialize hitbox with position and size
        this.hitbox = new HitBox(x, y, width, height);

        // Initialize health manager with max health
        this.healthManager = new HealthManager(maxHealth);

        // Initialize animation manager
        this.animationManager = new AnimationManager();
    }

    // Abstract methods that subclasses (Player, Enemy, etc.) need to implement
    public abstract void update(float deltaTime);

    public abstract void render(SpriteBatch batch);

    public boolean isAlive() {
        return healthManager.isAlive();
    }

    public HitBox getHitBox() {
        return hitbox;
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }

    public void takeDamage(float damage) {
        healthManager.takeDamage(damage);
    }

    // Set position using hitbox
    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }

    // Getters for position and dimensions from the hitbox
    public float getX() {
        return hitbox.getX();
    }

    public float getY() {
        return hitbox.getY();
    }

    public float getWidth() {
        return hitbox.getWidth();
    }

    public float getHeight() {
        return hitbox.getHeight();
    }
}