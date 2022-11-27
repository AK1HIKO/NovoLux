package com.akihiko.novolux.ecs;


import java.util.*;

/**
 * EntityArchetype is a unique combination of component types. The {@link ECSManager} uses the archetype to group all entities that have the same sets of components.
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class EntityArchetype {

    private final long id = ECSManager.generateEntityArchetypeId();
    public long getId() {
        return id;
    }

    private List<Integer> componentTypes = new ArrayList<>();

    // Map<ComponentId, ComponentGroup>.
    private final Map<Integer, ComponentGroup> componentGroups = new HashMap<>();
    public <T extends Component> ComponentGroup getComponentGroup(int key){
        return this.componentGroups.get(key);
    }

    // Map<ComponentId, EntityArchetype>
    // This is used for optimization, when we add/remove components. It effectively
    // represents a graph of EntityArchetype relations.
    private final Map<Integer, EntityArchetypeRelation> relatedArchetypes = new HashMap<>();
    public EntityArchetypeRelation getArchetypeRelation(int modifiedComponentId) {
        return this.relatedArchetypes.get(modifiedComponentId);
    }

    public EntityArchetype(Integer componentId, Component component) {
        componentTypes.add(componentId);
        componentGroups.put(componentId, new ComponentGroup(componentId).addComponent(component));
    }

    public EntityArchetype(List<Integer> componentTypes){
        this.componentTypes = componentTypes;
    }

    public static class EntityArchetypeRelation{
        private EntityArchetype promotedArchetype = null;
        private EntityArchetype demotedArchetype = null;

        public EntityArchetypeRelation() {
        }

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


    /**
     * Since our {@link EntityArchetype#componentTypes} array is sorted, this algorithm is a quick and performant version of containsAll.
     * @param componentTypes
     * @return
     */
    public boolean containsComponentTypes(List<Integer> componentTypes){
        int offset = 0;
        int matches = 0;
        for (int i = 0; i < componentTypes.size(); i++){
            int b = componentTypes.get(i);
            for(; offset < this.componentTypes.size(); offset++){
                int a = this.componentTypes.get(offset);
                if(a > b)
                    // Since it is sorted, if our componentId > currentComponentId and we still have not found
                    // the exact match, it means that such component does not exist, hence return false.
                    return false;

                if(a == b) {
                    matches++;
                    break;
                }
            }
        }
        return matches == componentTypes.size();
    }

    public List<Integer> getComponentTypes() {
        return Collections.unmodifiableList(componentTypes);
    }

    public EntityArchetype setComponentTypes(List<Integer> componentTypes) {
        this.componentTypes = componentTypes;
        return this;
    }

    // Without access modifier to isolate them to one package
    Map<Integer, ComponentGroup> getComponentGroups() {
        return componentGroups;
    }

    Map<Integer, EntityArchetypeRelation> getRelatedArchetypes() {
        return relatedArchetypes;
    }


    // TODO: Improve:
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EntityArchetype other))
            return false;

        return this.id == other.id;
    }
}

