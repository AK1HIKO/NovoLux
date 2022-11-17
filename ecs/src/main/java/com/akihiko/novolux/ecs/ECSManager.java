package com.akihiko.novolux.ecs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Main manager of the Entity-Component-System.
 * Stores the entities, handles proper caching and retrieval.
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class ECSManager {

    private static final AtomicLong entityIdCounter = new AtomicLong();
    private static final AtomicLong entityArchetypeIdCounter = new AtomicLong();
    private static final AtomicInteger componentIdCounter = new AtomicInteger();

    // No access modifier, so that they can be used in the same package.
    static long generateEntityId(){
        return entityIdCounter.getAndIncrement();
    }
    static long generateEntityArchetypeId(){
        return entityArchetypeIdCounter.getAndIncrement();
    }
    static int generateComponentId(){
        return componentIdCounter.getAndIncrement();
    }

    /**
     * Map&lt;EntityId, Entity&gt;
     */
    Map<Long, Entity> entities = new HashMap<>();

    /**
     * Map&lt;EntityArchetypeId, EntityArchetype&gt;
     */
    Map<Long, EntityArchetype> archetypes = new HashMap<>();


    List<ComponentSystem> componentSystems = new ArrayList<>();

    /**
     * Map&lt;ComponentId, Set&lt;EntityArchetypeId&gt;&gt;<br/>
     * Indicates in what Archetypes, does the ComponentId exist. Used to speed up "entity.has" operation, providing constant time.
     */
    Map<Integer, Set<Long>> cidToArchetypeIds = new HashMap<>();

    /**
     * Map&lt;ComponentTypes, EntityArchetypeId&rt;
     * Used to find all the entities that have a particular archetype.
     */
    Map<List<Integer>, Long> ctypesToArchetypeId = new HashMap<>();


    public List<EntityQueryResult> queryEntities(EntitiesQuery query){
        List<Integer> requiredComponents = query.requiredComponents();
        EntityArchetype archetype = archetypes.get(ctypesToArchetypeId.get(requiredComponents));
        if(archetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
            // Create archetype from scratch.
            archetype = new EntityArchetype(requiredComponents);

            // Add it into the "archetypes" maps for proper indexing.
            archetypes.put(archetype.getId(), archetype);
            ctypesToArchetypeId.put(requiredComponents, archetype.getId());

            Set<Long> archetypeIntersectionSet = new HashSet<>(cidToArchetypeIds.get(requiredComponents.get(0)));
            // Now index every component in the archetype and put them into the "components" map for proper "has".
            // Also, populate "componentGroups", for the further entity population.
            for (Integer requiredComponentId : requiredComponents) {
                Set<Long> archetypeSet = cidToArchetypeIds.get(requiredComponentId);
                archetypeSet.add(archetype.getId());
                archetypeIntersectionSet.retainAll(archetypeSet);
            }


            // If the result of intersection is 0, then
            // that it means that there are no fitting entities, hence, exit the function.
            if(archetypeIntersectionSet.size() == 0) {
                // But do not forget to include already prepared archetype in the map,
                // so that the next time a component is added, we already have a fitting archetype
                // in the map, further optimizing the search times:
                for(Integer requiredComponentId : requiredComponents){
                    // Since we did not find a fitting archetype, it means that there are no entities, that will
                    // fit in the component group, hence simply create an empty component group:
                    archetype.getComponentGroups().put(requiredComponentId, new ComponentGroup(requiredComponentId));
                }
                // Finally, add our archetype to the storage:
                addNewArchetype(archetype);
                return new ArrayList<>();
            }

            // Find the Archetype with the smallest amount of component types:
            EntityArchetype minimalArchetype = null;
            for(Long aid : archetypeIntersectionSet){
                if(minimalArchetype == null) {
                    minimalArchetype = archetypes.get(aid);
                }else if(minimalArchetype.getComponentTypes().size() > archetypes.get(aid).getComponentTypes().size()){
                    minimalArchetype = archetypes.get(aid);
                }
            }

            for(Integer requiredComponentId : requiredComponents){
                archetype.getComponentGroups().put(requiredComponentId, new ComponentGroup(minimalArchetype.getComponentGroup(requiredComponentId)));
            }

            // Finally, add our archetype to the storage:
            addNewArchetype(archetype);
        }

        // Form the result:
        List<EntityQueryResult> result = new ArrayList<>();
        boolean isInitialized = false;
        for(ComponentGroup componentGroup : archetype.getComponentGroups().values()){
            for(int i = 0; i < componentGroup.getComponents().size(); i++){
                if(!isInitialized){
                    result.add(new EntityQueryResult(new HashMap<>()));
                }
                result.get(i).components().put(componentGroup.getComponentId(), componentGroup.getComponents().get(i));
            }
            isInitialized = true;
        }

        return result;
    }

    private boolean hasComponent(long entityId, int componentId){
        Entity entity = entities.get(entityId);
        return hasComponent(entity, componentId);
    }

    private boolean hasComponent(Entity entity, int componentId){
        return entity.getEntityInfo().getArchetype().getComponentTypes().contains(componentId);
    }

    private <T extends Component> T getComponent(long entityId, int componentId){
        Entity entity = entities.get(entityId);
        EntityArchetype archetype = entity.getEntityInfo().getArchetype();

        ComponentGroup componentGroup = archetype.<T>getComponentGroup(componentId);
        if(componentGroup == null)
            return null;

        // Get a Column, that contains all the components of type T, and then get the component on an "entityRow", to
        // retrieve the provided entity's component.
        return componentGroup.getComponent(entity.getEntityInfo().getEntityRow());
    }

    private boolean addComponent(long entityId, Component newComponent){
        if(newComponent == null)
            return false;

        Entity entity = entities.get(entityId);
        EntityArchetype archetype = entity.getEntityInfo().getArchetype();

        if(hasComponent(entity, newComponent.ID()))  // If already contains a component with the same type:
            return false;   //TODO: Throw an exception.

        EntityArchetype promotedArchetype = archetype.getArchetypeRelation(newComponent.ID()).getPromotedArchetype();
        if(promotedArchetype == null){  // If no cached promotedArchetype. Try to lazy-initialize our graph:
            // Look-up whether the required archetype already exists in our "archetypes" map.
            List<Integer> newArchetypeTypes = new ArrayList<>(archetype.getComponentTypes());
            newArchetypeTypes.add(newComponent.ID());
            // Sort in ascending order:
            newArchetypeTypes.sort(Comparator.naturalOrder());

            promotedArchetype = archetypes.get(ctypesToArchetypeId.get(newArchetypeTypes));
            if(promotedArchetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
                // Create archetype from scratch.
                promotedArchetype = new EntityArchetype(newArchetypeTypes);
                addNewArchetype(promotedArchetype);
            }
        }

        int newEntityRow = 0;
        for(Map.Entry<Integer, ComponentGroup> entry : promotedArchetype.getComponentGroups().entrySet()){
            // Add same components to the new archetype.
            entry.getValue().getComponents().add(entry.getKey() == newComponent.ID() ? newComponent : getComponent(entityId, entry.getKey()));

            // And assign the newEntityRow:
            newEntityRow = entry.getValue().getComponents().size()-1;
        }
        entity.getEntityInfo().setArchetype(promotedArchetype);
        entity.getEntityInfo().setEntityRow(newEntityRow);

        return true;
    }

    private boolean removeComponent(long entityId, int componentId){
        Entity entity = entities.get(entityId);
        EntityArchetype archetype = entity.getEntityInfo().getArchetype();

        if(!hasComponent(entity, componentId))  // If does not contain the specified component:
            return false;   //TODO: Throw an exception.

        EntityArchetype demotedArchetype = archetype.getArchetypeRelation(componentId).getDemotedArchetype();
        if(demotedArchetype == null){   // If no cached demotedArchetype. Try to lazy-initialize our graph:
            // Look-up whether the required archetype already exists in our "archetypes" map.
            List<Integer> newArchetypeTypes = new ArrayList<>(archetype.getComponentTypes());
            newArchetypeTypes.remove(componentId);
            // During removal, the order is not changed, meaning that we do not need to perform additional sort

            demotedArchetype = archetypes.get(ctypesToArchetypeId.get(newArchetypeTypes));
            if(demotedArchetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
                // Create archetype from scratch.
                demotedArchetype = new EntityArchetype(newArchetypeTypes);
                addNewArchetype(demotedArchetype);
            }
        }

        // TODO: If previous archetype became empty, clean it up after some time.

        int newEntityRow = 0;
        for(Map.Entry<Integer, ComponentGroup> entry : demotedArchetype.getComponentGroups().entrySet()){
            // Add same components to the new archetype.
            entry.getValue().getComponents().add(getComponent(entityId, entry.getKey()));

            // And assign the newEntityRow:
            newEntityRow = entry.getValue().getComponents().size()-1;
        }

        entity.getEntityInfo().setArchetype(demotedArchetype);
        entity.getEntityInfo().setEntityRow(newEntityRow);

        return true;
    }

    /**
     * Properly adds {@code newArchetype} in the corresponding maps, ensuring the correct indexing.
     * @param newArchetype newly created archetype.
     */
    private void addNewArchetype(EntityArchetype newArchetype){
        // Add it into the "archetypes" maps for proper indexing.
        archetypes.put(newArchetype.getId(), newArchetype);
        ctypesToArchetypeId.put(newArchetype.getComponentTypes(), newArchetype.getId());

        // Also add it into the "components" map for proper "has".
        for (int componentTypeId : newArchetype.getComponentTypes())
            cidToArchetypeIds.get(componentTypeId).add(newArchetype.getId());
    }

}
