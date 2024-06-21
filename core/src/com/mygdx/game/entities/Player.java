package com.mygdx.game.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.WallTile;


import java.util.List;


public class Player extends HitBox {

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
    private Enums.Direction direction;
    private Enums.PlayerState state;


    private float speed = 200;


    // Attack HitBoxes
    private HitBox attackHitBoxLeft;
    private HitBox attackHitBoxRight;
    private HitBox attackHitBoxUp;
    private HitBox attackHitBoxDown;


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
        direction = Enums.Direction.RIGHT;
        state = Enums.PlayerState.IDLE;


        // Initialize attack HitBoxes
        attackHitBoxLeft = new HitBox(x - Constants.TILE_SIZE, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);
        attackHitBoxRight = new HitBox(x + Constants.TILE_SIZE, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);
        attackHitBoxUp = new HitBox(x, y + Constants.TILE_SIZE * 2, Constants.TILE_SIZE, Constants.TILE_SIZE);
        attackHitBoxDown = new HitBox(x, y - Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
    }


    public void update(float delta, List<WallTile> wallTiles, List<Enemy> enemies) {
        float oldX = x, oldY = y; // Store old position to revert if collision occurs
        boolean isMoving = false;


        if (state != Enums.PlayerState.ATTACKING) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                y += speed * delta;
                direction = Enums.Direction.UP;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                x -= speed * delta;
                direction = Enums.Direction.LEFT;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                y -= speed * delta;
                direction = Enums.Direction.DOWN;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                x += speed * delta;
                direction = Enums.Direction.RIGHT;
                isMoving = true;
            }


            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                Gdx.app.log("Player", "Attack initiated");
                state = Enums.PlayerState.ATTACKING;
                stateTime = 0f;
                activateAttackHitBox(enemies);
            }


            setPosition(x, y);


            for (WallTile wallTile : wallTiles) {
                if (this.overlaps(wallTile)) {
                    setPosition(oldX, oldY);
                    break;
                }
            }


            if (isMoving) {
                state = Enums.PlayerState.WALKING;
                stateTime += delta;
            } else {
                state = Enums.PlayerState.IDLE;
                stateTime = 0f;
            }
        } else {
            stateTime += delta;
            Gdx.app.log("Player", "Attacking: " + stateTime + "/" + attackRightAnimation.getAnimationDuration());
            if (stateTime > attackRightAnimation.getAnimationDuration()) {
                Gdx.app.log("Player", "Attack finished");
                state = Enums.PlayerState.IDLE;
                stateTime = 0f;
            }
        }
    }


    private void activateAttackHitBox(List<Enemy> enemies) {
        HitBox attackHitBox = null;
        switch (direction) {
            case LEFT:
                attackHitBox = attackHitBoxLeft;
                break;
            case RIGHT:
                attackHitBox = attackHitBoxRight;
                break;
            case UP:
                attackHitBox = attackHitBoxUp;
                break;
            case DOWN:
                attackHitBox = attackHitBoxDown;
                break;
        }


        if (attackHitBox != null) {
            for (Enemy enemy : enemies) {
                if (attackHitBox.overlaps(enemy)) {
                    doDamage(enemy);
                }
            }
        }
    }


    private void doDamage(Enemy enemy) {
        // Implement damage logic here TODO
    }


    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = null;


        switch (state) {
            case WALKING:
                currentFrame = direction == Enums.Direction.LEFT ?
                        walkLeftAnimation.getKeyFrame(stateTime, true) :
                        walkRightAnimation.getKeyFrame(stateTime, true);
                break;
            case ATTACKING:
                currentFrame = direction == Enums.Direction.LEFT ?
                        attackLeftAnimation.getKeyFrame(stateTime, false) :
                        attackRightAnimation.getKeyFrame(stateTime, false);
                break;
            case IDLE:
            default:
                currentFrame = idleFrame;
                break;
        }


        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, width, height);
        }
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