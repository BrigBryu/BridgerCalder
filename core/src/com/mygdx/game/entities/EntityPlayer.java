package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.util.*;
import com.mygdx.game.util.managers.*;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;

public class EntityPlayer extends Entity {

    private static final float PLAYER_ANIMATION_SPEED = 0.15f;

    private Enums.PlayerState movementState;
    private Enums.AttackState attackState;
    private Enums.Direction attackDirection = null;
    private boolean useAttack1 = true;
    private float speedBase = 350;
    private float speedOutOfHall = (float) (speedBase * 2);

    private boolean isInvincible = false;
    private float invincibilityDuration = 1.0f;
    private float invincibilityTimer = 0f;

    private float displayDamageTime = 0;
    private static final float DAMAGE_DISPLAY_DURATION = 0.5f;

    // Attack HitBoxes
    private HitBox attackHitBoxLeft;
    private HitBox attackHitBoxRight;
    private HitBox attackHitBoxUp;
    private HitBox attackHitBoxDown;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    public EntityPlayer(float x, float y, OrthographicCamera camera) {
        super(x, y, Constants.TILE_SIZE * 0.8f, Constants.TILE_SIZE * 2, 100, camera, null);
        this.camera = camera;

        stateTime = 0f;
        direction = Enums.Direction.RIGHT;
        movementState = Enums.PlayerState.IDLE;
        attackState = Enums.AttackState.NOT_ATTACKING;

        attackHitBoxLeft = new HitBox(x - Constants.TILE_SIZE, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);
        attackHitBoxRight = new HitBox(x + Constants.TILE_SIZE, y, Constants.TILE_SIZE, Constants.TILE_SIZE * 2);
        attackHitBoxUp = new HitBox(x, y + Constants.TILE_SIZE * 2, Constants.TILE_SIZE, Constants.TILE_SIZE);
        attackHitBoxDown = new HitBox(x, y - Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void move(float delta) {
        if (isInvincible) {
            invincibilityTimer += delta;
            if (invincibilityTimer >= invincibilityDuration) {
                isInvincible = false;
                invincibilityTimer = 0f;
            }
        }

        if (attackState != Enums.AttackState.ATTACKING) {
            float oldX = hitbox.getX(), oldY = hitbox.getY();
            boolean isMoving = false;

            // Movement
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                hitbox.setY(hitbox.getY() + speedBase * delta);
                direction = Enums.Direction.UP;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                hitbox.setY(hitbox.getY() - speedBase * delta);
                direction = Enums.Direction.DOWN;
                isMoving = true;
            }

            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            if (collisionManager.handleMapCollision(null, this, new Point(oldX, oldY))) {
                hitbox.setPosition(oldX, oldY);
            }

            oldX = hitbox.getX();
            oldY = hitbox.getY();

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                hitbox.setX(hitbox.getX() - speedBase * delta);
                direction = Enums.Direction.LEFT;
                isMoving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                hitbox.setX(hitbox.getX() + speedBase * delta);
                direction = Enums.Direction.RIGHT;
                isMoving = true;
            }

            hitbox.setPosition(hitbox.getX(), hitbox.getY());

            if (collisionManager.handleMapCollision(null, this, new Point(oldX, oldY))) {
                hitbox.setPosition(oldX, oldY);
            }

            if (isMoving) {
                movementState = Enums.PlayerState.WALKING;
                animationManager.setAnimation("walk");
            } else {
                movementState = Enums.PlayerState.IDLE;
                animationManager.setAnimation("idle");
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J) && attackState == Enums.AttackState.NOT_ATTACKING) {
            attackState = Enums.AttackState.ATTACKING;
            attackDirection = direction;
            stateTime = 0f;
            useAttack1 = !useAttack1;
            String attackAnimation = useAttack1 ? "attackRight1" : "attackRight2";
            animationManager.setAnimation(attackAnimation);
            activateAttackHitBox(null); // Pass in the enemies
        }

        if (attackState == Enums.AttackState.ATTACKING) {
            stateTime += delta;
            if (stateTime > animationManager.getCurrentFrame(delta).getRegionWidth()) {
                attackState = Enums.AttackState.NOT_ATTACKING;
                stateTime = 0f;
            }
        }
    }

