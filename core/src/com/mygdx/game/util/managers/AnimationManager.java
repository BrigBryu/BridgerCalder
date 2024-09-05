package com.mygdx.game.util.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnimationManager class that handles both sprite sheet and individual frame animations.
 */
public class AnimationManager {

    private Map<String, Animation<TextureRegion>> spriteSheetAnimations;
    private Map<String, List<Texture>> frameListAnimations;
    private String currentAnimationType;
    private Animation<TextureRegion> currentSpriteSheetAnimation;
    private List<Texture> currentFrameList;
    private int currentFrameIndex;
    private float stateTime;
    private float frameDuration;

    public AnimationManager() {
        this.spriteSheetAnimations = new HashMap<>();
        this.frameListAnimations = new HashMap<>();
        this.stateTime = 0f;
        this.currentFrameIndex = 0;
    }

    /**
     * Adds a frame list animation.
     * @param animationType The animation name.
     * @param frames List of frames.
     * @param frameDuration Time per frame.
     */
    public void addAnimation(String animationType, List<Texture> frames, float frameDuration) {
        frameListAnimations.put(animationType, frames);
        this.frameDuration = frameDuration; // Store frame duration for frame list animations
    }

    /**
     * Adds a sprite sheet animation.
     * @param animationType The animation name.
     * @param animation The sprite sheet animation.
     */
    public void addSpriteSheetAnimation(String animationType, Animation<TextureRegion> animation) {
        spriteSheetAnimations.put(animationType, animation);
    }

    /**
     * Sets the current animation.
     * @param animationType The animation to set.
     */
    public void setAnimation(String animationType) {
        this.currentAnimationType = animationType;
        if (spriteSheetAnimations.containsKey(animationType)) {
            currentSpriteSheetAnimation = spriteSheetAnimations.get(animationType);
            currentFrameList = null; // Deactivate frame list animation
            stateTime = 0f; // Reset state time for sprite sheet animations
        } else if (frameListAnimations.containsKey(animationType)) {
            currentFrameList = frameListAnimations.get(animationType);
            currentSpriteSheetAnimation = null; // Deactivate sprite sheet animation
            currentFrameIndex = 0;
            stateTime = 0f; // Reset state time for frame list animations
        }
    }

    /**
     * Gets the current frame from the sprite sheet animation.
     * @param deltaTime Time since the last frame.
     * @return The current frame.
     */
    public TextureRegion getCurrentFrame(float deltaTime) {
        if (currentSpriteSheetAnimation != null) {
            stateTime += deltaTime;
            return currentSpriteSheetAnimation.getKeyFrame(stateTime, true);
        }
        return null;
    }

    /**
     * Gets the current frame from the frame list animation.
     * @param deltaTime Time since the last frame.
     * @return The current frame.
     */
    public Texture getCurrentTexture(float deltaTime) {
        if (currentFrameList != null && !currentFrameList.isEmpty()) {
            stateTime += deltaTime;
            if (stateTime > frameDuration) {
                currentFrameIndex = (currentFrameIndex + 1) % currentFrameList.size(); // Cycle through frames
                stateTime = 0f;
            }
            return currentFrameList.get(currentFrameIndex);
        }
        return null;
    }
}