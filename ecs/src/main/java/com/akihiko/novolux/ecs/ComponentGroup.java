package com.akihiko.novolux.ecs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 16/11/22
 */
public class ComponentGroup {

    private final int componentId;
    private final List<Component> components = new ArrayList<>();

    public ComponentGroup(int componentId) {
        this.componentId = componentId;
    }

    public ComponentGroup(ComponentGroup copy){
        this(copy.componentId);
        this.components.addAll(copy.components);
    }

    public <T extends Component> T getComponent(int index){
        return (T) this.components.get(index);
    }

    public int getComponentId() {
        return componentId;
    }

    public <T extends Component> List<T> getComponents() {
        return (List<T>) components;
    }

    public ComponentGroup addComponent(Component component){
        this.components.add(component);
        return this;
    }
}
