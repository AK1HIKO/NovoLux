package com.akihiko.novolux.engine.core.scene;

import com.akihiko.novolux.engine.core.ecs.EntityManager;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Scene {

    private final EntityManager entityManager;

    public Scene(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
