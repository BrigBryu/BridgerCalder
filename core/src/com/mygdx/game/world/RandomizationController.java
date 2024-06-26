package com.mygdx.game.world;

import java.util.Random;

/**
 * Controls the random aspects of the dungeon generation to ensure playability.
 */
public class RandomizationController {
    private Random random;

    public RandomizationController() {
        random = new Random();
    }

    /**
     * Returns a random integer within a range.
     * @param min Minimum value.
     * @param max Maximum value.
     * @return A random integer between min and max.
     */
    public int getRandomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
