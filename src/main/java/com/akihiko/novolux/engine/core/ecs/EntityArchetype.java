package com.akihiko.novolux.engine.core.ecs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class EntityArchetype {

    private final long id = EntityManager.newEntityArchetypeId();
    public long getId() {
        return id;
    }

    private List<Integer> componentTypes;

    private List<ComponentGroup> componentGroups = new ArrayList<>();
    public <T extends Component> ComponentGroup<T> getComponentGroup(int index){
        return this.componentGroups.get(index);
    }

    // Map<ComponentId, EntityArchetype>
    // This is used for optimization, when we add/remove components. It effectively
    // represents a graph of EntityArchetype relations.
    private Map<Integer, EntityArchetypeRelation> relatedArchetypes = new HashMap<>();
    public EntityArchetypeRelation getArchetypeRelation(int modifiedComponentId) {
        return this.relatedArchetypes.get(modifiedComponentId);
    }

    public EntityArchetype(List<Integer> componentTypes) {
        this.componentTypes = componentTypes;
    }

    class ComponentGroup<T extends Component> {

        private final int componentId;
        private List<T> components = new ArrayList<>();

        public ComponentGroup(int componentId) {
            this.componentId = componentId;
        }

        public T getComponent(int index){
            return this.components.get(index);
        }

        public int getComponentId() {
            return componentId;
        }

        public List<T> getComponents() {
            return components;
        }
    }

    class EntityArchetypeRelation{
        private EntityArchetype promotedArchetype;
        private EntityArchetype demotedArchetype;

        public EntityArchetypeRelation(EntityArchetype promotedArchetype, EntityArchetype demotedArchetype) {
            this.promotedArchetype = promotedArchetype;
            this.demotedArchetype = demotedArchetype;
        }

        public EntityArchetype getPromotedArchetype() {
            return this.promotedArchetype;
        }

        public EntityArchetype getDemotedArchetype() {
            return this.demotedArchetype;
        }
    }


    public List<Integer> getComponentTypes() {
        return componentTypes;
    }

    public EntityArchetype setComponentTypes(List<Integer> componentTypes) {
        this.componentTypes = componentTypes;
        return this;
    }

    public List<ComponentGroup> getComponentGroups() {
        return componentGroups;
    }

    public EntityArchetype setComponentGroups(List<ComponentGroup> componentGroups) {
        this.componentGroups = componentGroups;
        return this;
    }

    public Map<Integer, EntityArchetypeRelation> getRelatedArchetypes() {
        return relatedArchetypes;
    }

    public EntityArchetype setRelatedArchetypes(Map<Integer, EntityArchetypeRelation> relatedArchetypes) {
        this.relatedArchetypes = relatedArchetypes;
        return this;
    }
}

