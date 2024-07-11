package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;

public class Player {

    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;
    private Animation<TextureRegion> slashLeftAnimation;
    private Animation<TextureRegion> slashRightAnimation;
    private Animation<TextureRegion> slashUpAnimation;
    private Animation<TextureRegion> slashDownAnimation;

    private TextureRegion idleFrame;
    private Texture walkLeftFrame1, walkLeftFrame2, walkLeftFrame3, walkLeftFrame4, walkLeftFrame5;
    private Texture walkRightFrame1, walkRightFrame2, walkRightFrame3, walkRightFrame4, walkRightFrame5;
    private Texture attackLeftFrame1, attackLeftFrame2, attackLeftFrame3, attackLeftFrame4, attackLeftFrame5;
    private Texture attackRightFrame1, attackRightFrame2, attackRightFrame3, attackRightFrame4, attackRightFrame5;
    private Texture slashLeftFrame1, slashLeftFrame2, slashLeftFrame3, slashLeftFrame4, slashLeftFrame5, slashLeftFrame6, slashLeftFrame7, slashLeftFrame8;
    private Texture slashRightFrame1, slashRightFrame2, slashRightFrame3, slashRightFrame4, slashRightFrame5, slashRightFrame6, slashRightFrame7, slashRightFrame8;
    private Texture slashUpFrame1, slashUpFrame2, slashUpFrame3, slashUpFrame4, slashUpFrame5, slashUpFrame6, slashUpFrame7, slashUpFrame8;
    private Texture slashDownFrame1, slashDownFrame2, slashDownFrame3, slashDownFrame4, slashDownFrame5, slashDownFrame6, slashDownFrame7, slashDownFrame8;

    private float stateTime;
    private Enums.Direction direction;
    private Enums.PlayerState movementState;
    private Enums.AttackState attackState;
    private Enums.Direction attackDirection = null;


    private float speed = 0;
    private float speedBase = 350 ;
    private float speedOutOfHall = (float) (speedBase * 2);



    private HitBox hitbox;
    private float playerBodyTextureWidth = Constants.TILE_SIZE;
    private float playerBodyTextureHeight = Constants.TILE_SIZE * 2;

    // Attack HitBoxes
    private HitBox attackHitBoxLeft;
    private HitBox attackHitBoxRight;
    private HitBox attackHitBoxUp;
    private HitBox attackHitBoxDown;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;


