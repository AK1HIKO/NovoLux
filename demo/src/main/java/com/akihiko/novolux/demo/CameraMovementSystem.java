package com.akihiko.novolux.demo;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.Application;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponent;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 10/12/22
 */
public class CameraMovementSystem extends ComponentSystem {
    public static final int id = ComponentSystem.generateComponentSystemId();

    @Override
    public int ID() {
        return CameraMovementSystem.id;
    }


    private float sensX;
    private float sensY;

    public CameraMovementSystem(float sensX, float sensY) {
        super(new EntitiesQuery(
                TransformComponent.id,
                CameraComponent.id
        ));
        this.sensX = sensX;
        this.sensY = sensY;
    }

    @Override
    public void onUpdate(List<EntityQueryResult> eComponents, float deltaTime) {
        for (EntityQueryResult qr : eComponents) {
            TransformComponent transform = (TransformComponent) qr.components().get(TransformComponent.id);

            if (Application.getInputManager().isKeyDown(KeyEvent.VK_W))
                transform.translate(Vector3.FORWARD().multiply(deltaTime * 5f));
            if (Application.getInputManager().isKeyDown(KeyEvent.VK_S))
                transform.translate(Vector3.BACK().multiply(deltaTime * 5f));
            if (Application.getInputManager().isKeyDown(KeyEvent.VK_A))
                transform.translate(Vector3.LEFT().multiply(deltaTime * 5f));
            if (Application.getInputManager().isKeyDown(KeyEvent.VK_D))
                transform.translate(Vector3.RIGHT().multiply(deltaTime * 5f));

            if (Application.getInputManager().isKeyDown(KeyEvent.VK_UP))
                transform.rotate(new Quaternion(Vector3.LEFT(), sensY * deltaTime));
            else if (Application.getInputManager().isKeyDown(KeyEvent.VK_DOWN))
                transform.rotate(new Quaternion(Vector3.RIGHT(), sensY * deltaTime));
            else if (Application.getInputManager().isKeyDown(KeyEvent.VK_LEFT))
                transform.rotate(new Quaternion(Vector3.DOWN(), sensX * deltaTime));
            else if (Application.getInputManager().isKeyDown(KeyEvent.VK_RIGHT))
                transform.rotate(new Quaternion(Vector3.UP(), sensX * deltaTime));
        }
    }
}
