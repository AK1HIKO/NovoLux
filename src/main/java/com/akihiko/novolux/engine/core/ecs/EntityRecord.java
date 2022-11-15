package com.akihiko.novolux.engine.core.ecs;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class EntityRecord {

    private EntityArchetype archetype;
    private int entityRow;

    public EntityArchetype getArchetype() {
        return archetype;
    }

    public EntityRecord setArchetype(EntityArchetype archetype) {
        this.archetype = archetype;
        return this;
    }


    public int getEntityRow() {
        return entityRow;
    }

    public EntityRecord setEntityRow(int entityRow) {
        this.entityRow = entityRow;
        return this;
    }
}
