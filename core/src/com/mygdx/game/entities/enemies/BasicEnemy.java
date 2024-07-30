package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;

public class BasicEnemy extends Enemy {

    private static final float ENEMY_ANIMATION_SPEED = 0.15f;
    private static final float SCALE_FACTOR = 3f; // Scaling factor to make the sprite look bigger

    private Animation<TextureRegion> attackUpAnimation;
    private Animation<TextureRegion> attackDownAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;

    private Animation<TextureRegion> currentAnimation;
    private float speed;
    private float minX, maxX, minY, maxY; // Boundaries for square movement

    public BasicEnemy(float x, float y, float health, OrthographicCamera camera) {
        super(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, health, camera);
        this.direction = Enums.Direction.RIGHT; // Start moving right
        this.speed = 50;
        loadAnimations();
        currentAnimation = idleAnimation; // Set default animation
        // Initialize the corners of the square movement
        minX = x; // Starting x
        maxX = x + Constants.TILE_SIZE * 4; // Width of the square
        minY = y; // Starting y
        maxY = y + Constants.TILE_SIZE * 4; // Height of the square
    }

    @Override
    protected void loadAnimations() {
        // Load the sprite sheet
        Texture spriteSheet = new Texture("fancyArt/enimies/Torch_Yellow.png");
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / 7, spriteSheet.getHeight() / 5);

        // Use two loops to assign frames to animations
        TextureRegion[] idleFrames = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            idleFrames[i] = tmp[0][i];
        }
        idleAnimation = createAnimation(idleFrames, ENEMY_ANIMATION_SPEED);

        TextureRegion[] attackRightFrames = new TextureRegion[6];
        TextureRegion[] attackLeftFrames = new TextureRegion[6];
        TextureRegion[] attackDownFrames = new TextureRegion[6];
        TextureRegion[] attackUpFrames = new TextureRegion[6];
        TextureRegion[] walkRightFrames = new TextureRegion[6];
        TextureRegion[] walkLeftFrames = new TextureRegion[6];

        for (int i = 0; i < 6; i++) {
            attackRightFrames[i] = tmp[2][i];
            attackLeftFrames[i] = new TextureRegion(tmp[2][i]);
            attackLeftFrames[i].flip(true, false);

            attackDownFrames[i] = tmp[3][i];
            attackUpFrames[i] = tmp[4][i];

            walkRightFrames[i] = tmp[1][i];
            walkLeftFrames[i] = new TextureRegion(tmp[1][i]);
            walkLeftFrames[i].flip(true, false);
        }

        attackRightAnimation = createAnimation(attackRightFrames, ENEMY_ANIMATION_SPEED);
        attackLeftAnimation = createAnimation(attackLeftFrames, ENEMY_ANIMATION_SPEED);
        attackDownAnimation = createAnimation(attackDownFrames, ENEMY_ANIMATION_SPEED);
        attackUpAnimation = createAnimation(attackUpFrames, ENEMY_ANIMATION_SPEED);
        walkRightAnimation = createAnimation(walkRightFrames, ENEMY_ANIMATION_SPEED);
        walkLeftAnimation = createAnimation(walkLeftFrames, ENEMY_ANIMATION_SPEED);

        setAnimations(idleAnimation, walkLeftAnimation, walkRightAnimation, attackUpAnimation, attackDownAnimation);
    }

    private Animation<TextureRegion> createAnimation(TextureRegion[] frames, float frameDuration) {
        return new Animation<>(frameDuration, frames);
    }

    @Override
    public void update(float delta) {
        move(delta);
        stateTime += delta;
    }

    @Override
    public void move(float delta) {
         float distance = speed * delta;

         switch (direction) {
             case RIGHT:
                 hitbox.setX(hitbox.getX() + distance);
                 if (hitbox.getX() >= maxX) {
                     hitbox.setX(maxX); // Correct overshooting
                     direction = Enums.Direction.DOWN; // Change direction
                     setCurrentAnimation(attackRightAnimation);
                 } else {
                     setCurrentAnimation(walkRightAnimation);
                 }
                 break;
             case DOWN:
                 hitbox.setY(hitbox.getY() - distance);
                 if (hitbox.getY() <= minY) {
                     hitbox.setY(minY); // Correct overshooting
                     direction = Enums.Direction.LEFT; // Change direction
                     setCurrentAnimation(attackDownAnimation);
                 } else {
                     setCurrentAnimation(walkRightAnimation);
                 }
                 break;
             case LEFT:
                 hitbox.setX(hitbox.getX() - distance);
                 if (hitbox.getX() <= minX) {
                     hitbox.setX(minX); // Correct overshooting
                     direction = Enums.Direction.UP; // Change direction
                     setCurrentAnimation(attackLeftAnimation);
                 } else {
                     setCurrentAnimation(walkLeftAnimation);
                 }
                 break;
             case UP:
                 hitbox.setY(hitbox.getY() + distance);
                 if (hitbox.getY() >= maxY) {
                     hitbox.setY(maxY); // Correct overshooting
                     direction = Enums.Direction.RIGHT; // Change direction
                     setCurrentAnimation(attackUpAnimation);
                 } else {
                     setCurrentAnimation(walkLeftAnimation);
                 }
                 break;
         }
    }

    private void setCurrentAnimation(Animation<TextureRegion> animation) {
        currentAnimation = animation;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        float width = hitbox.getWidth() * SCALE_FACTOR;
        float height = hitbox.getHeight() * SCALE_FACTOR;
        batch.draw(currentFrame, hitbox.getX() - (width - hitbox.getWidth()) / 2, hitbox.getY() - (height - hitbox.getHeight()) / 2, width, height);
    }
}