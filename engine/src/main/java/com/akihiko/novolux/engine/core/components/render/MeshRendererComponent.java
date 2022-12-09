package com.akihiko.novolux.engine.core.components.render;

import com.akihiko.novolux.ecs.Component;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.rendering.Texture;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class MeshRendererComponent extends Component {

    public static final int id = Component.generateComponentId();
    @Override
    public int ID() {
        return MeshRendererComponent.id;
    }

    public enum MeshRenderingType{
        SOLID, WIREFRAME
    }
    private Mesh mesh;
    private Texture texture;
    private MeshRenderingType renderingType;

    public MeshRendererComponent(Mesh mesh, Texture texture, MeshRenderingType renderingType) {
        this.mesh = mesh;
        this.texture = texture;
        this.renderingType = renderingType;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public MeshRendererComponent setMesh(Mesh mesh) {
        this.mesh = mesh;
        return this;
    }

    public MeshRenderingType getRenderingType() {
        return renderingType;
    }

    public MeshRendererComponent setRenderingType(MeshRenderingType renderingType) {
        this.renderingType = renderingType;
        return this;
    }

    public Texture getTexture() {
        return texture;
    }

    public MeshRendererComponent setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }
}
