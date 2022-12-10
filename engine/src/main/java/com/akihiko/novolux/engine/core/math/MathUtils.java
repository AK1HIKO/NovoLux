package com.akihiko.novolux.engine.core.math;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public final class MathUtils {

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
