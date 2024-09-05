package com.mygdx.game.util.managers;


import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.util.HitBox;

import java.util.List;

public class AttackManager {

    public void triggerAttack(Entity attacker, HitBox attackHitBox, List<Entity> enemies) {
        for (Entity enemy : enemies) {
            if (attackHitBox.overlaps(enemy.getHitBox())) {
                applyDamage(attacker, enemy);
            }
        }
    }

    public boolean checkHit(Entity attacker, Entity target) {
        return attacker.getHitBox().overlaps(target.getHitBox());
    }

    private void applyDamage(Entity attacker, Entity enemy) {
        enemy.getHealthManager().takeDamage(10); // Example damage value
    }
}