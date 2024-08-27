package com.mygdx.game.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.entities.Player;
import com.mygdx.game.util.AssetManager;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.Enums;
import com.mygdx.game.util.HitBox;
import com.mygdx.game.world.GameMap;
import com.mygdx.game.world.tiles.Tile;
import com.mygdx.game.world.tiles.WallTile;

import java.util.*;

public abstract class Enemy {
    protected float health;
    private float displayDamageTime = 0;
    private static final float DAMAGE_DISPLAY_DURATION = 0.5f;  // Duration to display the damage bar

    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkLeftAnimation;
    protected Animation<TextureRegion> walkRightAnimation;
    protected Animation<TextureRegion> walkUpAnimation;
    protected Animation<TextureRegion> walkDownAnimation;
    protected float stateTime;

    protected Enums.Direction direction;
    private float previousHealth;

    protected HitBox hitbox;
    private ShapeRenderer shapeRenderer; // Renderer for drawing hitboxes
    private Camera camera;
    protected GameMap map;

    protected float bodyDamage = 10;

    public Enemy(float x, float y, float width, float height, float health, Camera camera, GameMap map) {
        this.hitbox = new HitBox(x, y, width, height);
        this.health = health;
        this.stateTime = 0f;
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;
        this.map = map;
        loadAnimations();
    }

