package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.entities.Player;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.tiles.Tile;

import java.util.List;

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

    private List<Tile> currentPath; // Stores the path generated by A* or JPS
    private int currentPathIndex; // Tracks the current position in the path

    public BasicEnemy(float x, float y, float health, OrthographicCamera camera, GameMap map) {
        super(x, y, Constants.TILE_SIZE, Constants.TILE_SIZE, health, camera, map);
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



    private void setCurrentAnimation(Animation<TextureRegion> animation) {
        currentAnimation = animation;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        float width = hitbox.getWidth() * SCALE_FACTOR;
        float height = hitbox.getHeight() * SCALE_FACTOR;
        batch.draw(currentFrame, hitbox.getX() - (width - hitbox.getWidth()) / 2, hitbox.getY() - (height - hitbox.getHeight()) / 2, width, height);
    }



    public void move(float delta, Player player) {
        System.out.println("Cords of this guy (" + hitbox.getX() + ", " + hitbox.getY() + ")");
        if(map.getTileAt(hitbox.getX(), hitbox.getY()) != null) {
            if (currentPath == null || currentPathIndex >= currentPath.size()) {
                // No path or path completed, generate a new path
                Tile startTile = map.getTileAt(hitbox.getX() / Constants.TILE_SIZE, hitbox.getY() / Constants.TILE_SIZE);
                Tile goalTile = map.getTileAt(player.getHitbox().getX(), player.getHitbox().getY());

                currentPath = aStarPathfinding(startTile, goalTile, map.getTileMap());
                currentPathIndex = 0;
            }

            followPath(delta); // Follow the generated path
        }
    }

    @Override
    public void update(float delta, Player player) {
        stateTime += delta;
        move(delta, player);

    }


    /**
     * Follow the path generated by A* or JPS.
     *
     * @param delta Time since last frame.
     */
    private void followPath(float delta) {
        if (currentPathIndex >= currentPath.size()) {
            currentPath = null; // Path complete
            return;
        }

        Tile nextTile = currentPath.get(currentPathIndex);
        float targetX = nextTile.getX();
        float targetY = nextTile.getY();

        float currentX = getX();
        float currentY = getY();

        // Calculate the direction vector
        float directionX = targetX - currentX;
        float directionY = targetY - currentY;

        // Normalize the direction vector
        float distance = (float) Math.sqrt(directionX * directionX + directionY * directionY);
        if (distance != 0) {
            directionX /= distance;
            directionY /= distance;
        }

        // Move the enemy towards the next tile
        float moveX = directionX * speed * delta;
        float moveY = directionY * speed * delta;

        // Update the enemy's position
        hitbox.setPosition(currentX + moveX, currentY + moveY);

        // Check if the enemy has reached the next tile
        if (Math.abs(currentX - targetX) < speed * delta && Math.abs(currentY - targetY) < speed * delta) {
            currentPathIndex++;
        }

        // Set the appropriate animation based on the direction
        setAnimationDirection(directionX, directionY);
    }

    /**
     * Sets the current path for the enemy to follow.
     *
     * @param path The path generated by A* or JPS.
     */
    public void setPath(List<Tile> path) {
        this.currentPath = path;
        this.currentPathIndex = 0;
    }

    /**
     * Determines the appropriate animation based on the direction of movement.
     */
    private void setAnimationDirection(float directionX, float directionY) {
        if (Math.abs(directionX) > Math.abs(directionY)) {
            if (directionX > 0) {
                setCurrentAnimation(walkRightAnimation);
                direction = Enums.Direction.RIGHT;
            } else {
                setCurrentAnimation(walkLeftAnimation);
                direction = Enums.Direction.LEFT;
            }
        } else {
            if (directionY > 0) {
                setCurrentAnimation(walkUpAnimation);
                direction = Enums.Direction.UP;
            } else {
                setCurrentAnimation(walkDownAnimation);
                direction = Enums.Direction.DOWN;
            }
        }
    }

    private void animationMoveDemonstration(float delta){
        float oldX = hitbox.getX(), oldY = hitbox.getY();
        float distance = speed * delta;

        switch (direction) {
            case RIGHT:
                hitbox.setX(hitbox.getX() + distance);
                if (hitbox.getX() >= maxX) {
                    hitbox.setX(maxX); // Correct overshooting
                    direction = Enums.Direction.DOWN; // Change direction
                }
                break;
            case DOWN:
                hitbox.setY(hitbox.getY() - distance);
                if (hitbox.getY() <= minY) {
                    hitbox.setY(minY); // Correct overshooting
                    direction = Enums.Direction.LEFT; // Change direction
                }
                break;
            case LEFT:
                hitbox.setX(hitbox.getX() - distance);
                if (hitbox.getX() <= minX) {
                    hitbox.setX(minX); // Correct overshooting
                    direction = Enums.Direction.UP; // Change direction
                }
                break;
            case UP:
                hitbox.setY(hitbox.getY() + distance);
                if (hitbox.getY() >= maxY) {
                    hitbox.setY(maxY); // Correct overshooting
                    direction = Enums.Direction.RIGHT; // Change direction
                }
                break;
        }
        float distance1 = speed * delta;

        // System.out.println("Moving " + direction + ": (" + oldX + ", " + oldY + ") -> (" + x + ", " + y + ")");
        switch (direction) {
            case RIGHT:
                hitbox.setX(hitbox.getX() + distance1);
                if (hitbox.getX() >= maxX) {
                    hitbox.setX(maxX); // Correct overshooting
                    direction = Enums.Direction.DOWN; // Change direction
                    setCurrentAnimation(attackRightAnimation);
                } else {
                    setCurrentAnimation(walkRightAnimation);
                }
                break;
            case DOWN:
                hitbox.setY(hitbox.getY() - distance1);
                if (hitbox.getY() <= minY) {
                    hitbox.setY(minY); // Correct overshooting
                    direction = Enums.Direction.LEFT; // Change direction
                    setCurrentAnimation(attackDownAnimation);
                } else {
                    setCurrentAnimation(walkRightAnimation);
                }
                break;
            case LEFT:
                hitbox.setX(hitbox.getX() - distance1);
                if (hitbox.getX() <= minX) {
                    hitbox.setX(minX); // Correct overshooting
                    direction = Enums.Direction.UP; // Change direction
                    setCurrentAnimation(attackLeftAnimation);
                } else {
                    setCurrentAnimation(walkLeftAnimation);
                }
                break;
            case UP:
                hitbox.setY(hitbox.getY() + distance1);
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

}