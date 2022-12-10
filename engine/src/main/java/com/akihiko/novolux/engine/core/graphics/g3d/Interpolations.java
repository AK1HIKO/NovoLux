package com.akihiko.novolux.engine.core.graphics.g3d;

import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;
import com.akihiko.novolux.engine.core.math.MathUtils;
import com.akihiko.novolux.engine.core.math.gradients.BiTriGradient;
import com.akihiko.novolux.engine.core.math.gradients.TriGradient;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 28/11/22
 */
public class Interpolations {

    private final BiTriGradient texCoordsBiGradient;

    /**
     * Used for proper texture rendering. (fixes the perspective)
     * See the comparison, between affine texture mapping and current (using this variable) perspective-correct mapping:
     * <a href="https://www.youtube.com/watch?v=RyYEGdGwnFs">Affine vs perspective-correct texture mapping example</a>
     */
    private final TriGradient invZGradient;

    private final TriGradient zDepthGradient;

    private final TriGradient lightGradient;

    public Interpolations(Vertex minY, Vertex midY, Vertex maxY) {
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
        // Optimization step. Inverse derivative is always the same, so we can just calculate it in one gradient and then cache it:
        Vector2 invd = this.texCoordsBiGradient.getInvd();

        this.invZGradient = new TriGradient(
                invZArr,
                reducedPositions[0],
                reducedPositions[1],
                reducedPositions[2],
                invd
        );

        this.zDepthGradient = new TriGradient(
                new float[]{
                        minY.position().getZ(),
                        midY.position().getZ(),
                        maxY.position().getZ()
                },
                reducedPositions[0],
                reducedPositions[1],
                reducedPositions[2],
                invd
        );

        // Simple Lambertian shading: "https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/diffuse-lambertian-shading"
        // Placeholder lighting at position x=5, y=2, z=-1
        Vector4 tempLight = new Vector4(5, 2, -1);
        this.lightGradient = new TriGradient(
                new float[]{
                        MathUtils.clamp(minY.normal().dot(tempLight), 0, 1)* 0.9f + 0.2f,
                        MathUtils.clamp(midY.normal().dot(tempLight), 0, 1)* 0.9f + 0.2f,
                        MathUtils.clamp(maxY.normal().dot(tempLight), 0, 1)* 0.9f + 0.2f,
                },
                reducedPositions[0],
                reducedPositions[1],
                reducedPositions[2],
                invd
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

    public TriGradient getLightGradient() {
        return lightGradient;
    }

    public float getInvZ(int i) {
        return this.invZGradient.getPoint(i);
    }

    public float getZDepth(int i) {
        return this.zDepthGradient.getPoint(i);
    }

    public float getLight(int i) {
        return this.lightGradient.getPoint(i);
    }

}
