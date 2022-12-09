package com.akihiko.novolux.engine.core.components.render;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.ecs.EntitiesQuery;
import com.akihiko.novolux.ecs.EntityQueryResult;
import com.akihiko.novolux.engine.GameView;
import com.akihiko.novolux.engine.core.components.TransformComponent;
import com.akihiko.novolux.engine.core.components.camera.CameraComponent;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;
import com.akihiko.novolux.engine.core.graphics.utils.OBJModelLoader;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;
import com.akihiko.novolux.engine.core.rendering.Renderer;
import com.akihiko.novolux.engine.core.rendering.Texture;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.AbstractMap;
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
        for(EntityQueryResult qr : eComponents){
            MeshRendererComponent mr = (MeshRendererComponent) qr.components().get(MeshRendererComponent.id);
            TransformComponent tc = (TransformComponent) qr.components().get(TransformComponent.id);
            // TODO: Combine all the mesh data in one/two lists and then simply render them at once.
            batch.add(new BatchData(mr.getMesh(), mr.getTexture(), tc.getTransformationMatrix()));
        }
//        Matrix4x4 transform = Matrix4x4.PERSPECTIVE(70.0f, (float) getWidth()/getHeight(), 0.1f, 1000f).multiply(Matrix4x4.TRANSLATION(0.0f, 0.0f, 5f - debugcounter).multiply(Matrix4x4.EULER_ROTATION(debugcounter, debugcounter, 0)));
//        try{
//            batch.add(new BatchData(
//                    new Mesh(new OBJModelLoader().loadModel(Path.of(Thread.currentThread().getContextClassLoader().getResource("primitives/monkey.obj").toURI()))),
//                    Texture.fromImage(new File(Thread.currentThread().getContextClassLoader().getResource("brick-texture.png").getFile())),
//                    new TransformComponent(new Vector3(0, 0, 5f)).getTransformationMatrix()
//            ));
//        }catch (Exception e){
//
//        }
    }

    private Queue<BatchData> batch = new LinkedList<>();
    private void render(Renderer renderer){
        for(BatchData b : batch) {
            this.drawMesh(renderer, b.mesh(), mainCamera.getViewProjectionMatrix(), b.transformationMatrix(), b.texture());
        }


//        Matrix4x4 sspaceMatrix = Matrix4x4.WORLD_TO_SCREEN(gameView.getWidth(), gameView.getHeight());
//        for(AbstractMap.SimpleEntry<Mesh, Matrix4x4> b : batch){
//            Matrix4x4 transformationMatrix = b.getValue();
//            Mesh mesh = b.getKey();
//            for(int i = 0; i < mesh.tris().length; i++){
//                Face t = mesh.tris()[i];
//                Matrix4x4 projected = mainCamera.project(transformationMatrix);
//
////                Vector4 tA = sspaceMatrix.transform(projected.transform(mesh.verts()[t.v1()])).removePerspective();
////                Vector4 tB = sspaceMatrix.transform(projected.transform(mesh.verts()[t.v2()])).removePerspective();
////                Vector4 tC = sspaceMatrix.transform(projected.transform(mesh.verts()[t.v3()])).removePerspective();
//
//                /*Vector4 projectedA = mainCamera.project(tA).add(new Vector4(+1f, +1f, 0f)).multiply(new Vector4(0.5f * this.gameView.getWidth(), 0.5f * this.gameView.getHeight(), 1f));
//                Vector4 projectedB = mainCamera.project(tB).add(new Vector4(+1f, +1f, 0f)).multiply(new Vector4(0.5f * this.gameView.getWidth(), 0.5f * this.gameView.getHeight(), 1f));;
//                Vector4 projectedC = mainCamera.project(tC).add(new Vector4(+1f, +1f, 0f)).multiply(new Vector4(0.5f * this.gameView.getWidth(), 0.5f * this.gameView.getHeight(), 1f));;
//                */
//
//                g.setColor(Color.CYAN);
//                GraphicsHelper.drawTriangle(g, (int)tA.getX(), (int)tA.getY(), (int)tB.getX(), (int)tB.getY(), (int)tC.getX(), (int)tC.getY());
//                /* TODO: Drawing using polygons is very slow and clunky. Has a lot of jagged edges and performs a lot of unnecessary calculations.
//                Polygon p = new Polygon();
//                p.addPoint((int)projectedA.getX(), (int)projectedA.getY());
//                p.addPoint((int)projectedB.getX(), (int)projectedB.getY());
//                p.addPoint((int)projectedC.getX(), (int)projectedC.getY());
//                g.drawPolygon(p);*/
//                g.setColor(Color.RED);
//                g.drawString("FPS: 30???", 0, g.getFontMetrics().getHeight());
//            }
//        }
    }

    private void drawMesh(Renderer renderer, Mesh mesh, Matrix4x4 viewProjectionMatrix, Matrix4x4 transformationMatrix, Texture texture){
        transformationMatrix = viewProjectionMatrix.multiply(transformationMatrix);
        for(int i = 0; i < mesh.getIndices().size(); i+=3){
            Vertex a = mesh.getVerts().get(mesh.getIndices().get(i+0));
            Vertex b = mesh.getVerts().get(mesh.getIndices().get(i+1));
            Vertex c = mesh.getVerts().get(mesh.getIndices().get(i+2));
            renderer.drawTriangle(a.transform(transformationMatrix), b.transform(transformationMatrix), c.transform(transformationMatrix), texture);
        }
    }

    private record BatchData(Mesh mesh, Texture texture, Matrix4x4 transformationMatrix){}

}
