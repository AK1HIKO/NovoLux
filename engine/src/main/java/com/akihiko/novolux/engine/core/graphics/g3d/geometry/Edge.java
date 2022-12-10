package com.akihiko.novolux.engine.core.graphics.g3d.geometry;

import com.akihiko.novolux.engine.core.graphics.g3d.Gradients;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;

/**
 * Represents an Edge as a linear function in form of: x = f(y) = x0 + slope * intercept.
 * Reference: <a href="https://en.wikipedia.org/wiki/Linear_function">Linear Function</a>.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 28/11/22
 */
public class Edge {

    private float posX;
    private final float posXSlope;
    private final int yStart;
    private final int yEnd;

    private Vector2 texCoords;
    private final Vector2 texCoordsSlope;


    /**
     * Used for proper texture rendering. (fixes the perspective)
     * See the comparison, between affine texture mapping and current (using this variable) perspective-correct mapping:
     * <a href="https://www.youtube.com/watch?v=RyYEGdGwnFs">Affine vs perspective-correct texture mapping example</a>
     */
    private float invZ;
    private float invZSlope;

    private float zDepth;
    private float zDepthSlope;

    public Edge(Gradients gradients, Vertex start, Vertex end, int startIndex) {
        this.yStart = (int) Math.ceil(start.position().getY());
        this.yEnd = (int) Math.ceil(end.position().getY());

        float yIntercept = yStart - start.position().getY();

        this.posXSlope = (end.position().getX() - start.position().getX()) / (end.position().getY() - start.position().getY());
        this.posX = start.position().getX() + this.posXSlope * yIntercept;

        float xIntercept = this.posX - start.position().getX();


        // TODO: texGradient.getTexCoord(startIndex);
        this.texCoords = new Vector2(
                gradients.getTexCoord(startIndex).getX()
                        + gradients.getTexCoordsBiGradient().getXGradient().getXSlope() * xIntercept
                        + gradients.getTexCoordsBiGradient().getXGradient().getYSlope() * yIntercept,

                gradients.getTexCoord(startIndex).getY()
                        + gradients.getTexCoordsBiGradient().getYGradient().getXSlope() * xIntercept
                        + gradients.getTexCoordsBiGradient().getYGradient().getYSlope() * yIntercept
        );
        this.texCoordsSlope = new Vector2(
                gradients.getTexCoordsBiGradient().getXGradient().getYSlope() + gradients.getTexCoordsBiGradient().getXGradient().getXSlope() * posXSlope,
                gradients.getTexCoordsBiGradient().getYGradient().getYSlope() + gradients.getTexCoordsBiGradient().getYGradient().getXSlope() * posXSlope
        );

        this.invZ = gradients.getInvZ(startIndex) + gradients.getInvZGradient().getYSlope() * yIntercept + gradients.getInvZGradient().getXSlope() * xIntercept;
        this.invZSlope = gradients.getInvZGradient().getYSlope() + gradients.getInvZGradient().getXSlope() * posXSlope;

        this.zDepth = gradients.getZDepth(startIndex) + gradients.getZDepthGradient().getYSlope() * yIntercept + gradients.getZDepthGradient().getXSlope() * xIntercept;
        this.zDepthSlope = gradients.getZDepthGradient().getYSlope() + gradients.getZDepthGradient().getXSlope() * posXSlope;
    }

    public void stepAlong() {
        this.posX += this.posXSlope;
        this.texCoords = this.texCoords.add(this.texCoordsSlope);
        this.invZ += this.invZSlope;
        this.zDepth += this.zDepthSlope;
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosXSlope() {
        return this.posXSlope;
    }

    public int getYStart() {
        return this.yStart;
    }

    public int getYEnd() {
        return this.yEnd;
    }

    public Vector2 getTexCoords() {
        return this.texCoords;
    }

    public Vector2 getTexCoordsSlope() {
        return this.texCoordsSlope;
    }

    public float getInvZ() {
        return this.invZ;
    }

    public float getInvZSlope() {
        return this.invZSlope;
    }

    public float getZDepth() {
        return zDepth;
    }

    public Edge setZDepth(float zDepth) {
        this.zDepth = zDepth;
        return this;
    }

    public float getZDepthSlope() {
        return zDepthSlope;
    }

    public Edge setzDepthSlope(float zDepthSlope) {
        this.zDepthSlope = zDepthSlope;
        return this;
    }
}
