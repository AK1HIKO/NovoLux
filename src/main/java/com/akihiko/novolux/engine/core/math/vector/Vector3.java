package com.akihiko.novolux.engine.core.math.vector;

/**
 * "Vector3"s are used to represent 3D position, scale and direction.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Vector3 {

    private float x;
    private float y;
    private float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public final static Vector3 RIGHT = new Vector3(1f, 0, 0);
    public final static Vector3 LEFT = new Vector3(-1f, 0, 0);

    public final static Vector3 UP = new Vector3(0, 1f, 0);
    public final static Vector3 DOWN = new Vector3(0, -1f, 0);

    public final static Vector3 FORWARD = new Vector3(0, 0, 1f);
    public final static Vector3 BACK = new Vector3(0, 0, -1f);

    public final static Vector3 ONE = new Vector3(1f, 1f, 1f);
    public final static Vector3 ZERO = new Vector3(0, 0, 0);

    public float getX() {
        return x;
    }

    public Vector3 setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector3 setY(float y) {
        this.y = y;
        return this;
    }

    public float getZ() {
        return z;
    }

    public Vector3 setZ(float z) {
        this.z = z;
        return this;
    }
}
