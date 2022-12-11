package com.akihiko.novolux.engine.core.components.camera;

import com.akihiko.novolux.ecs.Component;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.utils.NovoLuxRuntimeException;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class CameraComponent extends Component {

    public static final int id = Component.generateComponentId();

    @Override
    public int ID() {
        return CameraComponent.id;
    }

    public enum ProjectionType {
        PERSPECTIVE, ORTHOGONAL
    }

    private boolean isMain;
    private ProjectionType projectionType;
    private float aspectRatio;
    private float fov;

    private float zNear;
    private float zFar;

    final Matrix4x4 projectionMatrix;

    Matrix4x4 viewProjectionMatrix;

    public CameraComponent(boolean isMain, ProjectionType projectionType, float aspectRatio, float fov, float zNear, float zFar) {
        this.isMain = isMain;
        this.projectionType = projectionType;
        // Forming a projection matrix:
        projectionMatrix = Matrix4x4.PERSPECTIVE(fov, aspectRatio, zNear, zFar);
    }

    public boolean isMain() {
        return this.isMain;
    }

    public CameraComponent setIsMain(boolean isMain) {
        this.isMain = isMain;
        return this;
    }

    public Matrix4x4 getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4x4 getViewProjectionMatrix() {
        if (viewProjectionMatrix == null)
            throw new NovoLuxRuntimeException("Camera's View Projection Matrix is uninitialized!");
        return viewProjectionMatrix;
    }
}
