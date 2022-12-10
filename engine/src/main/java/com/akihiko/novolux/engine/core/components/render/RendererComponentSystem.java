package com.akihiko.novolux.engine.core.components.render;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.GameView;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponent;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.rendering.Renderer;
import com.akihiko.novolux.engine.core.rendering.Texture;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class RendererComponentSystem extends ComponentSystem {

    private static final int id = ComponentSystem.generateComponentSystemId();

    @Override
    public int ID() {
        return RendererComponentSystem.id;
    }


    private final GameView gameView;
    private final CameraComponent mainCamera;

    public RendererComponentSystem(GameView gameView, CameraComponent mainCamera) {
        super(new EntitiesQuery(TransformComponent.id, MeshRendererComponent.id));
        this.gameView = gameView;
        this.gameView.addRenderCall(this::render);
        this.mainCamera = mainCamera;
    }

    @Override
    public void onUpdate(List<EntityQueryResult> eComponents, float deltaTime) {
        // Since we do not reference this list anywhere outside, instead of O(n) clear, we can simply create a new one O(1):
        batch = new LinkedList<>();
        for (EntityQueryResult qr : eComponents) {
            MeshRendererComponent mr = (MeshRendererComponent) qr.components().get(MeshRendererComponent.id);
            TransformComponent tc = (TransformComponent) qr.components().get(TransformComponent.id);
            batch.add(new BatchData(mr.getMesh(), mr.getTexture(), tc.getTransformationMatrix()));
        }
    }

    private Queue<BatchData> batch = new LinkedList<>();

    private void render(Renderer renderer) {
        for (BatchData b : batch) {
            this.drawMesh(renderer, b.mesh(), mainCamera.getViewProjectionMatrix(), b.transformationMatrix(), b.texture());
        }
    }

    private void drawMesh(Renderer renderer, Mesh mesh, Matrix4x4 viewProjectionMatrix, Matrix4x4 transformationMatrix, Texture texture) {
        // Model View Projection matrix:
        Matrix4x4 mvpMatrix = viewProjectionMatrix.multiply(transformationMatrix);
        for (int i = 0; i < mesh.getIndices().size(); i += 3) {
            Vertex a = mesh.getVerts().get(mesh.getIndices().get(i + 0));
            Vertex b = mesh.getVerts().get(mesh.getIndices().get(i + 1));
            Vertex c = mesh.getVerts().get(mesh.getIndices().get(i + 2));
            renderer.drawTriangle(a.transform(mvpMatrix, transformationMatrix), b.transform(mvpMatrix, transformationMatrix), c.transform(mvpMatrix, transformationMatrix), texture);
        }
    }

    private record BatchData(Mesh mesh, Texture texture, Matrix4x4 transformationMatrix) {
    }

}
