package com.akihiko.novolux.engine.core.components;

import com.akihiko.novolux.ecs.Component;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class TransformComponent extends Component {

    public static final int id = Component.generateComponentId();

    @Override
    public int ID() {
        return TransformComponent.id;
    }


    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;

    public TransformComponent() {
        this(Vector3.ZERO());
    }


    public TransformComponent(Vector3 position) {
        this(position, Quaternion.IDENTITY(), Vector3.ONE());
    }

    public TransformComponent(Vector3 position, Quaternion rotation, Vector3 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void rotate(Quaternion rotation){
        this.rotation = this.rotation.multiply(rotation).normalized();
    }

    public Matrix4x4 getTransformationMatrix(){
        Matrix4x4 translation = Matrix4x4.TRANSLATION(this.position);
        Matrix4x4 rotation = Matrix4x4.ROTATION(this.rotation);
        Matrix4x4 scale = Matrix4x4.SCALE(this.scale);

        return translation.multiply(rotation.multiply(scale));
    }

    public Vector3 getPosition() {
        return position;
    }

    public TransformComponent setPosition(Vector3 position) {
        this.position = position;
        return this;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public TransformComponent setRotation(Quaternion rotation) {
        this.rotation = rotation;
        return this;
    }

    public Vector3 getScale() {
        return scale;
    }

    public TransformComponent setScale(Vector3 scale) {
        this.scale = scale;
        return this;
    }
}
