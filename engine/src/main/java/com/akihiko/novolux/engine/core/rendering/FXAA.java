package com.akihiko.novolux.engine.core.rendering;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

/**
 * Custom software-side FXAA (Fast approXimate Anti-Aliasing) implementation.
 * Unlike super-sampling and SMAA, FXAA is very fast, and despite being worse than MSAA,
 * is performed as a last render pass (on already rendered pixel buffer), which makes it easier to implement.
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 28/11/22
 */
public class FXAA {

    // Comes from NVIDIA's research paper on FXAA:
    // https://developer.download.nvidia.com/assets/gamedev/files/sdk/11/FXAA_WhitePaper.pdf
    // https://gist.github.com/jwoolston/c96c22473110c9924d831cebb5f2cdb4
    private static final Vector4 LUMA = new Vector4(0, 0.299f, 0.587f, 0.114f);
    /**
     * X stores MIN THRESHOLD
     * Y stores MAX THRESHOLD
     * Represent edge contrast boundaries. Used to detect edges.
     * The idea is that if contrast is low, then it is probably not an edge,
     * but if contrast is too high, we are probably in a dark area, where FXAA will not be as noticeable and can be omitted.
     */
    private static final Vector2 EDGE_THRESHOLD = new Vector2(0.0312f, 0.125f);

    private final Vector2 invScreenSize;

    public FXAA(int width, int height) {
        this.invScreenSize = new Vector2(1.0f / width, 1.0f / height);
    }

    private float rgb2Luma(Vector4 rgb) {
        return (float) Math.sqrt(rgb.dot(FXAA.LUMA));
    }

    /**
     * Analog of the GLSL's <a href="https://registry.khronos.org/OpenGL-Refpages/gl4/html/textureOffset.xhtml">"textureOffset"</a> function.
     *
     * @return
     */
    private Vector4 textureOffset(FrameBuffer frameBuffer, Vector2 currentPosition, Vector2 offset) {
        Vector2 position = offset.add(currentPosition).clamp(new Vector2(0, 0), new Vector2(frameBuffer.getWidth(), frameBuffer.getHeight()));
        return frameBuffer.getPixel((int) position.getX(), (int) position.getY());
    }

    /**
     * Adapted for Java from <a href="http://blog.simonrodriguez.fr/articles/2016/07/implementing_fxaa.html">Implementing FXAA (Simon Rodriguez)</a>
     *
     * @param frameBuffer
     */
    public void apply(FrameBuffer frameBuffer) {
        for (int y = 0; y < frameBuffer.getHeight(); y++) {
            for (int x = 0; x < frameBuffer.getWidth(); x++) {
                Vector2 position = new Vector2(x, y);

                // Luma of the current pixel:
                float lumaCenter = rgb2Luma(textureOffset(frameBuffer, position, Vector2.ZERO()));

                // Luma of the four cross pixels:
                float lumaDown = rgb2Luma(textureOffset(frameBuffer, position, Vector2.DOWN()));
                float lumaUp = rgb2Luma(textureOffset(frameBuffer, position, Vector2.UP()));
                float lumaLeft = rgb2Luma(textureOffset(frameBuffer, position, Vector2.LEFT()));
                float lumaRight = rgb2Luma(textureOffset(frameBuffer, position, Vector2.RIGHT()));

                // Maximum and Minimum Luma of the cross pixels:
                float lumaMin = Math.min(lumaCenter, Math.min(Math.min(lumaDown, lumaUp), Math.min(lumaLeft, lumaRight)));
                float lumaMax = Math.max(lumaCenter, Math.max(Math.max(lumaDown, lumaUp), Math.max(lumaLeft, lumaRight)));

                float lumaDelta = lumaMax - lumaMin;

                // Optimization: decide whether we need to perform FXAA or not.
                if (lumaDelta < Math.max(FXAA.EDGE_THRESHOLD.getX(), lumaMax * FXAA.EDGE_THRESHOLD.getY())) {
                    return;
                }

                // Now we can sample the remaining four diagonal pixels:
                float lumaDLeft = rgb2Luma(textureOffset(frameBuffer, position, new Vector2(-1, -1)));
                float lumaULeft = rgb2Luma(textureOffset(frameBuffer, position, new Vector2(-1, 1)));
                float lumaURight = rgb2Luma(textureOffset(frameBuffer, position, new Vector2(1, 1)));
                float lumaDRight = rgb2Luma(textureOffset(frameBuffer, position, new Vector2(1, -1)));

                // Combine four cross pixels:
                float lumaDU = lumaDown + lumaUp;
                float lumaLR = lumaLeft + lumaRight;

                // Combine corners:
                float lumaLeftCorners = lumaDLeft + lumaULeft;
                float lumaDownCorners = lumaDLeft + lumaDRight;
                float lumaUpCorners = lumaULeft + lumaURight;
                float lumaRightCorners = lumaDRight + lumaURight;

                // Compute an estimated gradiant along both axis:
                float edgeHorizontal = Math.abs(-2.0f * lumaLeft + lumaLeftCorners) + Math.abs(-2.0f * lumaCenter + lumaDU) * 2.0f + Math.abs(-2.0f * lumaRight + lumaRightCorners);
                float edgeVertical = Math.abs(-2.0f * lumaUp + lumaUpCorners) + Math.abs(-2.0f * lumaCenter + lumaLR) * 2.0f + Math.abs(-2.0f * lumaDown + lumaDownCorners);

                boolean isHorizontal = edgeHorizontal >= edgeVertical;

                // Similar to Multi-sampling, select two or more neighboring pixels in the opposite directions of the edge:
                float luma1 = isHorizontal ? lumaDown : lumaLeft;
                float luma2 = isHorizontal ? lumaUp : lumaRight;

                // Compute gradients in the selected direction:
                float gradient1 = luma1 - lumaCenter;
                float gradient2 = luma2 - lumaCenter;

                boolean is1Steepest = Math.abs(gradient1) >= Math.abs(gradient2);

                // Create a normalized gradient in the selected direction:
                float gradientScaled = 0.25f * Math.max(Math.abs(gradient1), Math.abs(gradient2));

                // Choose the step size:
                float stepSize = isHorizontal ? invScreenSize.getY() : invScreenSize.getX();

                // Average luma:
                float lumaLocalAvg = 0.0f;

                if (is1Steepest) {
                    // Switch the direction
                    stepSize = -stepSize;
                    lumaLocalAvg = 0.5f * (luma1 + lumaCenter);
                } else {
                    lumaLocalAvg = 0.5f * (luma2 + lumaCenter);
                }
                // TODO: IMPLEMENT!!!
            }
        }
    }

    private void antiAlasPixel() {

    }

}
