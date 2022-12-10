package com.akihiko.novolux.engine.core.graphics.g3d;

import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;
import com.akihiko.novolux.engine.core.math.gradients.BiTriGradient;
import com.akihiko.novolux.engine.core.math.gradients.TriGradient;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 28/11/22
 */
public class Gradients {

    private final BiTriGradient texCoordsBiGradient;

    /**
     * Used for proper texture rendering. (fixes the perspective)
     * See the comparison, between affine texture mapping and current (using this variable) perspective-correct mapping:
     * <a href="https://www.youtube.com/watch?v=RyYEGdGwnFs">Affine vs perspective-correct texture mapping example</a>
     */
    private final TriGradient invZGradient;

    private final TriGradient zDepthGradient;

    public Gradients(Vertex minY, Vertex midY, Vertex maxY) {
        float[] invZArr = new float[]{
                1.0f / minY.position().getW(),
                1.0f / midY.position().getW(),
                1.0f / maxY.position().getW()
        };
        Vector2[] reducedPositions = new Vector2[]{
                new Vector2(minY.position()),
                new Vector2(midY.position()),
                new Vector2(maxY.position())
        };


        // Bilinear texture-mapping:
        this.texCoordsBiGradient = new BiTriGradient(
                new Vector2[]{
                        minY.texCoords().multiply(invZArr[0]),
                        midY.texCoords().multiply(invZArr[1]),
                        maxY.texCoords().multiply(invZArr[2])
                },
                reducedPositions[0],
                reducedPositions[1],
                reducedPositions[2]
        );

        this.invZGradient = new TriGradient(
                invZArr,
                reducedPositions[0],
                reducedPositions[1],
                reducedPositions[2]
        );

        this.zDepthGradient = new TriGradient(
                new float[]{
                        minY.position().getZ(),
                        midY.position().getZ(),
                        maxY.position().getZ()
                },
                reducedPositions[0],
                reducedPositions[1],
                reducedPositions[2]
        );
    }

    public Vector2 getTexCoord(int i) {
        return this.texCoordsBiGradient.getPoint(i);
    }

    public BiTriGradient getTexCoordsBiGradient() {
        return texCoordsBiGradient;
    }

    public TriGradient getInvZGradient() {
        return invZGradient;
    }

    public TriGradient getZDepthGradient() {
        return this.zDepthGradient;
    }

    public float getInvZ(int i) {
        return this.invZGradient.getPoint(i);
    }

    public float getZDepth(int i) {
        return this.zDepthGradient.getPoint(i);
    }

}
