package com.mygdx.game.util;

import com.badlogic.gdx.graphics.Texture;

public class AssetManager {
//    public static final Texture healthBarBackground = new Texture("HealthBar1.png");
//    public static final Texture healthBarForeground = new Texture("HealthBar2.png");
//    public static final Texture healthBarDamage = new Texture("HealthBar3.png");

    public static final Texture healthBarBackground = new Texture("HealthBarLarge1.png");
    public static final Texture healthBarForeground = new Texture("HealthBarLarge2.png");
    public static final Texture healthBarDamage = new Texture("HealthBarLarge3.png");

    // Dispose method to clean up textures
    public static void dispose() {
        healthBarBackground.dispose();
        healthBarForeground.dispose();
        healthBarDamage.dispose();
    }
}

