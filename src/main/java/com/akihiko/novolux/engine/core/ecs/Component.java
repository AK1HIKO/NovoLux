package com.akihiko.novolux.engine.core.ecs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * <p>Note: A single object cannot have multiple components of the same type!</p>
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class Component {

    private static final AtomicInteger componentIdCounter = new AtomicInteger();
    protected static int newComponentId(){
        return componentIdCounter.getAndIncrement();
    }

    public abstract int ID();

}
