package com.akihiko.novolux.demo;

import com.akihiko.novolux.ecs.Entity;
import com.akihiko.novolux.engine.GameView;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponentSystem;
import com.akihiko.novolux.engine.core.components.render.MeshRendererComponent;
import com.akihiko.novolux.engine.core.components.render.RendererComponentSystem;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.utils.OBJModelLoader;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;
import com.akihiko.novolux.engine.core.rendering.Texture;
import com.akihiko.novolux.engine.core.scene.Scene;

import java.io.File;
import java.nio.file.Path;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class SandboxScene extends Scene {

    private final GameView gameView;

    public SandboxScene(GameView gameView) {
        // TODO: Weird hack, perhaps redo.
        this.gameView = gameView;
    }

    @Override
    public void create() {
        Entity camera = super.ECSManager.createEntity();
        CameraComponent mainCameraComponent = new CameraComponent(true, CameraComponent.ProjectionType.PERSPECTIVE, (float) this.gameView.getWidth() / this.gameView.getHeight(), 45f, 0.1f, 1000f);
        Entity monkey1 = super.ECSManager.createEntity();
        Entity monkey2 = super.ECSManager.createEntity();
        Entity monkey3 = super.ECSManager.createEntity();
        try {
            super.ECSManager.emplaceComponent(camera.getId(), new TransformComponent(new Vector3(0, 0, 0)));
            super.ECSManager.emplaceComponent(camera.getId(), mainCameraComponent);

            super.ECSManager.emplaceComponent(monkey1.getId(), new TransformComponent(new Vector3(0, 0, 10f)));
            super.ECSManager.emplaceComponent(monkey1.getId(), new MeshRendererComponent(
                    new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource("primitives/monkey.obj").toURI()))),
                    Texture.fromImage(new File(Thread.currentThread().getContextClassLoader().getResource("brick-texture.png").getFile())),
                    MeshRendererComponent.MeshRenderingType.SOLID));

            super.ECSManager.emplaceComponent(monkey2.getId(), new TransformComponent(new Vector3(-10f, 0, -10f)));
            super.ECSManager.emplaceComponent(monkey2.getId(), new MeshRendererComponent(
                    new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource("primitives/monkey.obj").toURI()))),
                    Texture.fromImage(new File(Thread.currentThread().getContextClassLoader().getResource("brick-texture.png").getFile())),
                    MeshRendererComponent.MeshRenderingType.SOLID));

            super.ECSManager.emplaceComponent(monkey3.getId(), new TransformComponent(new Vector3(10f, 0, -10f)));
            super.ECSManager.emplaceComponent(monkey3.getId(), new MeshRendererComponent(
                    new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource("primitives/monkey.obj").toURI()))),
                    Texture.fromImage(new File(Thread.currentThread().getContextClassLoader().getResource("brick-texture.png").getFile())),
                    MeshRendererComponent.MeshRenderingType.SOLID));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        super.ECSManager.addGeneralComponentSystem(new CameraComponentSystem());
//        super.ECSManager.addGeneralComponentSystem(new RotatorSystem());
        super.ECSManager.addGeneralComponentSystem(new CameraShakerSystem());
        super.ECSManager.addGeneralComponentSystem(new RendererComponentSystem(this.gameView, mainCameraComponent));
    }
}
