package com.mygdx.game.util.managers;


import com.mygdx.game.entities.enemies.Enemy;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.util.HitBox;

import java.util.List;

public class AttackManager {

    /**
     *
     * @param attackHitBox hit box to check for collisions with enemies
     * @param enemies to check for collisions with attack
     */
    public void triggerAttackOnEnemies(HitBox attackHitBox, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (attackHitBox.overlaps(enemy.getHitBox())) {
                applyDamage(10, enemy);
            }
        }
    }

    public void triggerAttackOnPlayer(Entity player, List<Entity> enemies) {
        for (Entity enemy : enemies) {
            if (enemy.isAttacking() && player.getHitBox().overlaps(enemy.getAttackHitBox())) {
                applyDamage(10, player);
            }
        }
    }
    public boolean checkHit(Entity attacker, Entity target) {
        return attacker.getHitBox().overlaps(target.getHitBox());
    }

    private void applyDamage(float damage, Enemy enemy) {
        enemy.takeDamage(damage); // Example damage value
    }

    private void applyDamage(float damage, Entity enemy) {
        enemy.getHealthManager().takeDamage(damage); // Example damage value
    }
}