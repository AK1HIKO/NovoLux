package com.akihiko.novolux.engine.core.scene;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.ECSManager;
import com.akihiko.novolux.engine.Application;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class Scene {

    protected final ECSManager ECSManager = new ECSManager();


    public abstract void create();

    public final void update(float deltaTime) {

        this.ECSManager.lifecycleStart();

        for (ComponentSystem system : this.ECSManager.getGeneralComponentSystems()) {
            system.onUpdate(this.ECSManager.queryEntities(system.getDependencies()), deltaTime);
        }

        Application.getGameView().render();

        this.ECSManager.lifecycleEnd();
    }

}
