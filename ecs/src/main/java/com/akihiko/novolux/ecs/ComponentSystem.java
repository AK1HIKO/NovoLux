package com.akihiko.novolux.ecs;

import java.util.List;
import java.util.Map;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class ComponentSystem {

    private final EntitiesQuery dependencies;
    public final EntitiesQuery getDependencies() {
        return dependencies;
    }

    // Must create dependencies for the component system.
    protected ComponentSystem(EntitiesQuery dependencies) {
        this.dependencies = dependencies;
    }

    public abstract void onStart(final List<EntityQueryResult> eComponents);

    public abstract void onUpdate(final List<EntityQueryResult> eComponents, float deltaTime);

    public abstract void onDestroy(final List<EntityQueryResult> eComponents);

}
