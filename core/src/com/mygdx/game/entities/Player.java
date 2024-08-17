package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.util.InteractiveHitBox;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;

public class Player {

    private static final float PLAYER_ANIMATION_SPEED = 0.15f;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackRight1Animation;
    private Animation<TextureRegion> attackRight2Animation;
    private Animation<TextureRegion> attackLeft1Animation;
    private Animation<TextureRegion> attackLeft2Animation;
    private Animation<TextureRegion> attackDown1Animation;
    private Animation<TextureRegion> attackDown2Animation;
    private Animation<TextureRegion> attackUp1Animation;
    private Animation<TextureRegion> attackUp2Animation;

    private float stateTime;
    private Enums.Direction direction;
    private Enums.PlayerState movementState;
    private Enums.AttackState attackState;
    private Enums.Direction attackDirection = null;
    private boolean useAttack1 = true;

    private float speed = 0;
    private float speedBase = 350;
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
        hitbox = new HitBox(x, y, Constants.TILE_SIZE * 0.8f, Constants.TILE_SIZE);

        // Load the sprite sheet
        Texture spriteSheet = new Texture(Gdx.files.internal("fancyArt/player/Warrior_BlueUpdatedSizing.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / 6, spriteSheet.getHeight() / 8);

        // Use loops to assign frames to animations
        TextureRegion[] idleFrames = new TextureRegion[6];
        TextureRegion[] walkFrames = new TextureRegion[6];
        TextureRegion[] attackRight1Frames = new TextureRegion[6];
        TextureRegion[] attackRight2Frames = new TextureRegion[6];
        TextureRegion[] attackLeft1Frames = new TextureRegion[6];
        TextureRegion[] attackLeft2Frames = new TextureRegion[6];
        TextureRegion[] attackDown1Frames = new TextureRegion[6];
        TextureRegion[] attackDown2Frames = new TextureRegion[6];
        TextureRegion[] attackUp1Frames = new TextureRegion[6];
        TextureRegion[] attackUp2Frames = new TextureRegion[6];

        for (int i = 0; i < 6; i++) {
            idleFrames[i] = tmp[0][i];
            walkFrames[i] = tmp[1][i];
            attackRight1Frames[i] = tmp[2][i];
            attackRight2Frames[i] = tmp[3][i];
            attackDown1Frames[i] = tmp[4][i];
            attackDown2Frames[i] = tmp[5][i];
            attackUp1Frames[i] = tmp[6][i];
            attackUp2Frames[i] = tmp[7][i];

            // Copy and flip right attack frames for left attack animations
            attackLeft1Frames[i] = new TextureRegion(attackRight1Frames[i]);
            attackLeft1Frames[i].flip(true, false);
            attackLeft2Frames[i] = new TextureRegion(attackRight2Frames[i]);
            attackLeft2Frames[i].flip(true, false);
        }

        idleAnimation = createAnimation(idleFrames, PLAYER_ANIMATION_SPEED);
        walkAnimation = createAnimation(walkFrames, PLAYER_ANIMATION_SPEED);
        attackRight1Animation = createAnimation(attackRight1Frames, PLAYER_ANIMATION_SPEED);
        attackRight2Animation = createAnimation(attackRight2Frames, PLAYER_ANIMATION_SPEED);
        attackLeft1Animation = createAnimation(attackLeft1Frames, PLAYER_ANIMATION_SPEED);
        attackLeft2Animation = createAnimation(attackLeft2Frames, PLAYER_ANIMATION_SPEED);
        attackDown1Animation = createAnimation(attackDown1Frames, PLAYER_ANIMATION_SPEED);
        attackDown2Animation = createAnimation(attackDown2Frames, PLAYER_ANIMATION_SPEED);
        attackUp1Animation = createAnimation(attackUp1Frames, PLAYER_ANIMATION_SPEED);
        attackUp2Animation = createAnimation(attackUp2Frames, PLAYER_ANIMATION_SPEED);

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

    /**
     * Make a Animation<TextureRegion>
     * @param frames for animation
     * @param frameDuration normally PLAYER_ANIMATION_SPEED but needs to be updated to change with speed
     * @return animation
     */
    private Animation<TextureRegion> createAnimation(TextureRegion[] frames, float frameDuration) {
        return new Animation<TextureRegion>(frameDuration, frames);
    }

    public void updateMap(float delta, List<WallTile> wallTiles, List<Enemy> enemies, List<InteractiveHitBox> hitBoxes) {
        if (attackState != Enums.AttackState.ATTACKING) {
            float oldX = hitbox.getX(), oldY = hitbox.getY(); // Store old position to revert if collision occurs
            boolean isMoving = false;

            // Handle movement input
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                hitbox.setY(hitbox.getY() + speed * delta);
                direction = Enums.Direction.UP;
                isMoving = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                hitbox.setY(hitbox.getY() - speed * delta);
                direction = Enums.Direction.DOWN;
                isMoving = true;
            }

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
                hitbox.setX(hitbox.getX() - speed * delta);
                direction = Enums.Direction.LEFT;
                isMoving = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                hitbox.setX(hitbox.getX() + speed * delta);
                direction = Enums.Direction.RIGHT;
                isMoving = true;
            }

            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            //Check for intersections or actions to happen when moving
            if(Constants.wallIntersectionsOn && wallTiles != null) {
                for (WallTile wallTile : wallTiles) {
                    if (hitbox.overlaps(wallTile)) {
                        hitbox.setPosition(oldX, oldY);
                        break;
                    }
                }
            }


            if(hitBoxes != null) {
                for (InteractiveHitBox hitBox : hitBoxes) {
                    if (hitBox.overlaps(this.hitbox)) {
                        System.out.println("Doing interaction after overlaps return true");
                        break;
                    }
                }
            }

            if (isMoving) {
                movementState = Enums.PlayerState.WALKING;
            } else {
                movementState = Enums.PlayerState.IDLE;
            }
        }

        // Handle attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.J) && attackState == Enums.AttackState.NOT_ATTACKING) {
            attackState = Enums.AttackState.ATTACKING;
            attackDirection = direction;
            stateTime = 0f;
            useAttack1 = !useAttack1; // Toggle attack animation
            activateAttackHitBox(enemies);
        }

