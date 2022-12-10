package com.akihiko.novolux.demo;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponent;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;

import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 10/12/22
 */
public class CameraShakerSystem extends ComponentSystem {
    public static final int id = ComponentSystem.generateComponentSystemId();

    @Override
    public int ID() {
        return CameraShakerSystem.id;
    }

    protected CameraShakerSystem() {
        super(new EntitiesQuery(
                TransformComponent.id,
                CameraComponent.id
        ));
    }

    @Override
    public void onUpdate(List<EntityQueryResult> eComponents, float deltaTime) {
        for (EntityQueryResult qr : eComponents) {
            TransformComponent transform = (TransformComponent) qr.components().get(TransformComponent.id);
            transform.rotate(new Quaternion(Vector3.UP(), 0.5f * deltaTime));
        }
    }
}
