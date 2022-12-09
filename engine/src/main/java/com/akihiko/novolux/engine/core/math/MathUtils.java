package com.akihiko.novolux.engine.core.math;

import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public final class MathUtils {

    @Deprecated
    public static Matrix4x4 getTransformationMatrix(Vector4 position, Vector4 scale, Quaternion rotation){
        Matrix4x4 translationMatrix = position.toTranslationMatrix();
        Matrix4x4 rotationMatrix = rotation.toRotationMatrix();
        Matrix4x4 scaleMatrix = scale.toScaleMatrix();

        return (translationMatrix.multiply(rotationMatrix)).multiply(scaleMatrix);
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
