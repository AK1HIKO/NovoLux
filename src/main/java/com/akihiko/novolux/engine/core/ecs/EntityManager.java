package com.akihiko.novolux.engine.core.ecs;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Main manager of the Entity-Component-System.
 * Stores the entities, handles proper caching and retrieval.
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class EntityManager {

    private static final AtomicLong entityIdCounter = new AtomicLong();
    private static final AtomicLong entityArchetypeIdCounter = new AtomicLong();

    // No access modifier, so that they can be used in the same package.
    static long newEntityId(){
        return entityIdCounter.getAndIncrement();
    }
    static long newEntityArchetypeId(){
        return entityArchetypeIdCounter.getAndIncrement();
    }

    // Map<EntityId, EntityRecord>
    // Stores, what components does an entity have. EntityRecord contains the Archetype of ALL the components the entity has, and at which position in the EntityArchetype, we have this Entity's components set.
    Map<Long, EntityRecord> entities = new HashMap<>();

    // Map<ComponentId, Map<EntityArchetypeId, EntityArchetypeRecord>>
    // In what Archetypes, does the ComponentId exist.
    // Used to speed up "entity.has" operation, providing constant time.
    Map<Integer, Map<Long, EntityArchetypeRecord>> components = new HashMap<>();

    // Map<ComponentTypes, EntityArchetype>
    // Used to find all the entities that have a particular archetype.
    Map<List<Integer>, EntityArchetype> archetypes = new HashMap<>();

    private boolean hasComponent(long entityId, int componentId){
        EntityRecord record = entities.get(entityId);
        EntityArchetype archetype = record.getArchetype();

        Map<Long, EntityArchetypeRecord> archetypeRecordsMap = components.get(componentId);

        return archetypeRecordsMap.containsKey(archetype.getId());
    }

    private <T extends Component> T getComponent(long entityId, int componentId){
        EntityRecord record = entities.get(entityId);
        EntityArchetype archetype = record.getArchetype();

        Map<Long, EntityArchetypeRecord> archetypeRecordsMap = components.get(componentId);
        if(archetypeRecordsMap.containsKey(archetype.getId()))
            return null;

        EntityArchetypeRecord archetypeRecord = archetypeRecordsMap.get(archetype.getId());
        // Get a Column, that contains all the components of type T, and then get the component on an "entityRow", to
        // retrieve the provided entity's component.
        return archetype.<T>getComponentGroup(archetypeRecord.getGroupIndex()).getComponent(record.getEntityRow());
    }

    private boolean addComponent(long entityId, Component newComponent){
        if(newComponent == null)
            return false;

        EntityRecord record = entities.get(entityId);
        EntityArchetype archetype = record.getArchetype();

        if(components.get(newComponent.ID()).containsKey(archetype.getId()))  // If already contains a component with the same type:
            return false;

        EntityArchetype promotedArchetype = archetype.getArchetypeRelation(newComponent.ID()).getPromotedArchetype();
        if(promotedArchetype == null){  // If no cached promotedArchetype. Try to lazy-initialize our graph:
            // Look-up whether the required archetype already exists in our "archetypes" map.
            List<Integer> newArchetypeTypes = new ArrayList<>(archetype.getComponentTypes());
            newArchetypeTypes.add(newComponent.ID());
            // Sort in ascending order:
            newArchetypeTypes.sort(Comparator.naturalOrder());

            promotedArchetype = archetypes.get(newArchetypeTypes);
            if(promotedArchetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
                // Create archetype from scratch.
                promotedArchetype = new EntityArchetype(newArchetypeTypes);

                // Add it into the "archetypes" map for proper indexing.
                archetypes.put(newArchetypeTypes, promotedArchetype);
                // Also add it into the "components" map for proper "has".
                components.get(newComponent.ID()).put(promotedArchetype.getId(), new EntityArchetypeRecord(newArchetypeTypes.indexOf(newComponent.ID())));
            }
        }

        int newEntityRow = 0;
        for(EntityArchetype.ComponentGroup componentGroup : promotedArchetype.getComponentGroups()){
            // Add same components to the new archetype.
            int cgComponentId = componentGroup.getComponentId();
            componentGroup.getComponents().add(cgComponentId == newComponent.ID() ? newComponent : getComponent(entityId, cgComponentId));

            // And assign the newEntityRow:
            newEntityRow = componentGroup.getComponents().size()-1;
        }
        record.setArchetype(promotedArchetype);
        record.setEntityRow(newEntityRow);

        return true;
    }

    private boolean removeComponent(long entityId, int componentId){
        EntityRecord record = entities.get(entityId);
        EntityArchetype archetype = record.getArchetype();

        if(components.get(componentId).containsKey(archetype.getId()))  // If does not contain the specified component:
            return false;

        EntityArchetype demotedArchetype = archetype.getArchetypeRelation(componentId).getDemotedArchetype();
        if(demotedArchetype == null){   // If no cached demotedArchetype. Try to lazy-initialize our graph:
            // Look-up whether the required archetype already exists in our "archetypes" map.
            List<Integer> newArchetypeTypes = new ArrayList<>(archetype.getComponentTypes());
            newArchetypeTypes.remove(componentId);
            // During removal, the order is not changed, meaning that we do not need to perform additional sort

            demotedArchetype = archetypes.get(newArchetypeTypes);
            if(demotedArchetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
                // Create archetype from scratch.
                demotedArchetype = new EntityArchetype(newArchetypeTypes);

                // Add it into the "archetypes" map for proper indexing.
                archetypes.put(newArchetypeTypes, demotedArchetype);
                // Also add it into the "components" map for proper "has".
            }
        }

        int newEntityRow = 0;
        for(EntityArchetype.ComponentGroup componentGroup : demotedArchetype.getComponentGroups()){
            // Add same components to the new archetype.
            componentGroup.getComponents().add(getComponent(entityId, componentGroup.getComponentId()));

            // And assign the newEntityRow:
            newEntityRow = componentGroup.getComponents().size()-1;
        }

        record.setArchetype(demotedArchetype);
        record.setEntityRow(newEntityRow);

        return true;
    }

}