        // Update attack state
        if (attackState == Enums.AttackState.ATTACKING) {
            stateTime += delta;
            if (stateTime > attackRight1Animation.getAnimationDuration()) {
                attackState = Enums.AttackState.NOT_ATTACKING;
                stateTime = 0f;
            }
        } else {
            stateTime += delta;
        }
    }

    //Takes no enemies because this update is for walking around peacul areay

    /**
     * @param delta pased time
     * @param collisionObjects to check for collisions with and not move through they solid
     * @param hitBoxes you walk into triggers the method in them could be open shop go off ship
     */
    public void updateTiled(float delta, List<RectangleMapObject> collisionObjects, List<Enemy> enemies, List<InteractiveHitBox> hitBoxes) {
        if (attackState != Enums.AttackState.ATTACKING) {
            float oldX = hitbox.getX(), oldY = hitbox.getY(); // Store old position to revert if collision occurs
            boolean isMoving = false;

            // Handle movement input
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                hitbox.setY(hitbox.getY() + speed * delta);
                direction = Enums.Direction.UP;
                isMoving = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                hitbox.setY(hitbox.getY() - speed * delta);
                direction = Enums.Direction.DOWN;
                isMoving = true;
            }

            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            for (RectangleMapObject rectangleObject : collisionObjects) { // changed
                if (hitbox.overlaps(rectangleObject.getRectangle())) { // changed
                    hitbox.setPosition(oldX, oldY);
                    break;
                }
            }

            oldX = hitbox.getX();
            oldY = hitbox.getY();

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                hitbox.setX(hitbox.getX() - speed * delta);
                direction = Enums.Direction.LEFT;
                isMoving = true;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                hitbox.setX(hitbox.getX() + speed * delta);
                direction = Enums.Direction.RIGHT;
                isMoving = true;
            }

            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            if (Constants.wallIntersectionsOn && collisionObjects != null) { // changed
                for (RectangleMapObject rectangleObject : collisionObjects) { // changed
                    if (hitbox.overlaps(rectangleObject.getRectangle())) { // changed
                        hitbox.setPosition(oldX, oldY);
                        break;
                    }
                }
            }

            if (hitBoxes != null) {
                for (InteractiveHitBox hitBox : hitBoxes) {
                    if (hitBox.overlaps(this.hitbox)) {
                        System.out.println("Doing interaction after overlaps return true");
                        break;
                    }
                }
            }

            if (isMoving) {
                movementState = Enums.PlayerState.WALKING;
            } else {
                movementState = Enums.PlayerState.IDLE;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J) && attackState == Enums.AttackState.NOT_ATTACKING) {
            attackState = Enums.AttackState.ATTACKING;
            attackDirection = direction;
            stateTime = 0f;
            useAttack1 = !useAttack1;
            activateAttackHitBox(enemies);
        }

        if (attackState == Enums.AttackState.ATTACKING) {
            stateTime += delta;
            if (stateTime > attackRight1Animation.getAnimationDuration()) {
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
        float offset = Constants.TILE_SIZE * 1.5f; // Adjustable offset

        switch (attackDirection) {
            case LEFT:
                attackHitBoxLeft.set(hitboxX - offset, hitboxY - offset / 2, Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                break;
            case RIGHT:
                attackHitBoxRight.set(hitboxX + hitbox.getWidth(), hitboxY - offset / 2, Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                break;
            case UP:
                attackHitBoxUp.set(hitboxX - offset / 2, hitboxY + hitbox.getHeight(), Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                break;
            case DOWN:
                attackHitBoxDown.set(hitboxX - offset / 2, hitboxY - offset * 1.5f, Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                break;
        }

        for (Enemy enemy : enemies) {
            if ((attackDirection == Enums.Direction.LEFT && attackHitBoxLeft.overlaps(enemy.getHitbox())) ||
                    (attackDirection == Enums.Direction.RIGHT && attackHitBoxRight.overlaps(enemy.getHitbox())) ||
                    (attackDirection == Enums.Direction.UP && attackHitBoxUp.overlaps(enemy.getHitbox())) ||
                    (attackDirection == Enums.Direction.DOWN && attackHitBoxDown.overlaps(enemy.getHitbox()))) {
                doDamage(enemy);
            }
        }
    }

    private void doDamage(Enemy enemy) {
        enemy.takeDamage(10);
        System.out.println("Damage doing " + direction);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = null;
        Animation<TextureRegion> attackAnimation = null;

        if (movementState == Enums.PlayerState.WALKING) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        if (attackState == Enums.AttackState.ATTACKING) {
            if (attackDirection == null) {
                attackDirection = direction;
            }
            switch (attackDirection) {
                case LEFT:
                    attackAnimation = useAttack1 ? attackLeft1Animation : attackLeft2Animation;
                    currentFrame = attackAnimation.getKeyFrame(stateTime, false);
                    break;
                case RIGHT:
                    attackAnimation = useAttack1 ? attackRight1Animation : attackRight2Animation;
                    currentFrame = attackAnimation.getKeyFrame(stateTime, false);
                    break;
                case UP:
                    attackAnimation = useAttack1 ? attackUp1Animation : attackUp2Animation;
                    currentFrame = attackAnimation.getKeyFrame(stateTime, false);
                    break;
                case DOWN:
                    attackAnimation = useAttack1 ? attackDown1Animation : attackDown2Animation;
                    currentFrame = attackAnimation.getKeyFrame(stateTime, false);
                    break;
            }
        } else {
            attackDirection = null;
        }

        if (currentFrame != null) {
            batch.draw(currentFrame, hitbox.getX(), hitbox.getY(), playerBodyTextureWidth, playerBodyTextureHeight);
        }

        batch.end();
        if (Constants.DRAW_HIT_BOXES) {
            drawHitboxes();
        }
        batch.begin();
    }

    private void drawHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        if (attackState == Enums.AttackState.ATTACKING) {
            shapeRenderer.setColor(0, 1, 0, 1);
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

    public HitBox getHitbox() {
        return hitbox;
    }

    public void setOutOfRoomSpeed() {
        this.speed = speedOutOfHall;
    }

    public void resetSpeed() {
        this.speed = speedBase;
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}