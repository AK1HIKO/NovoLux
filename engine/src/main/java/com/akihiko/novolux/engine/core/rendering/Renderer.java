package com.akihiko.novolux.engine.core.rendering;

import com.akihiko.novolux.engine.core.graphics.g3d.Gradients;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Edge;
import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;
import com.akihiko.novolux.engine.core.math.tensors.matrix.Matrix4x4;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <a href="https://en.wikipedia.org/wiki/Scanline_rendering">Scanline Rendering</a> - very fast and easy-to-implement type of renderer.<br/>
 * Implemented with the algorithms explained in: <a href="https://www.amazon.com/Computer-Graphics-Scratch-Gabriel-Gambetta/dp/1718500769">Computer Graphics from Scratch: A Programmer's Introduction to 3D Rendering</a><br/>
 * And various optimizations from: <a href="https://github.com/ssloy/tinyrenderer">Tiny Renderer</a>
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public class Renderer {

    private final FrameBuffer renderBuffer;
    private final float[] zBuffer;

    public Renderer(FrameBuffer renderBuffer) {
        this.renderBuffer = renderBuffer;
        this.zBuffer = new float[renderBuffer.size];
        this.clearZBuffer();
    }

    @Deprecated
    public void drawMesh(Mesh mesh, Matrix4x4 transformationMatrix, Texture texture){
        for(int i = 0; i < mesh.getIndices().size(); i+=3){
            Vertex a = mesh.getVerts().get(mesh.getIndices().get(i+0));
            Vertex b = mesh.getVerts().get(mesh.getIndices().get(i+1));
            Vertex c = mesh.getVerts().get(mesh.getIndices().get(i+2));
            drawTriangle(a.transform(transformationMatrix), b.transform(transformationMatrix), c.transform(transformationMatrix), texture);
        }
    }

    public void drawTriangle(Vertex a, Vertex b, Vertex c, Texture texture){
        // Quick optimization, that will skip all the calculations if all our vertices are inside the view frustum. (meaning that no clipping is needed)
        if(a.isInViewFrustum() && b.isInViewFrustum() && c.isInViewFrustum()){
            fillTriangle(a, b, c, texture);
            return;
        }

        List<Vertex> verts = new ArrayList<>(){{
            add(a);
            add(b);
            add(c);
        }};

        if(clipPolygonAxis(verts, 0) && clipPolygonAxis(verts, 1) && clipPolygonAxis(verts, 2)){
            Vertex startVertex = verts.get(0);

            for(int i = 1; i < verts.size()-1; i++){
                fillTriangle(startVertex, verts.get(i), verts.get(i+1), texture);
            }
        }


    }

    private void fillTriangle(Vertex a, Vertex b, Vertex c, Texture texture){
        Matrix4x4 sstransform = Matrix4x4.WORLD_TO_SCREEN(this.renderBuffer.getWidth()/2f, this.renderBuffer.getHeight()/2f);
        Vertex minY = a.transform(sstransform).perspectiveDivide();
        Vertex midY = b.transform(sstransform).perspectiveDivide();
        Vertex maxY = c.transform(sstransform).perspectiveDivide();

        // Backface culling (only for the object)
        if(minY.twoAreas(maxY, midY) >= 0)
            return;

        // Sort passed vertices by Y coordinate:
        if(maxY.position().getY() < midY.position().getY()){
            Vertex temp = maxY;
            maxY = midY;
            midY = temp;
        }
        if(midY.position().getY() < minY.position().getY()){
            Vertex temp = midY;
            midY = minY;
            minY = temp;
        }
        if(maxY.position().getY() < midY.position().getY()){
            Vertex temp = maxY;
            maxY = midY;
            midY = temp;
        }

        // TODO: Try to replace side with the actual X-coord sorting.
        scanlineTriangle(minY, midY, maxY, minY.twoAreas(maxY, midY) >= 0, texture);
    }

    private boolean clipPolygonAxis(List<Vertex> polygonVerts, int clipAxisIndex){
        List<Vertex> auxList = clipPolygonComponent(polygonVerts, clipAxisIndex, 1);
        polygonVerts.clear();
        if(auxList.isEmpty())
            return false;

        polygonVerts.addAll(clipPolygonComponent(auxList, clipAxisIndex, -1));
        auxList.clear();

        return !polygonVerts.isEmpty();
    }

    private List<Vertex> clipPolygonComponent(List<Vertex> polygonVerts, int clipComponentIndex, float clipAmount){
        List<Vertex> result = new ArrayList<>();

        Vertex prevVert = polygonVerts.get(polygonVerts.size()-1);
        float prevComponent = prevVert.position().get(clipComponentIndex) * clipAmount;
        boolean isPrevClipped = prevComponent <= prevVert.position().getW();

        for(Vertex currVert : polygonVerts){
            float currComponent = currVert.position().get(clipComponentIndex) * clipAmount;
            boolean isCurrClipped = currComponent <= currVert.position().getW();

            // XOR
            if(isCurrClipped ^ isPrevClipped){
                float lerpT = (prevVert.position().getW() - prevComponent) / ((prevVert.position().getW() - prevComponent) - (currVert.position().getW() - currComponent));
                result.add(prevVert.lerp(currVert, lerpT));
            }

            if(isCurrClipped){
                result.add(currVert);
            }

            prevVert = currVert;
            prevComponent = currComponent;
            isPrevClipped = isCurrClipped;
        }
        return result;
    }

    private void scanlineTriangle(Vertex minY, Vertex midY, Vertex maxY, boolean side, Texture texture){
        Gradients textureGradients = new Gradients(minY, midY, maxY);
        Edge ac = new Edge(textureGradients, minY, maxY, 0);
        Edge ab = new Edge(textureGradients, minY, midY, 0);
        Edge bc = new Edge(textureGradients, midY, maxY, 1);

        scanlineEdge(ac, ab, side, texture);
        scanlineEdge(ac, bc, side, texture);
    }

    private void scanlineEdge(Edge a, Edge b, boolean side, Texture texture){
        Edge left = a;
        Edge right = b;
        if(side){
            Edge temp = left;
            left = right;
            right = temp;
        }

        int yStart = b.getYStart();
        int yEnd = b.getYEnd();
        for(int y = yStart; y < yEnd; y++){
            this.renderScanline(left, right, y, texture);
            left.stepAlong();
            right.stepAlong();
        }
    }

    public void clearZBuffer(){
        Arrays.fill(zBuffer, Float.MAX_VALUE);
    }

    private void renderScanline(Edge left, Edge right, int y, Texture texture){
        int xMin = (int) Math.ceil(left.getPosX());
        int xMax = (int) Math.ceil(right.getPosX());
        float xDist = right.getPosX() - left.getPosX();

        float xIntercept = xMin - left.getPosX();

        // Recalculate slopes. This prevents floating-point error accumulation:
        Vector2 newTexCoordXSlopes = right.getTexCoords().subtract(left.getTexCoords()).divide(xDist);
        float newInvZSlope = (right.getInvZ() - left.getInvZ())/xDist;
        float newZDepthSlope = (right.getZDepth() - left.getZDepth())/xDist;

        Vector2 texCoord = left.getTexCoords().add(newTexCoordXSlopes.multiply(xIntercept));
        float invZ = left.getInvZ() + newInvZSlope * xIntercept;
        float zDepth = left.getZDepth() + newZDepthSlope * xIntercept;

        for(int x = xMin; x < xMax; x++){
            int i = x + y * this.renderBuffer.width;
            if(zDepth < zBuffer[i]){
                zBuffer[i] = zDepth;
                float z = 1.0f/invZ;
                Vector2 src = texCoord.multiply(z).multiply(new Vector2(texture.getWidth() - 1f, texture.getHeight() - 1f));
                texture.copyTexel(Math.round(src.getX()), Math.round(src.getY()), this.renderBuffer, x, y);
            }
            invZ += newInvZSlope;
            texCoord = texCoord.add(newTexCoordXSlopes);
            zDepth += newZDepthSlope;
        }
    }
}
