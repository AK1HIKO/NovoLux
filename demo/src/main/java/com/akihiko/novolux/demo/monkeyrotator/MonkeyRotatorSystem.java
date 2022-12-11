package com.akihiko.novolux.demo.monkeyrotator;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.Application;
import com.akihiko.novolux.engine.core.components.TagComponent;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.render.MeshRendererComponent;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public class MonkeyRotatorSystem extends ComponentSystem {
    public static final int id = ComponentSystem.generateComponentSystemId();

    @Override
    public int ID() {
        return MonkeyRotatorSystem.id;
    }

    public MonkeyRotatorSystem() {
        super(new EntitiesQuery(
                TransformComponent.id,
                TagComponent.id,
                MeshRendererComponent.id,
                MonkeyRotatorComponent.id
        ));
    }

    private boolean works = false;

    @Override
    public void onUpdate(List<EntityQueryResult> eComponents, float deltaTime) {
        if (Application.getInputManager().isKeyDown(KeyEvent.VK_ENTER))
            works = true;
        else if (Application.getInputManager().isKeyDown(KeyEvent.VK_BACK_SPACE))
            works = false;
        for (EntityQueryResult qr : eComponents) {
            if (!works)
                return;
            TagComponent tag = (TagComponent) qr.components().get(TagComponent.id);
            if (!tag.getTag().equals("monkey"))
                continue;

            TransformComponent transform = (TransformComponent) qr.components().get(TransformComponent.id);
            MonkeyRotatorComponent monkeyRotatorComponent = (MonkeyRotatorComponent) qr.components().get(MonkeyRotatorComponent.id);

            transform.rotate(new Quaternion(Vector3.UP(), monkeyRotatorComponent.degPerSecond * deltaTime));
        }
    }
}
