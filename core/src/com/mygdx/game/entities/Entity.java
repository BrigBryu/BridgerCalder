package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.util.managers.*;

import java.util.List;

public abstract class Entity {

    // Managers
    protected AnimationManager animationManager;
    protected HealthManager healthManager;
    protected AttackManager attackManager;
    protected CollisionManager collisionManager;

    protected float stateTime;
    protected Enums.Direction direction;

    protected HitBox hitbox;
    private ShapeRenderer shapeRenderer; // Renderer for drawing hitboxes
    protected Camera camera;
    protected GameMap map;

    private static final float DAMAGE_DISPLAY_DURATION = 0.5f;

    public Entity(float x, float y, float width, float height, float maxHealth, Camera camera, GameMap map) {
        this.hitbox = new HitBox(x, y, width, height);
        this.stateTime = 0f;
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;
        this.map = map;

        this.healthManager = new HealthManager(maxHealth);
        this.animationManager = new AnimationManager();
        this.attackManager = new AttackManager();
        this.collisionManager = new CollisionManager();

        loadAnimations();
    }

    // methods for all entities

    public void render(SpriteBatch batch, Enums.Direction direction) {

        animationManager.setAnimation(getAnimationTypeForDirection(direction));
        batch.draw(animationManager.getCurrentFrame(Gdx.graphics.getDeltaTime()), hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        batch.end();
        if(Constants.DRAW_HIT_BOXES) {
            drawHitboxes();
        }
        batch.begin();
    }

    private String getAnimationTypeForDirection(Enums.Direction direction) {
        switch (direction) {
            case LEFT: return "walkLeft";
            case RIGHT: return "walkRight";
            case UP: return "walkUp";
            case DOWN: return "walkDown";
            case UP_LEFT: return "walkingUpLeft";
            case UP_RIGHT: return "walkingUpRight";
            case DOWN_LEFT: return "walkingDownLeft";
            case DOWN_RIGHT: return "walkingDownRight";
            default: return "idle";
        }
    }

    public void takeDamage(float damage) {
        healthManager.takeDamage(damage);
        if (!healthManager.isAlive()) {
            // Handle death (e.g., remove from game, play death animation)
        }
    }

    private void drawHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw hitbox
        shapeRenderer.setColor(1, 1, 0, 1); // Yellow for hitbox
        shapeRenderer.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        shapeRenderer.end();
    }

    public void dispose() {
        animationManager = null;  // Dispose animations properly in manager
        shapeRenderer.dispose();
    }

    public float getHealth() {
        return healthManager.getCurrentHealth();
    }

    public HitBox getHitBox() {
        return hitbox;
    }

    public void setHealth(float health) {
        this.healthManager = new HealthManager(health);
    }

    public abstract void move(float delta);

    public void update(float delta) {
        move(delta);
        stateTime += delta;
    }

    /**
     *
     * @see AnimationManager addAnimation() method needs to be used in implimentation
     */
    protected abstract void loadAnimations();


    public boolean checkAndHandleCollisionWith(List<Entity> other) {
        return collisionManager.checkAndHandleCollisionWith(this, other);
    }

    public boolean isAttacking() {
        //TODO
        return true;
    }

    public HitBox getAttackHitBox() {
        //TODO place holder to just trigger attack if bodies bump
        return hitbox;
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }
}