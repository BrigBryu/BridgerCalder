package com.mygdx.game.util.managers;

public class HealthManager {
    private float maxHealth;
    private float currentHealth;

    public HealthManager(float maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void takeDamage(float amount) {
        currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }

    public void heal(float amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }
}
