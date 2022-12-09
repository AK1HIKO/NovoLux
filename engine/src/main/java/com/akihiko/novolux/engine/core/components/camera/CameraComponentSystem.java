package com.akihiko.novolux.engine.core.components.camera;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.ECSRuntimeException;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.utils.NovoLuxRuntimeException;

import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class CameraComponentSystem extends ComponentSystem {

    private static final int id = ComponentSystem.generateComponentSystemId();
    @Override
    public int ID() {
        return CameraComponentSystem.id;
    }

    private CameraComponent mainCamera;

    public CameraComponentSystem() {
        super(new EntitiesQuery(
                TransformComponent.id,
                CameraComponent.id
        ));
    }

    @Override
    public void onUpdate(List<EntityQueryResult> eComponents, float deltaTime) {
        boolean newMain = false;
        for(EntityQueryResult qr : eComponents){
            CameraComponent camera = (CameraComponent) qr.components().get(CameraComponent.id);
            if(camera.isMain()){
                if(newMain) {
                    throw new NovoLuxRuntimeException("Multiple Main Cameras in one scene!");
                }else{
                    mainCamera = camera;
                    newMain = true;
                }
                TransformComponent transform = (TransformComponent) qr.components().get(TransformComponent.id);
                mainCamera.viewProjectionMatrix = calculateViewProjectionMatrix(mainCamera, transform);
            }
        }

    }

    private Matrix4x4 calculateViewProjectionMatrix(CameraComponent camera, TransformComponent transform){
        return camera.projectionMatrix.multiply(Matrix4x4.ROTATION(transform.getRotation().conjugate()).multiply(Matrix4x4.TRANSLATION(transform.getPosition().multiply(-1f))));
    }

    @Override
    public void onDestroy(List<EntityQueryResult> eComponents) {

    }
}
