package com.akihiko.novolux.engine.core.math.tensors.quaternion;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;

/**
 * "Quaternion"s are used to represent rotations. They are easier to compute, interpolate, and they avoid Euler-Lock (Gimbal-Lock). <br/>
 * For better mathematical explanations of the provided functions, see: <a href="https://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/functions/index.htm">Quaternion Functions</a>.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Quaternion extends Vector<Quaternion> {

    private float x;
    private float y;
    private float z;
    private float w;

    public static Quaternion IDENTITY() {
        return new Quaternion(0, 0, 0, 1);
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Vector3 axis, float angleDeg) {
        float angleRad = (float) Math.toRadians(angleDeg / 2);
        float sin = (float) Math.sin(angleRad);

        this.x = axis.getX() * sin;
        this.y = axis.getY() * sin;
        this.z = axis.getZ() * sin;
        this.w = (float) Math.cos(angleRad);
    }

    @Override
    public float dot(Quaternion b) {
        return this.x * b.x + this.y * b.y + this.z * b.z + this.w * b.w;
    }

    @Override
    public float magnitude() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public Quaternion conjugate() {
        return new Quaternion(-this.x, -this.y, -this.z, this.w);
    }

    @Override
    public Quaternion add(Quaternion b) {
        return new Quaternion(this.x + b.x, this.y + b.y, this.z + b.z, this.w + b.w);
    }

    @Override
    public Quaternion subtract(Quaternion b) {
        return new Quaternion(this.x - b.x, this.y - b.y, this.z - b.z, this.w - b.w);
    }

    public Quaternion multiply(Vector3 b) {
        return new Quaternion(
                this.w * b.getX() + this.y * b.getZ() - this.z * b.getY(),
                this.w * b.getY() + this.z * b.getX() - this.x * b.getZ(),
                this.w * b.getZ() + this.x * b.getY() - this.y * b.getX(),
                -this.x * b.getX() - this.y * b.getY() - this.z * b.getZ()
        );
    }

    @Override
    public Quaternion multiply(Quaternion b) {
        return new Quaternion(
                this.x * b.w + this.w * b.x + this.y * b.z - this.z * b.y,
                this.y * b.w + this.w * b.y + this.z * b.x - this.x * b.z,
                this.z * b.w + this.w * b.z + this.x * b.y - this.y * b.x,
                this.w * b.w - this.x * b.x - this.y * b.y - this.z * b.z
        );
    }

    @Override
    public Quaternion multiply(float scalar) {
        return new Quaternion(
                this.x * scalar,
                this.y * scalar,
                this.z * scalar,
                this.w * scalar
        );
    }

    @Override
    public Quaternion divide(Quaternion b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Quaternion divide(float scalar) {
        return new Quaternion(
                this.x / scalar,
                this.y / scalar,
                this.z / scalar,
                this.w / scalar
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }
}
