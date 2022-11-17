package com.akihiko.novolux.engine.core.scene;

import com.akihiko.novolux.ecs.ECSManager;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Scene {

    private final ECSManager ECSManager;

    public Scene(ECSManager ECSManager) {
        this.ECSManager = ECSManager;
    }

    private void update(){

//        float deltaTime = 0.0f;
//        for(ComponentSystem system : this.ECSManager.getComponentSystems()){
//            system.onUpdate(this.ECSManager.queryEntities(system.getDependencies()), deltaTime);
//        }
    }

}
