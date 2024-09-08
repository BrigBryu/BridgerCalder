package com.mygdx.game.util.managers;

import com.mygdx.game.entities.Entity;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.InteractiveHitBox;
import com.mygdx.game.util.Point;
import com.mygdx.game.world.tiles.WallTile;

import java.util.List;

public class CollisionManager {

    /**
     * resolves and returns true after first found collision
     * @param entity probobly the player to check for collisions
     * @param entities probobly enimies
     * @return if a collision happene
     */
    public boolean checkAndHandleCollisionWith(Entity entity, List<Entity> entities) {
        for (Entity other : entities) {
            if (entity != other && entity.getHitBox().overlaps(other.getHitBox())) {
                resolveCollision(entity, other);
                return true;
            }
        }
        return false;
    }

    public void resolveCollision(Entity entity1, Entity entity2) {
        if (entity1.getHitBox().overlaps(entity2.getHitBox())) {
            // Stop entity1 from moving or apply collision resolution logic TODO
        }
    }

    /**
     * Resolves collisions with hitboxes and returns true if there is a wall collision
     * not sure if the boolean return is needed
     * @param wallTiles
     * @param entity
     * @return true if there is a collision that caused the player to be moved back
     */
    public boolean handleMapCollision(List<WallTile> wallTiles, Entity entity, Point pt){
        if(Constants.wallIntersectionsOn && wallTiles != null) {
            for (WallTile wallTile : wallTiles) {
                if (entity.getHitBox().overlaps(wallTile)) {
                    entity.getHitBox().setPosition(pt.getX(), pt.getY());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean handleMapHitBoxCollisions(List<InteractiveHitBox> hitBoxes, Entity entity) {
        if(hitBoxes != null) {
            for (InteractiveHitBox hitBox : hitBoxes) {
                if (hitBox.overlaps(entity.getHitBox())) {
                    System.out.println("After interaction because overlaps return true on interactive HitBox");
                    return true;
                }
            }
        }
        return false;
    }
}