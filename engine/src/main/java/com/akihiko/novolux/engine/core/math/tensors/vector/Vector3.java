package com.akihiko.novolux.engine.core.math.tensors.vector;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 28/11/22
 */
public class Vector3 extends Vector<Vector3> {

    private float x;
    private float y;
    private float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 copy){
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
    }

    public static Vector3 RIGHT(){return new Vector3(1f, 0, 0);}
    public static Vector3 LEFT(){return new Vector3(-1f, 0, 0);}

    public static Vector3 UP(){return new Vector3(0, 1f, 0);}
    public static Vector3 DOWN(){return new Vector3(0, -1f, 0);}

    public static Vector3 FORWARD(){return new Vector3(0, 0, 1f);}
    public static Vector3 BACK(){return new Vector3(0, 0, -1f);}

    public static Vector3 ONE(){return new Vector3(1f, 1f, 1f);}
    public static Vector3 ZERO(){return new Vector3(0, 0, 0);}

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

    @Override
    public Vector3 add(Vector3 b){
        return new Vector3(this.x + b.x, this.y + b.y, this.z + b.z);
    }

    @Override
    public Vector3 subtract(Vector3 b){
        return new Vector3(this.x - b.x, this.y - b.y, this.z - b.z);
    }

    @Override
    public Vector3 multiply(float scalar){
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    @Override
    public Vector3 multiply(Vector3 b){
        return new Vector3(this.x * b.x, this.y * b.y, this.z * b.z);
    }

    @Override
    public Vector3 divide(Vector3 b) {
        return new Vector3(this.x/b.x, this.y/b.y, this.z/b.z);
    }

    @Override
    public Vector3 divide(float scalar) {
        return new Vector3(this.x/scalar, this.y/scalar, this.z/scalar);
    }

    @Override
    public float magnitude(){
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    @Override
    public float dot(Vector3 b){
        return this.x*b.x + this.y*b.y + this.z*b.z;
    }

    public Vector3 cross(Vector3 b){
        return new Vector3(
                this.y * b.z - this.z * b.y,
                this.z * b.x - this.x * b.z,
                this.x * b.y - this.y * b.x
        );
    }

    @Override
    public float get(int index) {
        return switch (index) {
            case 0 -> this.x;
            case 1 -> this.y;
            case 2 -> this.z;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public String toString()
    {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Vector3 other))
            return false;

        return x == other.x && y == other.y && z == other.z;
    }

}