    @Override
    protected void loadAnimations() {
        Texture spriteSheet = new Texture(Gdx.files.internal("fancyArt/player/Warrior_BlueUpdatedSizing.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / 6, spriteSheet.getHeight() / 8);

        TextureRegion[] idleFrames = new TextureRegion[6];
        TextureRegion[] walkFrames = new TextureRegion[6];
        TextureRegion[] attackRight1Frames = new TextureRegion[6];
        TextureRegion[] attackRight2Frames = new TextureRegion[6];

        for (int i = 0; i < 6; i++) {
            idleFrames[i] = tmp[0][i];
            walkFrames[i] = tmp[1][i];
            attackRight1Frames[i] = tmp[2][i];
            attackRight2Frames[i] = tmp[3][i];
        }

        animationManager.addSpriteSheetAnimation("idle", new Animation<>(PLAYER_ANIMATION_SPEED, idleFrames));
        animationManager.addSpriteSheetAnimation("walk", new Animation<>(PLAYER_ANIMATION_SPEED, walkFrames));
        animationManager.addSpriteSheetAnimation("attackRight1", new Animation<>(PLAYER_ANIMATION_SPEED, attackRight1Frames));
        animationManager.addSpriteSheetAnimation("attackRight2", new Animation<>(PLAYER_ANIMATION_SPEED, attackRight2Frames));
    }

    public void activateAttackHitBox(List<Enemy> enemies) {
        float hitboxX = hitbox.getX();
        float hitboxY = hitbox.getY();
        float offset = Constants.TILE_SIZE * 1.5f;

        switch (attackDirection) {
            case LEFT:
                attackHitBoxLeft.set(hitboxX - offset, hitboxY - offset / 2, Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                attackManager.triggerAttackOnEnemies(attackHitBoxLeft, enemies);
                break;
            case RIGHT:
                attackHitBoxRight.set(hitboxX + hitbox.getWidth(), hitboxY - offset / 2, Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                attackManager.triggerAttackOnEnemies(attackHitBoxRight, enemies);
                break;
            case UP:
                attackHitBoxUp.set(hitboxX - offset / 2, hitboxY + hitbox.getHeight(), Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                attackManager.triggerAttackOnEnemies(attackHitBoxUp, enemies);
                break;
            case DOWN:
                attackHitBoxDown.set(hitboxX - offset / 2, hitboxY - offset * 1.5f, Constants.TILE_SIZE * 2, Constants.TILE_SIZE * 2);
                attackManager.triggerAttackOnEnemies(attackHitBoxDown, enemies);
                break;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animationManager.getCurrentFrame(Gdx.graphics.getDeltaTime());
        if (currentFrame != null) {
            batch.draw(currentFrame, hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());
        }
        drawPlayerHealthBar(batch, camera);

        batch.end();
        if (Constants.DRAW_HIT_BOXES) {
            drawHitboxes();
        }
        batch.begin();
    }

    private void drawPlayerHealthBar(SpriteBatch batch, OrthographicCamera camera) {
        float barWidth = 200f;
        float barHeight = 20f;
        float x = camera.position.x - camera.viewportWidth / 2 + 20f;
        float y = camera.position.y + camera.viewportHeight / 2 - barHeight - 20f;

        float healthPercentage = healthManager.getCurrentHealth() / healthManager.getMaxHealth();

        batch.draw(AssetManager.healthBarBackground, x, y, barWidth, barHeight);
        batch.draw(AssetManager.healthBarForeground, x, y, barWidth * healthPercentage, barHeight);
    }

    public void update(float delta, List<WallTile> wallTiles, List<Enemy> enemies, List<InteractiveHitBox> hitBoxes) {
        // Check movement and attack
        move(delta);

        // Handle collisions with enemies and environment
        if (!isInvincible) {
            //TODO
            //collisionManager.checkAndHandleCollisionWith(this, enemies);
            //attackManager.triggerAttackOnPlayer(this, enemies);
        }

        // Update health and invincibility
        if (healthManager.getCurrentHealth() <= 0) {
            System.out.println("Player has died");
        }

        // Handle interactions with hitboxes
        collisionManager.handleMapHitBoxCollisions(hitBoxes, this);
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

    public void dispose() {
        shapeRenderer.dispose();
    }

    public void setOutOfRoomSpeed() {
        this.speedBase = speedOutOfHall;
    }

    public void resetSpeed() {
        this.speedBase = 350;
    }
}
