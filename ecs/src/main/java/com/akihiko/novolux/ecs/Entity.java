package com.akihiko.novolux.ecs;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Entity {

    private final long id = ECSManager.generateEntityId();
    public long getId() {
        return id;
    }

    private EntityInfo entityInfo;

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public Entity setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
        return this;
    }
}
