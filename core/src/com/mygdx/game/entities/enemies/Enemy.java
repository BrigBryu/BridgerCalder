package com.mygdx.game.entities.enemies;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;


public abstract class Enemy extends HitBox {
    protected float health;

    protected Animation<TextureRegion> walkLeftAnimation;
    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> walkUpAnimation;
    protected Animation<TextureRegion> walkDownAnimation;
    protected float stateTime;

    protected TextureRegion idleFrame;
    protected Enums.Direction direction;




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
    }


    public void takeDamage(float damage) {
        health -= damage;
        if (health <= 0) {
            // Handle enemy death (e.g., remove from game, play death animation)
        }
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