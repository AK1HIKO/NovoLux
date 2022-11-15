package com.akihiko.novolux.engine.core.math.vector;


/**
 * "Vector3"s are used to represent 2D position, scale and direction.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Vector2 {

    private float x;
    private float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public Vector2 setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector2 setY(float y) {
        this.y = y;
        return this;
    }
}
