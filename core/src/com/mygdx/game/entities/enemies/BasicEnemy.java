package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;

public class BasicEnemy extends Enemy {

    private float speed;
    private float minX, maxX, minY, maxY; // Boundaries for square movement

    public BasicEnemy(float x, float y, float health) {
        super(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, health);
        this.direction = Enums.Direction.RIGHT; // Start moving right
        this.speed = 200;
        loadAnimations();
        // Initialize the corners of the square movement
        minX = x; // Starting x
        maxX = x + Constants.TILE_SIZE * 4; // Width of the square
        minY = y; // Starting y
        maxY = y + Constants.TILE_SIZE * 4; // Height of the square
    }

    @Override
    protected void loadAnimations() {
        // Load idle texture
        Texture idleTexture = new Texture("basicEnemyIdlePlaceHolder.png");

        // Load walk animation frames for each direction
        int frames = 9;
        TextureRegion[] walkLeftFrames = new TextureRegion[frames];
        TextureRegion[] walkRightFrames = new TextureRegion[frames];
        TextureRegion[] walkUpFrames = new TextureRegion[frames];
        TextureRegion[] walkDownFrames = new TextureRegion[frames];

        for (int i = 0; i < frames; i++) {
            walkLeftFrames[i] = new TextureRegion(new Texture("basicEnemyWalkLeftPlaceHolder" + (i + 1) + ".png"));
            walkRightFrames[i] = new TextureRegion(new Texture("basicEnemyWalkRightPlaceHolder" + (i + 1) + ".png"));
            walkUpFrames[i] = new TextureRegion(new Texture("basicEnemyWalkUpPlaceHolder" + (i + 1) + ".png"));
            walkDownFrames[i] = new TextureRegion(new Texture("basicEnemyWalkDownPlaceHolder" + (i + 1) + ".png"));
        }

        Animation<TextureRegion> walkLeftAnimation = new Animation<>(0.15f, walkLeftFrames);
        Animation<TextureRegion> walkRightAnimation = new Animation<>(0.15f, walkRightFrames);
        Animation<TextureRegion> walkUpAnimation = new Animation<>(0.15f, walkUpFrames);
        Animation<TextureRegion> walkDownAnimation = new Animation<>(0.15f, walkDownFrames);

        // Set animations in parent
        setAnimations(idleTexture, walkLeftAnimation, walkRightAnimation, walkUpAnimation, walkDownAnimation);
    }

    @Override
    public void update(float delta) {
        move(delta);
        stateTime += delta;
    }

    @Override
    public void move(float delta) {
        float oldX = x, oldY = y;
        float distance = speed * delta;

        switch (direction) {
            case RIGHT:
                x += distance;
                if (x >= maxX) {
                    x = maxX; // Correct overshooting
                    direction = Enums.Direction.DOWN; // Change direction
                }
                break;
            case DOWN:
                y -= distance;
                if (y <= minY) {
                    y = minY; // Correct overshooting
                    direction = Enums.Direction.LEFT; // Change direction
                }
                break;
            case LEFT:
                x -= distance;
                if (x <= minX) {
                    x = minX; // Correct overshooting
                    direction = Enums.Direction.UP; // Change direction
                }
                break;
            case UP:
                y += distance;
                if (y >= maxY) {
                    y = maxY; // Correct overshooting
                    direction = Enums.Direction.RIGHT; // Change direction
                }
                break;
        }

        System.out.println("Moving " + direction + ": (" + oldX + ", " + oldY + ") -> (" + x + ", " + y + ")");
    }


}