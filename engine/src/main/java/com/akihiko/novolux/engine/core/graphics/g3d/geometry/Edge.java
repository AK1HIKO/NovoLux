package com.akihiko.novolux.engine.core.graphics.g3d.geometry;

import com.akihiko.novolux.engine.core.graphics.g3d.Interpolations;
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
    private final float invZSlope;

    private float zDepth;
    private final float zDepthSlope;

    private float light;
    private final float lightSlope;

    public Edge(Interpolations interpolations, Vertex start, Vertex end, int startIndex) {
        this.yStart = (int) Math.ceil(start.position().getY());
        this.yEnd = (int) Math.ceil(end.position().getY());

        float yIntercept = yStart - start.position().getY();

        this.posXSlope = (end.position().getX() - start.position().getX()) / (end.position().getY() - start.position().getY());
        this.posX = start.position().getX() + this.posXSlope * yIntercept;

        float xIntercept = this.posX - start.position().getX();


        // TODO: texGradient.getTexCoord(startIndex);
        this.texCoords = new Vector2(
                interpolations.getTexCoord(startIndex).getX()
                        + interpolations.getTexCoordsBiGradient().getXGradient().getXSlope() * xIntercept
                        + interpolations.getTexCoordsBiGradient().getXGradient().getYSlope() * yIntercept,

                interpolations.getTexCoord(startIndex).getY()
                        + interpolations.getTexCoordsBiGradient().getYGradient().getXSlope() * xIntercept
                        + interpolations.getTexCoordsBiGradient().getYGradient().getYSlope() * yIntercept
        );
        this.texCoordsSlope = new Vector2(
                interpolations.getTexCoordsBiGradient().getXGradient().getYSlope() + interpolations.getTexCoordsBiGradient().getXGradient().getXSlope() * posXSlope,
                interpolations.getTexCoordsBiGradient().getYGradient().getYSlope() + interpolations.getTexCoordsBiGradient().getYGradient().getXSlope() * posXSlope
        );

        this.invZ = interpolations.getInvZ(startIndex) + interpolations.getInvZGradient().getYSlope() * yIntercept + interpolations.getInvZGradient().getXSlope() * xIntercept;
        this.invZSlope = interpolations.getInvZGradient().getYSlope() + interpolations.getInvZGradient().getXSlope() * posXSlope;

        this.zDepth = interpolations.getZDepth(startIndex) + interpolations.getZDepthGradient().getYSlope() * yIntercept + interpolations.getZDepthGradient().getXSlope() * xIntercept;
        this.zDepthSlope = interpolations.getZDepthGradient().getYSlope() + interpolations.getZDepthGradient().getXSlope() * posXSlope;

        this.light = interpolations.getLight(startIndex) + interpolations.getLightGradient().getYSlope() * yIntercept + interpolations.getLightGradient().getXSlope() * xIntercept;
        this.lightSlope = interpolations.getLightGradient().getYSlope() + interpolations.getLightGradient().getXSlope() * posXSlope;
    }

    public void stepAlong() {
        this.posX += this.posXSlope;
        this.texCoords = this.texCoords.add(this.texCoordsSlope);
        this.invZ += this.invZSlope;
        this.zDepth += this.zDepthSlope;
        this.light += this.lightSlope;
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

    public float getZDepthSlope() {
        return zDepthSlope;
    }

    public float getLight() {
        return light;
    }

    public float getLightSlope() {
        return lightSlope;
    }
}
