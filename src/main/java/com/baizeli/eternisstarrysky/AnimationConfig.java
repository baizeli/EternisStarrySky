package com.baizeli.eternisstarrysky;

import java.io.*;
import java.util.Properties;

public class AnimationConfig {
    public static float BLOCK_X_ROTATION = 0F;
    public static float BLOCK_Y_ROTATION = 0F;
    public static float BLOCK_Z_ROTATION = 0F;
    public static float TRANSLATE_X = 0.0F;
    public static float TRANSLATE_Y = 0.0F;
    public static float TRANSLATE_Z = 0.0F;

    private static final String CONFIG_FILE = "config/block_animation_debug.properties";
    private static final float ADJUSTMENT_STEP = 0.02F;

    public static void adjustXRotation(float amount) {
        BLOCK_X_ROTATION += amount;
        saveConfig();
    }

    public static void adjustYRotation(float amount) {
        BLOCK_Y_ROTATION += amount;
        saveConfig();
    }

    public static void adjustZRotation(float amount) {
        BLOCK_Z_ROTATION += amount;
        saveConfig();
    }

    public static float getAdjustmentStep() {
        return ADJUSTMENT_STEP;
    }

    public static void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                saveConfig();
                return;
            }

            Properties props = new Properties();
            props.load(new FileInputStream(configFile));

            BLOCK_X_ROTATION = Float.parseFloat(props.getProperty("blockXRotation", "36.0"));
            BLOCK_Y_ROTATION = Float.parseFloat(props.getProperty("blockYRotation", "36.0"));
            BLOCK_Z_ROTATION = Float.parseFloat(props.getProperty("blockZRotation", "36.0"));
            TRANSLATE_X = Float.parseFloat(props.getProperty("translateX", "0.0"));
            TRANSLATE_Y = Float.parseFloat(props.getProperty("translateY", "0.0"));
            TRANSLATE_Z = Float.parseFloat(props.getProperty("translateZ", "0.0"));

        } catch (Exception e) {
            System.err.println("Failed to load animation config: " + e.getMessage());
        }
    }

    public static void saveConfig() {
        try {
            Properties props = new Properties();
            props.setProperty("blockXRotation", String.valueOf(BLOCK_X_ROTATION));
            props.setProperty("blockYRotation", String.valueOf(BLOCK_Y_ROTATION));
            props.setProperty("blockZRotation", String.valueOf(BLOCK_Z_ROTATION));
            props.setProperty("translateX", String.valueOf(TRANSLATE_X));
            props.setProperty("translateY", String.valueOf(TRANSLATE_Y));
            props.setProperty("translateZ", String.valueOf(TRANSLATE_Z));

            File configFile = new File(CONFIG_FILE);
            configFile.getParentFile().mkdirs();
            props.store(new FileOutputStream(configFile), "Block Animation Debug Config - Real-time adjustable");

        } catch (Exception e) {
            System.err.println("Failed to save animation config: " + e.getMessage());
        }
    }

    public static void resetToDefaults() {
        BLOCK_X_ROTATION = 0F;
        BLOCK_Y_ROTATION = 0F;
        BLOCK_Z_ROTATION = 0F;
        TRANSLATE_X = 0.0F;
        TRANSLATE_Y = 0.0F;
        TRANSLATE_Z = 0.0F;
        saveConfig();
    }
}