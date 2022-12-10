package com.akihiko.novolux.engine.core.math.gradients;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;

/**
 * 3-value gradient.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 29/11/22
 */
public class TriGradient {

    private final float[] points;
    private final float xSlope;
    private final float ySlope;
    private final Vector2 invd;

    public TriGradient(float[] points, Vector2 start, Vector2 middle, Vector2 end) {
        this(points, start, middle, end,
                1.0f / (
                        ((middle.getX() - end.getX()) * (start.getY() - end.getY()))
                                -
                                ((start.getX() - end.getX()) * (middle.getY() - end.getY()))
                )
        );
    }

    public TriGradient(float[] points, Vector2 start, Vector2 middle, Vector2 end, float invdx) {
        this(points, start, middle, end, new Vector2(invdx, -invdx));
    }

    public TriGradient(float[] points, Vector2 start, Vector2 middle, Vector2 end, Vector2 invd) {
        this.invd = invd;
        this.points = points;
        this.xSlope = this.calculateSlope(this.points, start.getY(), middle.getY(), end.getY(), this.invd.getX());
        this.ySlope = this.calculateSlope(this.points, start.getX(), middle.getX(), end.getX(), this.invd.getY());
    }

    /**
     * Linear single-axis interpolation:
     *
     * @param points
     * @param start
     * @param middle
     * @param end
     * @param invd
     * @return
     */
    private float calculateSlope(float[] points, float start, float middle, float end, float invd) {
        return (
                ((points[1] - points[2]) * (start - end))
                        -
                        ((points[0] - points[2]) * (middle - end))
        ) * invd;
    }

    public float getPoint(int i) {
        return this.points[i];
    }

    public float getXSlope() {
        return xSlope;
    }

    public float getYSlope() {
        return ySlope;
    }

    public Vector2 getInvd() {
        return this.invd;
    }
}
