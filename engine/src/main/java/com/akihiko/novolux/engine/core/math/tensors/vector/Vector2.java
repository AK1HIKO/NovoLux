package com.akihiko.novolux.engine.core.math.tensors.vector;


import com.akihiko.novolux.engine.core.math.MathUtils;

/**
 * "Vector2"s are used to represent 2D coordinates for texture mapping and gradients.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Vector2 extends Vector<Vector2> {

    private float x;
    private float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 copy) {
        this(copy.x, copy.y);
    }

    public Vector2(Vector4 v4) {
        this(v4.getX(), v4.getY());
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


    public static Vector2 RIGHT() {
        return new Vector2(1f, 0);
    }

    public static Vector2 LEFT() {
        return new Vector2(-1f, 0);
    }

    public static Vector2 UP() {
        return new Vector2(0, 1f);
    }

    public static Vector2 DOWN() {
        return new Vector2(0, -1f);
    }

    public static Vector2 ONE() {
        return new Vector2(1f, 1f);
    }

    public static Vector2 ZERO() {
        return new Vector2(0, 0);
    }

    @Override
    public Vector2 add(Vector2 b) {
        return new Vector2(this.x + b.x, this.y + b.y);
    }

    @Override
    public Vector2 subtract(Vector2 b) {
        return new Vector2(this.x - b.x, this.y - b.y);
    }

    @Override
    public Vector2 multiply(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    @Override
    public Vector2 multiply(Vector2 b) {
        return new Vector2(this.x * b.x, this.y * b.y);
    }

    @Override
    public Vector2 divide(Vector2 b) {
        return new Vector2(this.x / b.x, this.y / b.y);
    }

    @Override
    public Vector2 divide(float scalar) {
        return new Vector2(this.x / scalar, this.y / scalar);
    }

    public Vector2 clamp(Vector2 min, Vector2 max) {
        return new Vector2(
                MathUtils.clamp(this.x, min.x, max.x),
                MathUtils.clamp(this.y, min.y, max.y)
        );
    }

    @Override
    public float dot(Vector2 b) {
        return this.x * b.x + this.y * b.y;
    }

    @Override
    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    @Override
    public float get(int index) {
        return switch (index) {
            case 0 -> this.x;
            case 1 -> this.y;
            default -> throw new IndexOutOfBoundsException();
        };
    }


    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Vector2 other))
            return false;

        return x == other.x && y == other.y;
    }
}
