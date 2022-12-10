package com.akihiko.novolux.engine.core.math.gradients;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;

/**
 * Bilinear interpolation 3-value gradient.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 29/11/22
 */
public class BiTriGradient {

    private final Vector2[] points;
    private final TriGradient xGradient;
    private final TriGradient yGradient;

    private final Vector2 invd;

    public BiTriGradient(Vector2[] points, Vector2 start, Vector2 middle, Vector2 end) {
        this.points = points;
        float[] pointsX = new float[]{
                points[0].getX(),
                points[1].getX(),
                points[2].getX()
        };
        float[] pointsY = new float[]{
                points[0].getY(),
                points[1].getY(),
                points[2].getY()
        };
        this.xGradient = new TriGradient(pointsX, start, middle, end);
        this.invd = this.xGradient.getInvd();
        this.yGradient = new TriGradient(pointsY, start, middle, end, this.invd);
    }

    public Vector2 getPoint(int i) {
        return this.points[i];
    }

    public TriGradient getXGradient() {
        return xGradient;
    }

    public TriGradient getYGradient() {
        return yGradient;
    }

    public Vector2 getInvd() {
        return this.invd;
    }
}
