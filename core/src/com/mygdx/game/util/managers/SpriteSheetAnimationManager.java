package com.mygdx.game.util.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

public class SpriteSheetAnimationManager {
    private Map<String, Animation<TextureRegion>> spriteSheetAnimations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    public SpriteSheetAnimationManager() {
        this.spriteSheetAnimations = new HashMap<>();
        this.stateTime = 0f;
    }

    // Add a sprite sheet animation
    public void addAnimation(String animationType, Animation<TextureRegion> animation) {
        spriteSheetAnimations.put(animationType, animation);
    }

    // Set the current animation
    public void setAnimation(String animationType) {
        if (spriteSheetAnimations.containsKey(animationType)) {
            currentAnimation = spriteSheetAnimations.get(animationType);
            stateTime = 0f; // Reset time when switching animations
        }
    }

    // Get the current frame of the sprite sheet animation
    public TextureRegion getCurrentFrame(float deltaTime) {
        if (currentAnimation != null) {
            stateTime += deltaTime;
            return currentAnimation.getKeyFrame(stateTime, true);
        }
        return null;
    }
}
