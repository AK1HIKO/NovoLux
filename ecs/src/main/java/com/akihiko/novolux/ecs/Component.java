package com.akihiko.novolux.ecs;

/**
 *
 * <p>Note: A single object cannot have multiple components of the same type!</p>
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class Component {

    protected static int generateComponentId(){
        return ECSManager.generateComponentId();
    }

    public abstract int ID();

}
