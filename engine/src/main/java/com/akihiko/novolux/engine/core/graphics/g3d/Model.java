package com.akihiko.novolux.engine.core.graphics.g3d;

import com.akihiko.novolux.engine.core.math.tensors.vector.Vector;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 09/12/22
 */
public class Model {

    private List<Vector4> positions = new ArrayList<>();
    private List<Vector2> texCoords = new ArrayList<>();
    private List<Vector4> normals = new ArrayList<>();
    private List<Vector4> tangents = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    public void calculateNormals() {
        for (int i = 0; i < indices.size(); i += 3) {
            int a = indices.get(i + 0);
            int b = indices.get(i + 1);
            int c = indices.get(i + 2);

            Vector4 v1 = positions.get(b).subtract(positions.get(a));
            Vector4 v2 = positions.get(c).subtract(positions.get(a));

            Vector4 normal = v1.cross(v2).normalized();

            normals.set(a, normals.get(a).add(normal));
            normals.set(b, normals.get(b).add(normal));
            normals.set(c, normals.get(c).add(normal));
        }

        normals.replaceAll(Vector::normalized);
    }

    public void calculateTangents() {
        for (int i = 0; i < indices.size(); i += 3) {
            int a = indices.get(i + 0);
            int b = indices.get(i + 1);
            int c = indices.get(i + 2);

            Vector4 edge1 = positions.get(b).subtract(positions.get(a));
            Vector4 edge2 = positions.get(c).subtract(positions.get(a));

            Vector2 deltaUV1 = texCoords.get(b).subtract(texCoords.get(a));
            Vector2 deltaUV2 = texCoords.get(c).subtract(texCoords.get(a));

            float dividend = (deltaUV1.getX() * deltaUV2.getY() - deltaUV2.getX() * deltaUV1.getY());
            float f = dividend == 0 ? 0.0f : 1.0f / dividend;

            Vector4 tangent = new Vector4(
                    f * (deltaUV2.getY() * edge1.getX() - deltaUV1.getY() * edge2.getX()),
                    f * (deltaUV2.getY() * edge1.getY() - deltaUV1.getY() * edge2.getY()),
                    f * (deltaUV2.getY() * edge1.getZ() - deltaUV1.getY() * edge2.getZ()),
                    0);

            tangents.set(a, tangents.get(a).add(tangent));
            tangents.set(b, tangents.get(b).add(tangent));
            tangents.set(c, tangents.get(c).add(tangent));
        }

        tangents.replaceAll(Vector::normalized);
    }

    public List<Vector4> getPositions() {
        return positions;
    }

    public List<Vector2> getTexCoords() {
        return texCoords;
    }

    public List<Vector4> getNormals() {
        return normals;
    }

    public List<Vector4> getTangents() {
        return tangents;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}
