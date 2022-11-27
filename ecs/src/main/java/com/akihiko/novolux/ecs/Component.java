package com.akihiko.novolux.ecs;

/**
 *
 * <p>Note: A single object cannot have multiple components of the same type!</p>
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */

// TODO: Create wrapper around ComponentData that will handle all the ids, isInitialized states and contain ecs manager ??? or scene.
public abstract class Component {

    protected static int generateComponentId(){
        return ECSManager.generateComponentId();
    }

    public abstract int ID();

    private long holderEntityId = -1L;
    public long getHolderEntityId() {
        return holderEntityId;
    }

    private ECSManager ecsManager = null;

    final void bind(long holderEntityId, ECSManager ecsManager){
        this.holderEntityId = holderEntityId;
        this.ecsManager = ecsManager;
    }
    protected final void emplaceComponent(Component newComponent){
        this.ecsManager.emplaceComponent(this.holderEntityId, newComponent);
    }

    protected final void eraseComponent(Integer componentId){
        this.ecsManager.eraseComponent(this.holderEntityId, componentId);
    }

}
