package com.akihiko.novolux.ecs;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Entity {

    private final long id = ECSManager.generateEntityId();
    public final long getId() {
        return id;
    }

    private EntityInfo entityInfo = null;

    public final EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public final Entity setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
        return this;
    }

    // TODO: Improve:
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Entity other))
            return false;

        return this.id == other.id;
    }
}
