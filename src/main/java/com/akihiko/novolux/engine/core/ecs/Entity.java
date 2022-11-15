package com.akihiko.novolux.engine.core.ecs;

import java.util.UUID;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Entity {

    private final long id = EntityManager.newEntityId();
    public long getId() {
        return id;
    }

}
