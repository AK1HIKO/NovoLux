package com.akihiko.novolux.demo;

import com.akihiko.novolux.demo.monkeyrotator.MonkeyRotatorComponent;
import com.akihiko.novolux.demo.monkeyrotator.MonkeyRotatorSystem;
import com.akihiko.novolux.ecs.Entity;
import com.akihiko.novolux.engine.Application;
import com.akihiko.novolux.engine.core.components.TagComponent;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponentSystem;
import com.akihiko.novolux.engine.core.components.render.MeshRendererComponent;
import com.akihiko.novolux.engine.core.components.render.RendererComponentSystem;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.utils.OBJModelLoader;
import com.akihiko.novolux.engine.core.math.tensors.quaternion.Quaternion;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.rendering.Texture;
import com.akihiko.novolux.engine.core.scene.Scene;
import com.akihiko.novolux.engine.utils.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * Demo Sandbox Scene
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class SandboxScene extends Scene {

    @Override
    public void create() {
        Logger.warn("Test Warn Message from SandboxScene.class");
        try {
            Entity camera = super.ECSManager.createEntity();

            CameraComponent mainCameraComponent = new CameraComponent(true, CameraComponent.ProjectionType.PERSPECTIVE, (float) Application.getGameView().getWidth() / Application.getGameView().getHeight(), 45f, 0.1f, 1000f);
            Entity map = super.ECSManager.createEntity();
            Entity monkey1 = super.ECSManager.createEntity();
            Entity monkey2 = super.ECSManager.createEntity();
            Entity monkey3 = super.ECSManager.createEntity();

            // Create camera, and set its position to 0,0,0.
            super.ECSManager.emplaceComponent(camera.getId(), new TransformComponent(new Vector3(0, 0, 0)));
            super.ECSManager.emplaceComponent(camera.getId(), mainCameraComponent);

            // Create 3 monkeys, that is rotating at speed 45 degrees a second:
            createMonkey(monkey1, new TransformComponent(new Vector3(0, 0, 5f)), 45f);
            // Create a monkey with smooth-shading enabled, with speed 90 degrees a second.
            createSmoothMonkey(monkey2, new TransformComponent(new Vector3(-3f, 1f, 5f), Quaternion.IDENTITY(), new Vector3(0.5f, 0.5f, 0.5f)), 90f);
            // Create a flat shading monkey with speed 180 degrees a second.
            createMonkey(monkey3, new TransformComponent(new Vector3(4f, 0f, 5f), Quaternion.IDENTITY(), new Vector3(1.5f, 1.5f, 1.5f)), 180f);

            // Import a map model.
            super.ECSManager.emplaceComponent(map.getId(), new TransformComponent(new Vector3(0, -1, 0), Quaternion.IDENTITY(), new Vector3(0.25f, 0.25f, 0.25f)));
            super.ECSManager.emplaceComponent(map.getId(), new MeshRendererComponent(
                    new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource("map.obj").toURI()))),
                    Texture.fromImage(new File(Thread.currentThread().getContextClassLoader().getResource("texture_atlas.png").getFile())),
                    MeshRendererComponent.MeshRenderingType.SOLID
            ));

            // Add component systems to the world.
            super.ECSManager.addGeneralComponentSystem(new CameraComponentSystem());
            super.ECSManager.addGeneralComponentSystem(new MonkeyRotatorSystem());
            super.ECSManager.addGeneralComponentSystem(new CameraMovementSystem(90, 0));
            super.ECSManager.addGeneralComponentSystem(new RendererComponentSystem(Application.getGameView(), mainCameraComponent));

            // Add custom GUI rendering:
            Application.getGameView().addGUICall(this::drawDemoGUI);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createMonkey(Entity monkey, TransformComponent transformComponent, String objname, float degPerSecond) throws IOException, URISyntaxException {
        super.ECSManager.emplaceComponent(monkey.getId(), transformComponent);
        super.ECSManager.emplaceComponent(monkey.getId(), new MeshRendererComponent(new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource(objname).toURI()))),
                Texture.fromImage(new File(Thread.currentThread().getContextClassLoader().getResource("brick-texture.png").getFile())),
                MeshRendererComponent.MeshRenderingType.SOLID
        ));
        super.ECSManager.emplaceComponent(monkey.getId(), new MonkeyRotatorComponent(degPerSecond));
        super.ECSManager.emplaceComponent(monkey.getId(), new TagComponent("monkey"));
    }

    private void createMonkey(Entity monkey, TransformComponent transformComponent, float degPerSecond) throws IOException, URISyntaxException {
        createMonkey(monkey, transformComponent, "primitives/monkey.obj", degPerSecond);
    }

    private void createSmoothMonkey(Entity monkey, TransformComponent transformComponent, float degPerSecond) throws IOException, URISyntaxException {
        createMonkey(monkey, transformComponent, "primitives/smonkey.obj", degPerSecond);
    }

    private void drawDemoGUI(Graphics2D g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("GUI EXAMPLE:", 10, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("NovoLux DEMO", 10, 60);

        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.ITALIC, 10));
        g.drawString("Press 'ENTER' to enable 'Monkey Rotator' Component System.", 10, 80);
        g.drawString("Press 'BACKSPACE' to disable 'Monkey Rotator' Component System.", 10, 100);

        g.setFont(new Font("Arial", Font.ITALIC, 10));
        g.drawString("Use 'Horizontal Arrow Keys' to rotate camera.", 10, 120);
        g.drawString("Use 'WASD' to move camera. (Note, that movement is absolute, meaning that it is independent of camera rotation)", 10, 140);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("COPYRIGHTS:", 10, 170);

        g.setFont(new Font("Arial", Font.ITALIC, 10));
        g.drawString("Textures are taken from NS's Doom Texture Expansion.", 10, 190);
        g.drawString("Suzanne (Monkey) model is taken from Blender's standard model library.", 10, 210);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Dynamic GUI Example:", 10, 240);

        g.setFont(new Font("Arial", Font.ITALIC, 10));
        g.drawString("Mouse Position: " + Application.getInputManager().getMousePosition(), 10, 260);
    }
}
