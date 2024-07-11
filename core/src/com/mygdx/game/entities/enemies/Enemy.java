package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.util.AssetManager;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;

public abstract class Enemy extends HitBox {
    protected float health;
    private float displayDamageTime = 0;
    private static final float DAMAGE_DISPLAY_DURATION = 0.5f;  // Duration to display the damage bar

    protected Animation<TextureRegion> walkLeftAnimation;
    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> walkUpAnimation;
    protected Animation<TextureRegion> walkDownAnimation;
    protected float stateTime;

    protected TextureRegion idleFrame;
    protected Enums.Direction direction;
    private float previousHealth;


    public Enemy(float x, float y, float width, float height, float health) {
        super(x, y, width, height);
        this.health = health;
        this.stateTime = 0f;
        loadAnimations();
    }

    protected void setAnimations(Texture idleTexture,
                                 Animation<TextureRegion> walkLeftAnimation,
                                 Animation<TextureRegion> walkRightAnimation,
                                 Animation<TextureRegion> walkUpAnimation,
                                 Animation<TextureRegion> walkDownAnimation) {
        this.idleFrame = new TextureRegion(idleTexture);
        this.walkLeftAnimation = walkLeftAnimation;
        this.walkRightAnimation = walkRightAnimation;
        this.walkUpAnimation = walkUpAnimation;
        this.walkDownAnimation = walkDownAnimation;
    }

    public void render(SpriteBatch batch, Enums.Direction direction) {
        Animation<TextureRegion> currentAnimation = null;
        switch (direction) {
            case LEFT:
                currentAnimation = walkLeftAnimation;
                break;
            case RIGHT:
                currentAnimation = walkRightAnimation;
                break;
            case UP:
                currentAnimation = walkUpAnimation;
                break;
            case DOWN:
                currentAnimation = walkDownAnimation;
                break;
        }

        TextureRegion currentFrame = currentAnimation != null ?
                currentAnimation.getKeyFrame(stateTime, true) : idleFrame;

        batch.draw(currentFrame, x, y, width, height);

        // Draw the health bar
        drawHealthBar(batch);
    }

    public void takeDamage(float damage) {
        previousHealth = health;
        health -= damage;
        if (health < 0) health = 0;
        displayDamageTime = DAMAGE_DISPLAY_DURATION;
        if (health <= 0) {
            // Handle enemy death (e.g., remove from game, play death animation)
        }
    }

    private void drawHealthBar(SpriteBatch batch) {
        float barWidth = width * 0.8f;
        float barHeight = barWidth * (AssetManager.healthBarBackground.getHeight() / (float) AssetManager.healthBarBackground.getWidth());
        float barX = x + (width - barWidth) / 2;
        float barY = y + height + 10;

        float healthPercentage = health / 100f;
        float previousHealthPercentage = previousHealth / 100f;


        // Draw background
        batch.draw(AssetManager.healthBarBackground, barX, barY, barWidth, barHeight);

        // Draw damage bar if needed
        if (displayDamageTime > 0) {
            float damageWidth = barWidth * (previousHealthPercentage - healthPercentage);
            batch.draw(AssetManager.healthBarDamage, barX + barWidth * healthPercentage, barY, damageWidth, barHeight);
            displayDamageTime -= Gdx.graphics.getDeltaTime();
        }

        // Draw health bar
        batch.draw(AssetManager.healthBarForeground, barX, barY, barWidth * healthPercentage, barHeight);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void dispose() {
        idleFrame.getTexture().dispose();
        if (walkLeftAnimation != null) {
            for (TextureRegion frame : walkLeftAnimation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
        if (walkRightAnimation != null) {
            for (TextureRegion frame : walkRightAnimation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
        if (walkUpAnimation != null) {
            for (TextureRegion frame : walkUpAnimation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
        if (walkDownAnimation != null) {
            for (TextureRegion frame : walkDownAnimation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
    }

    public Enums.Direction getDirection() {
        return direction;
    }

    public abstract void move(float delta);

    public abstract void update(float delta);

    protected abstract void loadAnimations();
}