    public Player(float x, float y, OrthographicCamera camera) {
        this.camera = camera;
        //super((float) (x + (Constants.TILE_SIZE * 0.2)), (float) (y + (Constants.TILE_SIZE * 0.2)), Constants.TILE_SIZE * 0.5f, Constants.TILE_SIZE * .7f);
        hitbox = new HitBox ((x),(y), (float) ((float) Constants.TILE_SIZE * 0.8), (float) (Constants.TILE_SIZE * 1.));

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

        slashLeftFrame1 = new Texture("slashLeft1.png");
        slashLeftFrame2 = new Texture("slashLeft2.png");
        slashLeftFrame3 = new Texture("slashLeft3.png");
        slashLeftFrame4 = new Texture("slashLeft4.png");
        slashLeftFrame5 = new Texture("slashLeft5.png");
        slashLeftFrame6 = new Texture("slashLeft6.png");
        slashLeftFrame7 = new Texture("slashLeft7.png");
        slashLeftFrame8 = new Texture("slashLeft8.png");

        slashRightFrame1 = new Texture("slashRight1.png");
        slashRightFrame2 = new Texture("slashRight2.png");
        slashRightFrame3 = new Texture("slashRight3.png");
        slashRightFrame4 = new Texture("slashRight4.png");
        slashRightFrame5 = new Texture("slashRight5.png");
        slashRightFrame6 = new Texture("slashRight6.png");
        slashRightFrame7 = new Texture("slashRight7.png");
        slashRightFrame8 = new Texture("slashRight8.png");

        slashUpFrame1 = new Texture("slashUp1.png");
        slashUpFrame2 = new Texture("slashUp2.png");
        slashUpFrame3 = new Texture("slashUp3.png");
        slashUpFrame4 = new Texture("slashUp4.png");
        slashUpFrame5 = new Texture("slashUp5.png");
        slashUpFrame6 = new Texture("slashUp6.png");
        slashUpFrame7 = new Texture("slashUp7.png");
        slashUpFrame8 = new Texture("slashUp8.png");

        slashDownFrame1 = new Texture("slashDown1.png");
        slashDownFrame2 = new Texture("slashDown2.png");
        slashDownFrame3 = new Texture("slashDown3.png");
        slashDownFrame4 = new Texture("slashDown4.png");
        slashDownFrame5 = new Texture("slashDown5.png");
        slashDownFrame6 = new Texture("slashDown6.png");
        slashDownFrame7 = new Texture("slashDown7.png");
        slashDownFrame8 = new Texture("slashDown8.png");

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

        float attackFrameDuration = .1f;
        // Create the attack animations
        attackRightAnimation = new Animation<TextureRegion>(attackFrameDuration,
                new TextureRegion(attackLeftFrame1),
                new TextureRegion(attackLeftFrame2),
                new TextureRegion(attackLeftFrame3),
                new TextureRegion(attackLeftFrame4),
                new TextureRegion(attackLeftFrame5)
        );
        attackRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        attackLeftAnimation = new Animation<TextureRegion>(attackFrameDuration,
                new TextureRegion(attackRightFrame1),
                new TextureRegion(attackRightFrame2),
                new TextureRegion(attackRightFrame3),
                new TextureRegion(attackRightFrame4),
                new TextureRegion(attackRightFrame5)
        );
        attackLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        float slashFrameDuration = .1f;
        // Create the slash animations
        slashLeftAnimation = new Animation<TextureRegion>(slashFrameDuration,
                new TextureRegion(slashLeftFrame1),
                new TextureRegion(slashLeftFrame2),
                new TextureRegion(slashLeftFrame3),
                new TextureRegion(slashLeftFrame4),
                new TextureRegion(slashLeftFrame5),
                new TextureRegion(slashLeftFrame6),
                new TextureRegion(slashLeftFrame7),
                new TextureRegion(slashLeftFrame8)
        );
        slashLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        slashRightAnimation = new Animation<TextureRegion>(slashFrameDuration,
                new TextureRegion(slashRightFrame1),
                new TextureRegion(slashRightFrame2),
                new TextureRegion(slashRightFrame3),
                new TextureRegion(slashRightFrame4),
                new TextureRegion(slashRightFrame5),
                new TextureRegion(slashRightFrame6),
                new TextureRegion(slashRightFrame7),
                new TextureRegion(slashRightFrame8)
        );
        slashRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        slashUpAnimation = new Animation<TextureRegion>(attackFrameDuration,
                new TextureRegion(slashUpFrame1),
                new TextureRegion(slashUpFrame2),
                new TextureRegion(slashUpFrame3),
                new TextureRegion(slashUpFrame4),
                new TextureRegion(slashUpFrame5),
                new TextureRegion(slashUpFrame6),
                new TextureRegion(slashUpFrame7),
                new TextureRegion(slashUpFrame8)
        );
        slashUpAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        slashDownAnimation = new Animation<TextureRegion>(attackFrameDuration,
                new TextureRegion(slashDownFrame1),
                new TextureRegion(slashDownFrame2),
                new TextureRegion(slashDownFrame3),
                new TextureRegion(slashDownFrame4),
                new TextureRegion(slashDownFrame5),
                new TextureRegion(slashDownFrame6),
                new TextureRegion(slashDownFrame7),
                new TextureRegion(slashDownFrame8)
        );
        slashDownAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        stateTime = 0f;
        direction = Enums.Direction.RIGHT;
        movementState = Enums.PlayerState.IDLE;
        attackState = Enums.AttackState.NOT_ATTACKING;

        // Initialize attack HitBoxes
        attackHitBoxLeft = new HitBox(x - Constants.TILE_SIZE, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);
        attackHitBoxRight = new HitBox(x + Constants.TILE_SIZE, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);
        attackHitBoxUp = new HitBox(x, y + Constants.TILE_SIZE * 2, Constants.TILE_SIZE, Constants.TILE_SIZE);
        attackHitBoxDown = new HitBox(x, y - Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);

        shapeRenderer = new ShapeRenderer();
    }

    public void update(float delta, List<WallTile> wallTiles, List<Enemy> enemies) {
        if (attackState != Enums.AttackState.ATTACKING) {
            float oldX = hitbox.getX(), oldY = hitbox.getY(); // Store old position to revert if collision occurs
            boolean isMoving = false;

            // Handle movement input
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                //y += speed * delta;
                hitbox.setY(hitbox.getY() + speed * delta);
                direction = Enums.Direction.UP;
                isMoving = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                //y -= speed * delta;
                hitbox.setY(hitbox.getY() - speed * delta);
                direction = Enums.Direction.DOWN;
                isMoving = true;
            }

            //setPosition(x, y);
            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            for (WallTile wallTile : wallTiles) {
                if (hitbox.overlaps(wallTile)) {
                    hitbox.setPosition(oldX, oldY);
                    break;
                }
            }

            oldX = hitbox.getX();
            oldY = hitbox.getY();


            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                //x -= speed * delta;
                hitbox.setX(hitbox.getX() - speed * delta);
                direction = Enums.Direction.LEFT;
                isMoving = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                //x += speed * delta;
                hitbox.setX(hitbox.getX() + speed * delta);
                direction = Enums.Direction.RIGHT;
                isMoving = true;
            }

