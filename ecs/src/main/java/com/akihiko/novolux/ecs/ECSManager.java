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
    private static final AtomicInteger componentSystemIdCounter = new AtomicInteger();

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
    static int generateComponentSystemId(){
        return componentSystemIdCounter.getAndIncrement();
    }

    /**
     * Map&lt;EntityId, Entity&gt;
     */
    private final Map<Long, Entity> entities = new HashMap<>();

    /**
     * Map&lt;EntityArchetypeId, EntityArchetype&gt;
     */
    private final Map<Long, EntityArchetype> archetypes = new HashMap<>();

    //region ECS Life-cycle Implementation:
    /**
     * Must only be used for general {@link ComponentSystem}. That is Systems that follow the automatic {@link ECSManager} lifecycle.
     * If you wish to manually control a lifecycle of a particular {@link ComponentSystem} (e.g. Physics always executed at constant intervals,
     * or Renderer always executed last) do not add it into this {@link HashSet}.
     */
    private final Set<ComponentSystem> generalComponentSystems = new HashSet<>();

    public boolean addGeneralComponentSystem(ComponentSystem newComponentSystem){
        return generalComponentSystems.add(newComponentSystem);
    }

    public boolean removeGeneralComponentSystem(){
        // TODO: Not Implemented yet
        throw new UnsupportedOperationException();
//        return false;
    }

    public Set<ComponentSystem> getGeneralComponentSystems() {
        // Prevents user from directly modifying the componentSystems set.
        return Collections.unmodifiableSet(generalComponentSystems);
    }


    //endregion

    /**
     * Queue&lt;Pair&lt;EntityId, Component&gt;&gt;
     */
    private final Queue<AbstractMap.SimpleImmutableEntry<Long, Component>> emplacedComponentsQueue = new LinkedList<>();

    /**
     * Queue&lt;Pair&lt;EntityId, ComponentId&gt;&gt;
     */
    private final Queue<AbstractMap.SimpleImmutableEntry<Long, Integer>> erasedComponentsQueue = new LinkedList<>();


    /* TODO: Optimize by storing both emplaced and erased components in one map, this will allow us to optimize
     *  situations, where we add and delete a component in one lifecycle. (Profile later) */
    public void emplaceComponent(long entityId, Component newComponent){
        if(newComponent == null)
            throw new ECSRuntimeException("Trying to emplace a null component!");

        emplacedComponentsQueue.offer(new AbstractMap.SimpleImmutableEntry<>(entityId, newComponent));
    }

    public void eraseComponent(long entityId, int componentId){
        /* TODO: perform additional checks, (hasComponent), to simply not add unnecessary "erased" components
         *  to the queue. May not be optimized due to multiple retrievals and method call. (Profile later) */
        erasedComponentsQueue.offer(new AbstractMap.SimpleImmutableEntry<>(entityId, componentId));
    }

    /**
     * Queue&lt;Entity&gt;
     */
    private final Queue<Entity> newEntitiesQueue = new LinkedList<>();

    /**
     * Queue&lt;Entity&gt;
     */
    private final Queue<Entity> deletedEntitiesQueue = new LinkedList<>();
    public Entity createEntity(){
        Entity entity = new Entity();
        newEntitiesQueue.offer(entity);
        return entity;
    }

    public void deleteEntity(Entity entity){
        if(entity == null)
            throw new ECSRuntimeException("Trying to delete a null entity!");

        EntityInfo entityInfo = entity.getEntityInfo();
        if(entityInfo != null){
            for(int componentId : entityInfo.getArchetype().getComponentTypes()){
                this.eraseComponent(entity.getId(), componentId);
            }
        }
        deletedEntitiesQueue.offer(entity);
    }

    public Entity getEntity(long entityId){
        Entity result = entities.get(entityId);
        if(result == null){
            throw new ECSRuntimeException("Entity does not exist!");
        }
        return result;
    }

    public void lifecycleStart(){
        while(!newEntitiesQueue.isEmpty()){
            Entity newEntity = newEntitiesQueue.remove();
            entities.put(newEntity.getId(), newEntity);
        }

        while(!emplacedComponentsQueue.isEmpty()){
            AbstractMap.SimpleImmutableEntry<Long, Component> emplacedComponent = emplacedComponentsQueue.remove();
            this.addComponent(emplacedComponent.getKey(), emplacedComponent.getValue());
        }
    }

    public void lifecycleEnd(){
        while(!erasedComponentsQueue.isEmpty()){
            AbstractMap.SimpleImmutableEntry<Long, Integer> erasedComponent = erasedComponentsQueue.remove();
            this.removeComponent(erasedComponent.getKey(), erasedComponent.getValue());
        }

        while(!deletedEntitiesQueue.isEmpty()){
            Entity deletedEntity = deletedEntitiesQueue.remove();
            entities.remove(deletedEntity.getId(), deletedEntity);
        }
    }


    //region Storage Implementation
    /**
     * Map&lt;ComponentId, Set&lt;EntityArchetypeId&gt;&gt;<br/>
     * Indicates in what Archetypes, does the ComponentId exist. Used to speed up "entity.has" operation, providing constant time.
     */
    // TODO: Perform additional tests, whether it is working properly.
    private final Map<Integer, Set<Long>> cidToArchetypeIds = new HashMap<>();

    /**
     * Map&lt;ComponentTypes(Immutable), EntityArchetypeId&gt;
     * Used to find all the entities that have a particular archetype.
     */
    private final Map<List<Integer>, Long> ctypesToArchetypeId = new HashMap<>();

    public List<EntityQueryResult> queryEntities(EntitiesQuery query){
        List<Integer> requiredComponents = query.requiredComponents();
        if(requiredComponents.isEmpty())
            return new ArrayList<>();

        EntityArchetype archetype = archetypes.get(ctypesToArchetypeId.get(requiredComponents));
        if(archetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
            // Create archetype from scratch.
            archetype = new EntityArchetype(requiredComponents);

            // Add it into the "archetypes" maps for proper indexing.
            archetypes.put(archetype.getId(), archetype);
            ctypesToArchetypeId.put(Collections.unmodifiableList(requiredComponents), archetype.getId());

            boolean isFirst = true;
            Set<Long> archetypeIntersectionSet = new HashSet<>();
            // Now index every component in the archetype and put them into the "components" map for proper "has".
            // Also, populate "componentGroups", for the further entity population.
            for (int requiredComponentId : requiredComponents) {
                Set<Long> archetypeSet = cidToArchetypeIds.get(requiredComponentId);

                // If no such archetype, then there will be no intersection, so we can
                // simply prematurely exit the loop:
                if(archetypeSet == null) {
                    archetypeIntersectionSet = new HashSet<>();
                    break;
                }

                archetypeSet.add(archetype.getId());

                if(isFirst){
                    archetypeIntersectionSet.addAll(archetypeSet);
                    isFirst = false;
                    continue;
                }
                archetypeIntersectionSet.retainAll(archetypeSet);
            }


            // If the result of intersection is 0, then
            // that it means that there are no fitting entities, hence, exit the function.
            if(archetypeIntersectionSet.size() == 0) {
                // But do not forget to include an already prepared archetype in the map,
                // so that the next time a component is added, we already have a fitting archetype
                // in the map, further optimizing the search times:
                for(int requiredComponentId : requiredComponents){
                    // Since we did not find a fitting archetype, it means that there are no entities, that will
                    // fit in the component group, hence simply create an empty component group:
                    archetype.getComponentGroups().put(requiredComponentId, new ComponentGroup(requiredComponentId));
                }
                // Finally, add our archetype to the storage:
                this.addNewArchetype(archetype);
                return new ArrayList<>();
            }

            // Find the Archetype with the smallest amount of component types:
            EntityArchetype minimalArchetype = null;
            for(long aid : archetypeIntersectionSet){
                if(minimalArchetype == null) {
                    minimalArchetype = archetypes.get(aid);
                }else if(minimalArchetype.getComponentTypes().size() > archetypes.get(aid).getComponentTypes().size()){
                    minimalArchetype = archetypes.get(aid);
                }
            }

            for(int requiredComponentId : requiredComponents){
                archetype.getComponentGroups().put(requiredComponentId, new ComponentGroup(minimalArchetype.getComponentGroup(requiredComponentId)));
            }

            // Finally, add our archetype to the storage:
            this.addNewArchetype(archetype);
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
        Entity entity = this.getEntity(entityId);
        return this.hasComponent(entity, componentId);
    }

    private boolean hasComponent(Entity entity, int componentId){
        EntityInfo entityInfo = entity.getEntityInfo();
        if(entityInfo == null)
            return false;
        return entityInfo.getArchetype().getComponentTypes().contains(componentId);
    }

    private <T extends Component> T getComponent(long entityId, int componentId){
        Entity entity = this.getEntity(entityId);
        EntityInfo entityInfo = entity.getEntityInfo();
        if(entityInfo == null)
            return null;
        EntityArchetype archetype = entityInfo.getArchetype();

        ComponentGroup componentGroup = archetype.<T>getComponentGroup(componentId);
        if(componentGroup == null)
            return null;

        // Get a Column, that contains all the components of type T, and then get the component on an "entityRow", to
        // retrieve the provided entity's component.
        return componentGroup.getComponent(entityInfo.getEntityRow());
    }


    private void initializeEntityInfo(Entity entity, Component initialComponent){
        EntityArchetype newArchetype = new EntityArchetype(initialComponent.ID(), initialComponent);
        this.addNewArchetype(newArchetype);
        entity.getEntityInfo().setArchetype(newArchetype);
        entity.getEntityInfo().setEntityRow(0);
    }
    private boolean addComponent(long entityId, Component newComponent){
        Entity entity = this.getEntity(entityId);
        EntityInfo entityInfo = entity.getEntityInfo();
        if(entityInfo == null){
            entityInfo = new EntityInfo();
            entity.setEntityInfo(entityInfo);
            Set<Long> archetypeIds = cidToArchetypeIds.get(newComponent.ID());
            if(archetypeIds == null){
                this.initializeEntityInfo(entity, newComponent);
            }else {
                List<EntityArchetype> fitting = archetypeIds.stream().filter(archetypeId -> {
                    EntityArchetype archetype = archetypes.get(archetypeId);
                    if (archetype == null)
                        return false;
                    return archetype.getComponentTypes().size() == 1 && archetype.getComponentTypes().contains(newComponent.ID());
                }).map(archetypes::get).toList();

                if (fitting.size() > 1)
                    throw new ECSRuntimeException("Something went wrong! Multiple archetypes with the same components list!");
                // If no already existing archetype with the given parameters, create archetype from the ground up:
                if (fitting.size() == 0) {
                    this.initializeEntityInfo(entity, newComponent);
                } else {
                    EntityArchetype archetype = fitting.get(0);
                    archetype.getComponentGroup(newComponent.ID()).getComponents().add(newComponent);
                    entity.getEntityInfo().setEntityRow(archetype.getComponentGroup(newComponent.ID()).getComponents().size()-1);
                    entity.getEntityInfo().setArchetype(archetype);
                }
            }
            newComponent.bind(entityId, this);
            return true;
        }
        EntityArchetype archetype = entityInfo.getArchetype();

        if(this.hasComponent(entity, newComponent.ID()))  // If already contains a component with the same type:
            throw new ECSRuntimeException("Trying to add multiple components of the same type!");

        newComponent.bind(entityId, this);

        EntityArchetype.EntityArchetypeRelation relation = archetype.getArchetypeRelation(newComponent.ID());
        EntityArchetype promotedArchetype = null;
        if(relation == null) {
            archetype.getRelatedArchetypes().put(newComponent.ID(), new EntityArchetype.EntityArchetypeRelation());
        }else{
            promotedArchetype = relation.getPromotedArchetype();
        }
        if (promotedArchetype == null) {  // If no cached promotedArchetype. Try to lazy-initialize our graph:
            // Look-up whether the required archetype already exists in our "archetypes" map.
            List<Integer> newArchetypeTypes = new ArrayList<>(archetype.getComponentTypes());
            newArchetypeTypes.add(newComponent.ID());
            // Sort in ascending order:
            newArchetypeTypes.sort(Comparator.naturalOrder());

            promotedArchetype = archetypes.get(ctypesToArchetypeId.get(newArchetypeTypes));
            if (promotedArchetype == null) {  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
                // Create archetype from scratch.
                promotedArchetype = new EntityArchetype(newArchetypeTypes);
                this.addNewArchetype(promotedArchetype);
            }
        }
        int newEntityRow = 0;
        for(Map.Entry<Integer, ComponentGroup> entry : promotedArchetype.getComponentGroups().entrySet()){
            // Add same components to the new archetype.
            entry.getValue().getComponents().add(entry.getKey() == newComponent.ID() ? newComponent : getComponent(entityId, entry.getKey()));

            // And assign the newEntityRow:
            newEntityRow = entry.getValue().getComponents().size()-1;
        }
        entityInfo.setArchetype(promotedArchetype);
        entityInfo.setEntityRow(newEntityRow);

        return true;
    }

    private boolean removeComponent(long entityId, int componentId){
        Entity entity = this.getEntity(entityId);

        EntityInfo entityInfo = entity.getEntityInfo();
        if(entityInfo == null)
            return false;
        EntityArchetype archetype = entityInfo.getArchetype();

        if(!this.hasComponent(entity, componentId))  // If does not contain the specified component:
            throw new ECSRuntimeException("Trying to remove a component that does not exist!");

        EntityArchetype.EntityArchetypeRelation relation = archetype.getArchetypeRelation(componentId);
        EntityArchetype demotedArchetype = null;
        if(relation == null) {
            archetype.getRelatedArchetypes().put(componentId, new EntityArchetype.EntityArchetypeRelation());
        }else{
            demotedArchetype = relation.getDemotedArchetype();
        }

        if(demotedArchetype == null){   // If no cached demotedArchetype. Try to lazy-initialize our graph:
            // Look-up whether the required archetype already exists in our "archetypes" map.
            List<Integer> newArchetypeTypes = new ArrayList<>(archetype.getComponentTypes());
            newArchetypeTypes.remove(componentId);
            // During removal, the order is not changed, meaning that we do not need to perform additional sort

            demotedArchetype = archetypes.get(ctypesToArchetypeId.get(newArchetypeTypes));
            if(demotedArchetype == null){  // If no existing archetype in the "archetypes" map with the same List of ComponentTypes.
                // Create archetype from scratch.
                demotedArchetype = new EntityArchetype(newArchetypeTypes);
                this.addNewArchetype(demotedArchetype);
            }
        }

        // TODO: If previous archetype became empty, clean it up after some time.

        int newEntityRow = 0;
        for(Map.Entry<Integer, ComponentGroup> entry : demotedArchetype.getComponentGroups().entrySet()){
            // Add same components to the new archetype.
            entry.getValue().getComponents().add(this.getComponent(entityId, entry.getKey()));

            // And assign the newEntityRow:
            newEntityRow = entry.getValue().getComponents().size()-1;
        }

        entityInfo.setArchetype(demotedArchetype);
        entityInfo.setEntityRow(newEntityRow);

        return true;
    }

    /**
     * Properly adds {@code newArchetype} in the corresponding maps, ensuring the correct indexing.
     * @param newArchetype newly created archetype.
     */
    private void addNewArchetype(EntityArchetype newArchetype){
        // Add it into the "archetypes" maps for proper indexing.
        archetypes.put(newArchetype.getId(), newArchetype);
        // getComponentTypes already returns an immutable list:
        ctypesToArchetypeId.put(newArchetype.getComponentTypes(), newArchetype.getId());

        // Also add it into the "components" map for proper "has".
        for (int componentTypeId : newArchetype.getComponentTypes()) {
            cidToArchetypeIds.putIfAbsent(componentTypeId, new HashSet<>());
            cidToArchetypeIds.get(componentTypeId).add(newArchetype.getId());

            newArchetype.getComponentGroups().putIfAbsent(componentTypeId, new ComponentGroup(componentTypeId));
        }
    }

    //endregion

}
