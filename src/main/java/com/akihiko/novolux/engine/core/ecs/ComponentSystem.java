package com.akihiko.novolux.engine.core.ecs;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class ComponentSystem {

    public abstract void onStart();

    public abstract void onUpdate();

    public abstract void onLateUpdate();

    public abstract void onDestroy();

}
