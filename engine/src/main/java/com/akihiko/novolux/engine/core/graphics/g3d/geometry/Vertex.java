package com.akihiko.novolux.engine.core.graphics.g3d.geometry;

import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 * Stores the vertex data.
 * @param position position of a vertex in space.
 * @param texCoords texture coordinates of a vertex, used for texture UV-mapping. For more info: <a href="https://learnopengl.com/Getting-started/Textures">OpenGL Textures</a>
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public record Vertex(Vector4 position, Vector2 texCoords) {

    public Vertex transform(Matrix4x4 transformationMatrix){
        return new Vertex(transformationMatrix.transform(this.position), this.texCoords);
    }

    /**
     * To make perspective distortion work. In a perspective projection matrix, the Z coordinate gets "mixed" into the W output component.
     * So the smaller the value of the Z coordinate, i.e. the closer to the origin, the more things get scaled up, i.e. bigger on screen.
     * That is why, to properly get the actual screen position, we want to normalize it.
     * Reference: <a href="https://stackoverflow.com/questions/17269686/why-do-we-need-perspective-division">Perspective Division</a>
     * @return
     */
    public Vertex perspectiveDivide(){
        return new Vertex(
                new Vector4(
                        this.position.getX()/this.position.getW(),
                        this.position.getY()/this.position.getW(),
                        this.position.getZ()/this.position.getW(),
                        // OpenGL and Vulkan prefer to also divide it for the consistency, but it is not a necessary step,
                        // and without it, we can use this component later on for optimization.
                        this.position.getW()
                ),
                this.texCoords
        );
    }

    public float twoAreas(Vertex b, Vertex c){
        float x1 = b.position.getX() - this.position.getX();
        float y1 = b.position.getY() - this.position.getY();

        float x2 = c.position.getX() - this.position.getX();
        float y2 = c.position.getY() - this.position.getY();

        return (x1 * y2 - x2 * y1);
    }

    public Vertex lerp(Vertex b, float t){
        return new Vertex(
                this.position.lerp(b.position, t),
                this.texCoords.lerp(b.texCoords, t)
        );
    }

    public boolean isInViewFrustum(){
        return Math.abs(position.getX()) <= Math.abs(position().getW()) && Math.abs(position.getY()) <= Math.abs(position().getW()) && Math.abs(position.getZ()) <= Math.abs(position().getW());
    }

}
