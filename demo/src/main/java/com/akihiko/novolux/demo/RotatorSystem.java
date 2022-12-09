package com.akihiko.novolux.demo;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.render.MeshRendererComponent;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public class RotatorSystem extends ComponentSystem {
    public static final int id = ComponentSystem.generateComponentSystemId();

    @Override
    public int ID() {
        return RotatorSystem.id;
    }

    protected RotatorSystem() {
        super(new EntitiesQuery(
                TransformComponent.id,
                MeshRendererComponent.id
        ));
    }

    @Override
    public void onUpdate(List<EntityQueryResult> eComponents, float deltaTime) {
        for(EntityQueryResult qr : eComponents){
            TransformComponent transform = (TransformComponent)qr.components().get(TransformComponent.id);
            transform.rotate(new Quaternion(Vector3.UP(), 0.01f * deltaTime));
        }
    }
}
