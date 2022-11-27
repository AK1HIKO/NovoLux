package com.akihiko.novolux.ecs;

import java.util.*;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class ComponentSystem {

    protected static int generateComponentSystemId(){
        return ECSManager.generateComponentSystemId();
    }

    public abstract int ID();

    private final EntitiesQuery dependencies;
    public final EntitiesQuery getDependencies() {
        return dependencies;
    }

    // Must create dependencies for the component system.
    protected ComponentSystem(EntitiesQuery dependencies) {
        this.dependencies = dependencies;
    }

    public void onUpdate(final List<EntityQueryResult> eComponents, float deltaTime){}

    public void onDestroy(final List<EntityQueryResult> eComponents){}


    // TODO: Improve
    @Override
    public final int hashCode() {
        return Long.hashCode(this.ID());
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ComponentSystem other))
            return false;

        return this.ID() == other.ID();
    }

}
