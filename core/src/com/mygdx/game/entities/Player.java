package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.WallTile;

import java.util.List;

public class Player extends HitBox {
    private enum Direction {
        LEFT, RIGHT, IDLE, ATTACKING
    }

    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;

    private TextureRegion idleFrame;
    private Texture walkLeftFrame1, walkLeftFrame2, walkLeftFrame3, walkLeftFrame4, walkLeftFrame5;
    private Texture walkRightFrame1, walkRightFrame2, walkRightFrame3, walkRightFrame4, walkRightFrame5;
    private Texture attackLeftFrame1, attackLeftFrame2, attackLeftFrame3, attackLeftFrame4, attackLeftFrame5;
    private Texture attackRightFrame1, attackRightFrame2, attackRightFrame3, attackRightFrame4, attackRightFrame5;

    private float stateTime;
    private float attackTime;
    private boolean isAttacking;
    /**
     * Move speed in pixels per second
     */
    private float speed = 200;
    private Direction direction;

    public Player(float x, float y) {
        super(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);

        // Load individual textures
        idleFrame = new TextureRegion(new Texture("playerPlaceHolder1.png"));

        walkRightFrame1 = new Texture("playerPlaceHolderWalkRight1.png");
        walkRightFrame2 = new Texture("playerPlaceHolderWalkRight2.png");
        walkRightFrame3 = new Texture("playerPlaceHolderWalkRight3.png");
        walkRightFrame4 = new Texture("playerPlaceHolderWalkRight4.png");
        walkRightFrame5 = new Texture("playerPlaceHolderWalkRight5.png");

        walkLeftFrame1 = new Texture("playerPlaceHolderWalkLeft1.png");
        walkLeftFrame2 = new Texture("playerPlaceHolderWalkLeft2.png");
        walkLeftFrame3 = new Texture("playerPlaceHolderWalkLeft3.png");
        walkLeftFrame4 = new Texture("playerPlaceHolderWalkLeft4.png");
        walkLeftFrame5 = new Texture("playerPlaceHolderWalkLeft5.png");

        attackRightFrame1 = new Texture("playerPlaceHolderAttackRight1.png");
        attackRightFrame2 = new Texture("playerPlaceHolderAttackRight2.png");
        attackRightFrame3 = new Texture("playerPlaceHolderAttackRight3.png");
        attackRightFrame4 = new Texture("playerPlaceHolderAttackRight4.png");
        attackRightFrame5 = new Texture("playerPlaceHolderAttackRight5.png");

        attackLeftFrame1 = new Texture("playerPlaceHolderAttackLeft1.png");
        attackLeftFrame2 = new Texture("playerPlaceHolderAttackLeft2.png");
        attackLeftFrame3 = new Texture("playerPlaceHolderAttackLeft3.png");
        attackLeftFrame4 = new Texture("playerPlaceHolderAttackLeft4.png");
        attackLeftFrame5 = new Texture("playerPlaceHolderAttackLeft5.png");

        // Create the walk animations
        walkLeftAnimation = new Animation<TextureRegion>(0.15f,
                new TextureRegion(walkLeftFrame1),
                new TextureRegion(walkLeftFrame2),
                new TextureRegion(walkLeftFrame3),
                new TextureRegion(walkLeftFrame4),
                new TextureRegion(walkLeftFrame5)
        );
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);

        walkRightAnimation = new Animation<TextureRegion>(0.15f,
                new TextureRegion(walkRightFrame1),
                new TextureRegion(walkRightFrame2),
                new TextureRegion(walkRightFrame3),
                new TextureRegion(walkRightFrame4),
                new TextureRegion(walkRightFrame5)
        );
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Create the attack animations
        attackLeftAnimation = new Animation<TextureRegion>(0.1f,
                new TextureRegion(attackLeftFrame1),
                new TextureRegion(attackLeftFrame2),
                new TextureRegion(attackLeftFrame3),
                new TextureRegion(attackLeftFrame4),
                new TextureRegion(attackLeftFrame5)
        );
        attackLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        attackRightAnimation = new Animation<TextureRegion>(0.1f,
                new TextureRegion(attackRightFrame1),
                new TextureRegion(attackRightFrame2),
                new TextureRegion(attackRightFrame3),
                new TextureRegion(attackRightFrame4),
                new TextureRegion(attackRightFrame5)
        );
        attackRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        stateTime = 0f;
        direction = Direction.IDLE;
        isAttacking = false;
        attackTime = 0f;
    }

    public void update(float delta, List<WallTile> wallTiles) {
        boolean isMoving = false;

        float oldX = x, oldY = y; // Store old position to revert if collision occurs

        float curSpeed = speed;
        if(isAttacking){
            curSpeed = speed/2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            y += curSpeed * delta;
            direction = Direction.RIGHT; // Use right animation for moving up
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= curSpeed * delta;
            direction = Direction.LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            y -= curSpeed * delta;
            direction = Direction.LEFT; // Use left animation for moving down
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += curSpeed * delta;
            direction = Direction.RIGHT;
            isMoving = true;
        }

        // Check for attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isAttacking) {
            isAttacking = true;
            attackTime = 0f;
            direction = Direction.ATTACKING;
            System.out.println("test");
        }

        // Update position only if not attacking
            setPosition(x, y); // Update hitbox position

            // Check for collisions with wall tiles
            for (WallTile wallTile : wallTiles) {
                if (this.overlaps(wallTile)) {
                    setPosition(oldX, oldY); // Revert to old position if collision occurs
                    break;
                }
            }

            if (isMoving) {
                stateTime += delta;
            } else {
                stateTime = 0;
                direction = Direction.IDLE;
            }
            attackTime += delta;
            if (attackTime > attackLeftAnimation.getAnimationDuration()) {
                isAttacking = false;
                attackTime = 0f;
            }

    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (isAttacking) {
            if (direction != Direction.LEFT) {
                currentFrame = attackLeftAnimation.getKeyFrame(attackTime, false);
            } else {
                currentFrame = attackRightAnimation.getKeyFrame(attackTime, false);
            }
        } else {
            switch (direction) {
                case LEFT:
                    currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
                    break;
                case RIGHT:
                    currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                    break;
                case IDLE:
                default:
                    currentFrame = idleFrame;
                    break;
            }
        }

        batch.draw(currentFrame, x, y, width, height);
    }

    public void dispose() {
        idleFrame.getTexture().dispose();
        walkLeftFrame1.dispose();
        walkLeftFrame2.dispose();
        walkLeftFrame3.dispose();
        walkLeftFrame4.dispose();
        walkLeftFrame5.dispose();
        walkRightFrame1.dispose();
        walkRightFrame2.dispose();
        walkRightFrame3.dispose();
        walkRightFrame4.dispose();
        walkRightFrame5.dispose();
        attackLeftFrame1.dispose();
        attackLeftFrame2.dispose();
        attackLeftFrame3.dispose();
        attackLeftFrame4.dispose();
        attackLeftFrame5.dispose();
        attackRightFrame1.dispose();
        attackRightFrame2.dispose();
        attackRightFrame3.dispose();
        attackRightFrame4.dispose();
        attackRightFrame5.dispose();
    }
}
