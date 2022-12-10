package com.akihiko.novolux.engine.core.scene;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.ECSManager;
import com.akihiko.novolux.engine.Application;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public abstract class Scene {

    protected final ECSManager ECSManager = new ECSManager();


    public abstract void create();

    private float frames = 0;
    private long previousTime = 0L;
    public final void update(float deltaTime){
//        float deltaTime = getDeltaTime();
//        previousTime = System.nanoTime();

//        long currentTime = System.currentTimeMillis();
//        float elapsed = (currentTime - previousTime);
//        if(elapsed > 1000){
//            previousTime = currentTime;
//            System.out.println(frames);
//            frames = 0;
//        }

        this.ECSManager.lifecycleStart();

        for(ComponentSystem system : this.ECSManager.getGeneralComponentSystems()){
            system.onUpdate(this.ECSManager.queryEntities(system.getDependencies()), deltaTime);
        }

//        Mesh cube = new Mesh(new Vector4[]{
//                new Vector4(0, 0, 0),
//                new Vector4(1, 0, 0),
//                new Vector4(1, 1, 0),
//                new Vector4(0, 1, 0),
//                new Vector4(0, 1, 1),
//                new Vector4(1, 1, 1),
//                new Vector4(1, 0, 1),
//                new Vector4(0, 0, 1),
//        }, new Face[]{
//                new Face(0, 2, 1), //face front
//                new Face(0, 3, 2),
//                new Face(2, 3, 4), //face top
//                new Face(2, 4, 5),
//                new Face(1, 2, 5), //face right
//                new Face(1, 5, 6),
//                new Face(0, 7, 4), //face left
//                new Face(0, 4, 3),
//                new Face(5, 4, 7), //face back
//                new Face(5, 7, 6),
//                new Face(0, 6, 7), //face bottom
//                new Face(0, 1, 6)
//        });
        Application.getInstance().getGameView().render();
        this.ECSManager.lifecycleEnd();
        frames++;
//        for(ComponentSystem system : this.ECSManager.getComponentSystems()){
//            system.onUpdate(this.ECSManager.queryEntities(system.getDependencies()), deltaTime);
//        }
    }

}
