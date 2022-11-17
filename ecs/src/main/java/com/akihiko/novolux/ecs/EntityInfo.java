package com.akihiko.novolux.ecs;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class EntityInfo {

    private EntityArchetype archetype;
    private int entityRow;

    public EntityArchetype getArchetype() {
        return archetype;
    }

    public EntityInfo setArchetype(EntityArchetype archetype) {
        this.archetype = archetype;
        return this;
    }


    public int getEntityRow() {
        return entityRow;
    }

    public EntityInfo setEntityRow(int entityRow) {
        this.entityRow = entityRow;
        return this;
    }
}
