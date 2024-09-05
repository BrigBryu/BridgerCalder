package com.mygdx.game.util;

import com.mygdx.game.entities.Entity;
import java.util.List;

public class CollisionManager {

    public boolean checkCollision(Entity entity, List<Entity> entities) {
        for (Entity other : entities) {
            if (entity != other && entity.getHitBox().overlaps(other.getHitBox())) {
                return true;
            }
        }
        return false;
    }

    public void resolveCollision(Entity entity1, Entity entity2) {
        // Basic example: stop movement on collision
        if (entity1.getHitBox().overlaps(entity2.getHitBox())) {
            // Stop entity1 from moving or apply collision resolution logic
        }
    }
}