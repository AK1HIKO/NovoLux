package com.akihiko.novolux.engine.core.math.tensors.vector;

import com.akihiko.novolux.engine.core.math.MathUtils;
import com.akihiko.novolux.engine.core.math.tensors.Tensor;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 29/11/22
 */
public abstract class Vector<T extends Vector> implements Tensor<T> {

    public abstract float dot(T b);

    /**
     * Linear Interpolation of the current vector to another vector.
     *
     * @param b end vector.
     * @param t interpolant - percentage of the interpolation (0 - current vector, 0.5 - halfway between current and end vector, 1 - end vector).
     * @return interpolated vector.
     */
    public T lerp(T b, float t) {
        return (T) b.subtract(this).multiply(t).add(this);
    }

    /**
     * Spherical Linear Interpolation of the current vector to another vector.
     *
     * @param b end vector.
     * @param t interpolant - percentage of the interpolation (0 - current vector, 0.5 - halfway between current and end vector, 1 - end vector).
     * @return interpolated vector.
     */
    public T slerp(T b, float t) {
        float dot = MathUtils.clamp(this.dot(b), -1f, 1f);
        float theta = (float) Math.acos(dot) * t;
        T relative = (T) b.subtract(this.multiply(dot)).normalized();
        return (T) (
                (this.multiply((float) Math.cos(theta))).add
                        (relative.multiply((float) Math.sin(theta)))
        );
    }

    /**
     * Normalized Linear Interpolation of the current vector to another vector. Almost the same as SLerp, but much faster at a cost of variable velocity.
     *
     * @param b end vector.
     * @param t interpolant - percentage of the interpolation (0 - current vector, 0.5 - halfway between current and end vector, 1 - end vector).
     * @return interpolated vector.
     */
    public T nlerp(T b, float t) {
        return (T) this.lerp(b, t).normalized();
    }

    /**
     * Calculates the length of a vector.
     *
     * @return norm or length of a vector.
     */
    public abstract float magnitude();

    @Override
    public T normalized() {
        return this.divide(this.magnitude());
    }

    public abstract float get(int index);
}
