package com.akihiko.novolux.engine.core.math.tensors.vector;

/**
 * "Vector4"s are used to represent 3D position, scale and direction. Unlike Vector3, it has an additional "W" component,
 * that helps with affine transformation, proper projection, and fast depth-testing.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Vector4 extends Vector<Vector4> {

    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4(float x, float y, float z) {
        this(x, y, z, 1.0f);
    }

    public Vector4(Vector4 copy) {
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
        this.w = copy.w;
    }

    public static Vector4 RIGHT() {
        return new Vector4(1f, 0, 0);
    }

    public static Vector4 LEFT() {
        return new Vector4(-1f, 0, 0);
    }

    public static Vector4 UP() {
        return new Vector4(0, 1f, 0);
    }

    public static Vector4 DOWN() {
        return new Vector4(0, -1f, 0);
    }

    public static Vector4 FORWARD() {
        return new Vector4(0, 0, 1f);
    }

    public static Vector4 BACK() {
        return new Vector4(0, 0, -1f);
    }

    public static Vector4 ONE() {
        return new Vector4(1f, 1f, 1f);
    }

    public static Vector4 ZERO() {
        return new Vector4(0, 0, 0);
    }

    public static Vector4 NULL() {
        return new Vector4(0, 0, 0, 0);
    }

    public float getX() {
        return x;
    }

    public Vector4 setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector4 setY(float y) {
        this.y = y;
        return this;
    }

    public float getZ() {
        return z;
    }

    public Vector4 setZ(float z) {
        this.z = z;
        return this;
    }

    public float getW() {
        return w;
    }

    public Vector4 setW(float w) {
        this.w = w;
        return this;
    }

    @Override
    public Vector4 add(Vector4 b) {
        return new Vector4(this.x + b.x, this.y + b.y, this.z + b.z, this.w + b.w);
    }

    @Override
    public Vector4 subtract(Vector4 b) {
        return new Vector4(this.x - b.x, this.y - b.y, this.z - b.z, this.w - b.w);
    }

    @Override
    public Vector4 multiply(float scalar) {
        return new Vector4(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }

    @Override
    public Vector4 multiply(Vector4 b) {
        return new Vector4(this.x * b.x, this.y * b.y, this.z * b.z, this.w * b.w);
    }

    @Override
    public Vector4 divide(Vector4 b) {
        return new Vector4(this.x / b.x, this.y / b.y, this.z / b.z, this.w / b.w);
    }

    @Override
    public Vector4 divide(float scalar) {
        return new Vector4(this.x / scalar, this.y / scalar, this.z / scalar, this.w / scalar);
    }

    @Override
    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    @Override
    public float dot(Vector4 b) {
        return this.x * b.x + this.y * b.y + this.z * b.z + this.w * b.w;
    }

    public Vector4 cross(Vector4 b) {
        return new Vector4(
                this.y * b.z - this.z * b.y,
                this.z * b.x - this.x * b.z,
                this.x * b.y - this.y * b.x,
                0
        );
    }

    @Override
    public float get(int index) {
        return switch (index) {
            case 0 -> this.x;
            case 1 -> this.y;
            case 2 -> this.z;
            case 3 -> this.w;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Vector4 other))
            return false;

        return x == other.x && y == other.y && z == other.z && w == other.w;
    }
}