            //setPosition(x, y);
            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            for (WallTile wallTile : wallTiles) {
                if (hitbox.overlaps(wallTile)) {
                    hitbox.setPosition(oldX, oldY);
                    break;
                }
            }

            if (isMoving) {
                movementState = Enums.PlayerState.WALKING;
            } else {
                movementState = Enums.PlayerState.IDLE;
            }
        }

        // Handle attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackState == Enums.AttackState.NOT_ATTACKING) {
           // Gdx.app.log("Player", "Attack initiated");
            attackState = Enums.AttackState.ATTACKING;
            attackDirection = direction;
            stateTime = 0f;
            activateAttackHitBox(enemies);
        }

        // Update attack state
        if (attackState == Enums.AttackState.ATTACKING) {
            stateTime += delta;
            //Gdx.app.log("Player", "Attacking: " + stateTime + "/" + attackRightAnimation.getAnimationDuration());
            if (stateTime > attackRightAnimation.getAnimationDuration()) {
           //     Gdx.app.log("Player", "Attack finished");
                attackState = Enums.AttackState.NOT_ATTACKING;
                stateTime = 0f;
            }
        } else {
            stateTime += delta;
        }
    }

    private void activateAttackHitBox(List<Enemy> enemies) {
        float hitboxX = hitbox.getX();
        float hitboxY = hitbox.getY();

        switch (attackDirection) {
            case LEFT:
                hitboxX = (float) (hitbox.getX() - hitbox.getWidth() - Constants.TILE_SIZE * 2.3);
                attackHitBoxLeft.set(hitboxX, hitbox.getY() - Constants.TILE_SIZE/2, Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 3);
                break;
            case RIGHT:
                hitboxX = hitbox.getX() + hitbox.getWidth();
                attackHitBoxRight.set(hitboxX, hitbox.getY() - Constants.TILE_SIZE/2, Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 3);
                break;
            case UP:
                hitboxY = hitbox.getY() + hitbox.getHeight();
                attackHitBoxUp.set(hitbox.getX()  - Constants.TILE_SIZE, hitboxY, Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 3);
                break;
            case DOWN:
                hitboxY = (float) (hitbox.getY() - hitbox.getHeight() * 2.5);
                attackHitBoxDown.set(hitbox.getX()  - Constants.TILE_SIZE, hitboxY, Constants.TILE_SIZE * 3, Constants.TILE_SIZE * 3);
                break;
        }

        // Check for collision with enemies
        for (Enemy enemy : enemies) {
            if ((attackDirection == Enums.Direction.LEFT && attackHitBoxLeft.overlaps(enemy.getHitbox())) ||
                    (attackDirection == Enums.Direction.RIGHT && attackHitBoxRight.overlaps(enemy.getHitbox())) ||
                    (attackDirection == Enums.Direction.UP && attackHitBoxUp.overlaps(enemy.getHitbox())) ||
                    (attackDirection == Enums.Direction.DOWN && attackHitBoxDown.overlaps(enemy.getHitbox()))) {
                doDamage(enemy);
            }
        }
    }

    public int getSpeed() {
        return (int) speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setOutOfRoomSpeed() {
        this.speed = speedOutOfHall;
    }

    public void resetSpeed() {
        this.speed = speedBase;
    }

    private void doDamage(Enemy enemy) {
        // Implement damage logic here
        enemy.takeDamage(10);
        System.out.println("Damage doing " + direction);
    }

public void render(SpriteBatch batch) {
    TextureRegion currentFrame = null;
    Animation<TextureRegion> slashAnimation = null;
    float slashX = hitbox.getX() - Constants.TILE_SIZE, slashY = hitbox.getY() - Constants.TILE_SIZE / 2;

    // Handle movement animation
    if (movementState == Enums.PlayerState.WALKING) {
        currentFrame = direction == Enums.Direction.LEFT ?
                walkLeftAnimation.getKeyFrame(stateTime, true) :
                walkRightAnimation.getKeyFrame(stateTime, true);
    } else {
        currentFrame = idleFrame;
    }

    // Handle attack animation
    if (attackState == Enums.AttackState.ATTACKING) {
        if (attackDirection == null) {
            attackDirection = direction;
        }
        switch (attackDirection) {
            case LEFT:
                slashAnimation = slashLeftAnimation;
                currentFrame = attackLeftAnimation.getKeyFrame(stateTime, false);
                slashX = (float) (hitbox.getX() - hitbox.getWidth() - Constants.TILE_SIZE * 2.3);
                break;
            case RIGHT:
                slashAnimation = slashRightAnimation;
                currentFrame = attackRightAnimation.getKeyFrame(stateTime, false);
                slashX = hitbox.getX() + hitbox.getWidth();
                break;
            case UP:
                slashAnimation = slashUpAnimation;
                currentFrame = attackRightAnimation.getKeyFrame(stateTime, false);
                slashY = hitbox.getY() + hitbox.getHeight();
                break;
            case DOWN:
                slashAnimation = slashDownAnimation;
                currentFrame = attackLeftAnimation.getKeyFrame(stateTime, false);
                slashY = (float) (hitbox.getY() - hitbox.getHeight() * 2.5);
                break;
        }
    } else {
        attackDirection = null; // Reset attack direction when not attacking
    }

    if (currentFrame != null) {
        // Drawing the player sprite
        batch.draw(currentFrame, hitbox.getX(), hitbox.getY(), playerBodyTextureWidth, playerBodyTextureHeight);
    }

    // Render the slash effect
    if (slashAnimation != null && attackState == Enums.AttackState.ATTACKING) {
        float verticalWidth = Constants.TILE_SIZE * 3;
        float verticalHeight = Constants.TILE_SIZE * 3;
        float horizontalWidth = Constants.TILE_SIZE * 3;
        float horizontalHeight = Constants.TILE_SIZE * 3;

        switch (attackDirection) {
            case LEFT:
            case RIGHT:
                batch.draw(slashAnimation.getKeyFrame(stateTime, false), slashX, slashY, horizontalWidth, horizontalHeight);
                break;
            case UP:
            case DOWN:
                batch.draw(slashAnimation.getKeyFrame(stateTime, false), slashX, slashY, verticalWidth, verticalHeight);
                break;
        }
    }

    // Draw hitboxes after sprite batch ends
    batch.end();
    if (Constants.DRAW_HIT_BOXES) {
        drawHitboxes();
    }
    batch.begin();
}

    private void drawHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined); // Set the projection matrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw player hitbox
        shapeRenderer.setColor(1, 0, 0, 1); // Red for player hitbox
        shapeRenderer.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        // Draw attack hitbox if attacking
        if (attackState == Enums.AttackState.ATTACKING) {
            shapeRenderer.setColor(0, 1, 0, 1); // Green for attack hitboxes
            switch (attackDirection) {
                case LEFT:
                    shapeRenderer.rect(attackHitBoxLeft.getX(), attackHitBoxLeft.getY(), attackHitBoxLeft.getWidth(), attackHitBoxLeft.getHeight());
                    break;
                case RIGHT:
                    shapeRenderer.rect(attackHitBoxRight.getX(), attackHitBoxRight.getY(), attackHitBoxRight.getWidth(), attackHitBoxRight.getHeight());
                    break;
                case UP:
                    shapeRenderer.rect(attackHitBoxUp.getX(), attackHitBoxUp.getY(), attackHitBoxUp.getWidth(), attackHitBoxUp.getHeight());
                    break;
                case DOWN:
                    shapeRenderer.rect(attackHitBoxDown.getX(), attackHitBoxDown.getY(), attackHitBoxDown.getWidth(), attackHitBoxDown.getHeight());
                    break;
            }
        }

        shapeRenderer.end();
    }

    public HitBox getHitbox(){
        return hitbox;
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
        slashLeftFrame1.dispose();
        slashLeftFrame2.dispose();
        slashLeftFrame3.dispose();
        slashLeftFrame4.dispose();
        slashLeftFrame5.dispose();
        slashLeftFrame6.dispose();
        slashLeftFrame7.dispose();
        slashLeftFrame8.dispose();
        slashRightFrame1.dispose();
        slashRightFrame2.dispose();
        slashRightFrame3.dispose();
        slashRightFrame4.dispose();
        slashRightFrame5.dispose();
        slashRightFrame6.dispose();
        slashRightFrame7.dispose();
        slashRightFrame8.dispose();
        slashUpFrame1.dispose();
        slashUpFrame2.dispose();
        slashUpFrame3.dispose();
        slashUpFrame4.dispose();
        slashUpFrame5.dispose();
        slashUpFrame6.dispose();
        slashUpFrame7.dispose();
        slashUpFrame8.dispose();
        slashDownFrame1.dispose();
        slashDownFrame2.dispose();
        slashDownFrame3.dispose();
        slashDownFrame4.dispose();
        slashDownFrame5.dispose();
        slashDownFrame6.dispose();
        slashDownFrame7.dispose();
        slashDownFrame8.dispose();
        shapeRenderer.dispose();
    }
}