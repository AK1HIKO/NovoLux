package com.akihiko.novolux.engine.core.ecs;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class EntityArchetypeRecord {

    private int groupIndex;

    public EntityArchetypeRecord(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getGroupIndex() {
        return groupIndex;
    }
}
