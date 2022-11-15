package com.akihiko.novolux.engine.core.math.quaternion;

/**
 * "Quaternion"s are used to represent rotations. They are easier to compute, interpolate, and they avoid Euler-Lock (Gimbal-Lock).
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Quaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    public static final Quaternion IDENTITY = new Quaternion(0, 0, 0, 1);

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
}
