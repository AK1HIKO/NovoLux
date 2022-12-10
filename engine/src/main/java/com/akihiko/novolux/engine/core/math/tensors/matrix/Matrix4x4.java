package com.akihiko.novolux.engine.core.math.tensors.matrix;

import com.akihiko.novolux.engine.core.math.tensors.Tensor;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Matrix4x4 implements Tensor<Matrix4x4> {

    private final float[][] m;

    public Matrix4x4() {
        this.m = new float[4][4];
    }

    public Matrix4x4(float[][] m) {
        this.m = m;
    }

    public float[][] getMatrix() {
        return m;
    }

    @Override
    public Matrix4x4 add(Matrix4x4 b) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = this.m[i][j] + b.m[i][j];
            }
        }
        return result;
    }

    @Override
    public Matrix4x4 subtract(Matrix4x4 b) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = this.m[i][j] - b.m[i][j];
            }
        }
        return result;
    }

    public Matrix4x4 multiply(Matrix4x4 b) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    result.m[i][j] += this.m[i][k] * b.m[k][j];
                }
            }
        }
        return result;
    }

    @Override
    public Matrix4x4 multiply(float scalar) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = this.m[i][j] * scalar;
            }
        }
        return result;
    }

    @Override
    public Matrix4x4 divide(Matrix4x4 b) {
        // TODO: Invert the matrix and multiply.
        throw new UnsupportedOperationException();
    }

    @Override
    public Matrix4x4 divide(float scalar) {
        Matrix4x4 result = new Matrix4x4();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.m[i][j] = this.m[i][j] / scalar;
            }
        }
        return result;
    }

    @Override
    public Matrix4x4 normalized() {
        // TODO: Calculate the determinant and divide each element by it.
        throw new UnsupportedOperationException();
    }

    public Vector4 transform(Vector4 b) {
        return new Vector4(
                this.m[0][0] * b.getX() + this.m[0][1] * b.getY() + this.m[0][2] * b.getZ() + this.m[0][3] * b.getW(),
                this.m[1][0] * b.getX() + this.m[1][1] * b.getY() + this.m[1][2] * b.getZ() + this.m[1][3] * b.getW(),
                this.m[2][0] * b.getX() + this.m[2][1] * b.getY() + this.m[2][2] * b.getZ() + this.m[2][3] * b.getW(),
                this.m[3][0] * b.getX() + this.m[3][1] * b.getY() + this.m[3][2] * b.getZ() + this.m[3][3] * b.getW()
        );
    }


    public static Matrix4x4 IDENTITY() {
        return new Matrix4x4(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 WORLD_TO_SCREEN(float halfW, float halfH) {
        return new Matrix4x4(new float[][]{
                {halfW, 0, 0, halfW - 0.5f}, // -0.5f to compensate "rounding", that can overflow screen width
                {0, -halfH, 0, halfH - 0.5f}, // -0.5f to compensate "rounding", that can overflow screen height
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 TRANSLATION(Vector3 position) {
        return Matrix4x4.TRANSLATION(position.getX(), position.getY(), position.getZ());
    }

    public static Matrix4x4 TRANSLATION(float x, float y, float z) {
        return new Matrix4x4(new float[][]{
                {1, 0, 0, x},
                {0, 1, 0, y},
                {0, 0, 1, z},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 EULER_ROTATION(float x, float y, float z) {
        Matrix4x4 rz = new Matrix4x4(new float[][]{
                {(float) Math.cos(z), -(float) Math.sin(z), 0, 0},
                {(float) Math.sin(z), (float) Math.cos(z), 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });

        Matrix4x4 ry = new Matrix4x4(new float[][]{
                {(float) Math.cos(y), 0, -(float) Math.sin(y), 0},
                {0, 1, 0, 0},
                {(float) Math.sin(y), 0, (float) Math.cos(y), 0},
                {0, 0, 0, 1},
        });

        Matrix4x4 rx = new Matrix4x4(new float[][]{
                {1, 0, 0, 0},
                {0, (float) Math.cos(x), -(float) Math.sin(x), 0},
                {0, (float) Math.sin(x), (float) Math.cos(x), 0},
                {0, 0, 0, 1}
        });

        return rz.multiply(ry.multiply(rx));
    }

    public static Matrix4x4 ROTATION(Vector3 fwd, Vector3 up, Vector3 right) {
        return new Matrix4x4(new float[][]{
                {right.getX(), right.getY(), right.getZ(), 0},
                {up.getX(), up.getY(), up.getZ(), 0},
                {fwd.getX(), fwd.getY(), fwd.getZ(), 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 ROTATION(Quaternion quaternion) {
        return Matrix4x4.ROTATION(
                new Vector3(2.0f * (quaternion.getX() * quaternion.getZ() - quaternion.getW() * quaternion.getY()), 2.0f * (quaternion.getY() * quaternion.getZ() + quaternion.getW() * quaternion.getX()), 1.0f - 2.0f * (quaternion.getX() * quaternion.getX() + quaternion.getY() * quaternion.getY())),
                new Vector3(2.0f * (quaternion.getX() * quaternion.getY() + quaternion.getW() * quaternion.getZ()), 1.0f - 2.0f * (quaternion.getX() * quaternion.getX() + quaternion.getZ() * quaternion.getZ()), 2.0f * (quaternion.getY() * quaternion.getZ() - quaternion.getW() * quaternion.getX())),
                new Vector3(1.0f - 2.0f * (quaternion.getY() * quaternion.getY() + quaternion.getZ() * quaternion.getZ()), 2.0f * (quaternion.getX() * quaternion.getY() - quaternion.getW() * quaternion.getZ()), 2.0f * (quaternion.getX() * quaternion.getZ() + quaternion.getW() * quaternion.getY()))
        );
    }

    public static Matrix4x4 SCALE(Vector3 scale) {
        return Matrix4x4.SCALE(scale.getX(), scale.getY(), scale.getZ());
    }

    public static Matrix4x4 SCALE(float x, float y, float z) {
        return new Matrix4x4(new float[][]{
                {x, 0, 0, 0},
                {0, y, 0, 0},
                {0, 0, z, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix4x4 PERSPECTIVE(float fovDeg, float aspectRatio, float near, float far) {
        float invTanHfov = 1.0f / (float) Math.tan(Math.toRadians(fovDeg) / 2);
        float zSpace = near - far;

        // Reference: https://ogldev.org/www/tutorial12/tutorial12.html
        return new Matrix4x4(new float[][]{
                {invTanHfov / aspectRatio, 0, 0, 0},
                {0, invTanHfov, 0, 0},
                {0, 0, -(near + far) / zSpace, 2 * far * near / zSpace},
                {0, 0, 1, 0}
        });
    }
}
