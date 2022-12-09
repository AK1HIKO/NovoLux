package com.akihiko.novolux.engine.core.math.tensors;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 29/11/22
 */
public interface Tensor<T extends Tensor> {

    T add(T b);

    T subtract(T b);

    T multiply(T b);
    T multiply(float scalar);

    T divide(T b);
    T divide(float scalar);

    T normalized();

}