    protected void setAnimations(Animation<TextureRegion> idleAnimation,
                                 Animation<TextureRegion> walkLeftAnimation,
                                 Animation<TextureRegion> walkRightAnimation,
                                 Animation<TextureRegion> walkUpAnimation,
                                 Animation<TextureRegion> walkDownAnimation) {
        this.idleAnimation = idleAnimation;
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
            default:
                currentAnimation = idleAnimation;
                break;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        batch.draw(currentFrame, hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        // Draw the health bar
        drawHealthBar(batch);

        // Draw hitboxes after sprite batch ends
        batch.end();
        drawHitboxes();
        batch.begin();
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
        float barWidth = hitbox.getWidth() * 1.8f;
        float barHeight = barWidth * (AssetManager.healthBarBackground.getHeight() / (float) AssetManager.healthBarBackground.getWidth());
        float barX = hitbox.getX() + (hitbox.getWidth() - barWidth) / 2;
        float barY = hitbox.getY() + hitbox.getHeight() + 10;

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

    private void drawHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined); // Set the projection matrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw enemy hitbox
        shapeRenderer.setColor(1, 1, 0, 1); // Yellow for enemy hitbox
        shapeRenderer.rect(hitbox.getX(), hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        shapeRenderer.end();
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void dispose() {
        if (idleAnimation != null) {
            for (TextureRegion frame : idleAnimation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
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
        shapeRenderer.dispose(); // Dispose the shape renderer
    }

    public Enums.Direction getDirection() {
        return direction;
    }

    public float getX() {
        return hitbox.getX();
    }

    public float getY() {
        return hitbox.getY();
    }



    public abstract void move(float delta, Player player);

    public abstract void update(float delta, Player player);

    protected abstract void loadAnimations();


    //Path finding algorithms

    /**
     * A* Pathfinding Algorithm
     */
    public List<Tile> aStarPathfinding(Tile start, Tile goal, Tile[][] map) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<Tile, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, calculateHeuristic(start, goal));
        openList.add(startNode);
        allNodes.put(start, startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.tile.equals(goal)) {
                return constructPath(current);
            }

            for (Tile neighbor : getNeighbors(current.tile, map)) {
                if (neighbor instanceof WallTile) continue; // Skip walls

                double tentativeG = current.gCost + calculateHeuristic(current.tile, neighbor);

                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor));

                if (tentativeG < neighborNode.gCost) {
                    neighborNode.gCost = tentativeG;
                    neighborNode.fCost = neighborNode.gCost + calculateHeuristic(neighbor, goal);
                    neighborNode.parent = current;

                    if (!openList.contains(neighborNode)) {
                        openList.add(neighborNode);
                        allNodes.put(neighbor, neighborNode);
                    }
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    /**
     * Jump Point Search Algorithm
     */
    public List<Tile> jumpPointSearch(Tile start, Tile goal, Tile[][] map) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<Tile, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, calculateHeuristic(start, goal));
        openList.add(startNode);
        allNodes.put(start, startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.tile.equals(goal)) {
                return constructPath(current);
            }

            List<Tile> jumpPoints = identifySuccessors(current, goal, map);

            for (Tile jumpPoint : jumpPoints) {
                if (jumpPoint instanceof WallTile) continue; // Skip walls

                double tentativeG = current.gCost + calculateHeuristic(current.tile, jumpPoint);

                Node neighborNode = allNodes.getOrDefault(jumpPoint, new Node(jumpPoint));

                if (tentativeG < neighborNode.gCost) {
                    neighborNode.gCost = tentativeG;
                    neighborNode.fCost = neighborNode.gCost + calculateHeuristic(jumpPoint, goal);
                    neighborNode.parent = current;

                    if (!openList.contains(neighborNode)) {
                        openList.add(neighborNode);
                        allNodes.put(jumpPoint, neighborNode);
                    }
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    // Helper methods

    private double calculateHeuristic(Tile a, Tile b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()); // Manhattan distance
    }

    private List<Tile> getNeighbors(Tile tile, Tile[][] map) {
        List<Tile> neighbors = new ArrayList<>();
        int x = (int) tile.getX() / Constants.TILE_SIZE;
        int y = (int) tile.getY() / Constants.TILE_SIZE;

        if (x > 0) neighbors.add(map[x - 1][y]);
        if (x < map.length - 1) neighbors.add(map[x + 1][y]);
        if (y > 0) neighbors.add(map[x][y - 1]);
        if (y < map[0].length - 1) neighbors.add(map[x][y + 1]);

        return neighbors;
    }

    private List<Tile> constructPath(Node node) {
        List<Tile> path = new LinkedList<>();
        while (node != null) {
            path.add(0, node.tile);
            node = node.parent;
        }
        return path;
    }

    private List<Tile> identifySuccessors(Node current, Tile goal, Tile[][] map) {
        List<Tile> jumpPoints = new ArrayList<>();
        for (Tile neighbor : getNeighbors(current.tile, map)) {
            Tile jumpPoint = jump(current.tile, neighbor, goal, map);
            if (jumpPoint != null) {
                jumpPoints.add(jumpPoint);
            }
        }
        return jumpPoints;
    }

    private Tile jump(Tile current, Tile direction, Tile goal, Tile[][] map) {
        int x = (int) direction.getX() / Constants.TILE_SIZE;
        int y = (int) direction.getY() / Constants.TILE_SIZE;

        if (direction instanceof WallTile || x < 0 || y < 0 || x >= map.length || y >= map[0].length) {
            return null;
        }

        if (direction.equals(goal)) {
            return direction;
        }

        if ((x > 0 && map[x - 1][y] instanceof WallTile && !(map[x - 1][y + 1] instanceof WallTile)) ||
                (x < map.length - 1 && map[x + 1][y] instanceof WallTile && !(map[x + 1][y + 1] instanceof WallTile))) {
            return direction;
        }

        if (direction.getX() != current.getX() && direction.getY() != current.getY()) {
            if (jump(new Tile(current.getX(), direction.getY(), direction.getTexture()), direction, goal, map) != null ||
                    jump(new Tile(direction.getX(), current.getY(), direction.getTexture()), direction, goal, map) != null) {
                return direction;
            }
        }

        return jump(direction, new Tile(direction.getX() + (direction.getX() - current.getX()), direction.getY() + (direction.getY() - current.getY()), direction.getTexture()), goal, map);
    }

    /**
     * returns the damage done to player if the player walks into the enemy
     *
     * @return damage to do to the player
     */
    public float getBodyDamage() {
        return bodyDamage;
    }

    public HitBox getHitBox() {
        return hitbox;
    }

    /**
     * Node class used in pathfinding algorithms
     */
    private static class Node {
        Tile tile;
        Node parent;
        double gCost, fCost;

        Node(Tile tile) {
            this.tile = tile;
            this.gCost = Double.POSITIVE_INFINITY;
            this.fCost = Double.POSITIVE_INFINITY;
        }

        Node(Tile tile, Node parent, double gCost, double fCost) {
            this.tile = tile;
            this.parent = parent;
            this.gCost = gCost;
            this.fCost = fCost;
        }
    }
}
