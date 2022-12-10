package com.akihiko.novolux.engine.core.graphics.utils;

import com.akihiko.novolux.ecs.ECSRuntimeException;
import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.g3d.Model;
import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector2;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector3;
import com.akihiko.novolux.engine.core.math.tensors.vector.Vector4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class OBJModelLoader implements ModelLoader {

    @Override
    public Model loadModel(Path meshFile) {
        try {
            return parseObj(meshFile.toFile());
        } catch (FileNotFoundException e) {
            throw new ECSRuntimeException(e.getMessage());
        }
    }

    // Adapted from https://github.com/BennyQBD/3DEngineCpp/tree/a9264241bfa553ca1dde0277391c6c7e69854f26
    private Model parseObj(File file) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        Scanner sc = new Scanner(fis);

        OBJModel interm = new OBJModel();

        while (sc.hasNextLine()) {
            String ln = sc.nextLine();
            String[] tokens = ln.split(" ");
            // Removes empty entries:
            tokens = Arrays.stream(tokens).filter((str) -> !str.trim().isEmpty()).toList().toArray(String[]::new);

            if (ln == null || ln.trim().isEmpty() || ln.startsWith("#"))
                continue;

            switch (tokens[0]){
                case "v":
                    interm.positions.add(new Vector4(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    ));
                    break;
                case "vt":
                    interm.texCoords.add(new Vector2(
                            Float.parseFloat(tokens[1]),
                            1.0f - Float.parseFloat(tokens[2])
                    ));
                    break;
                case "vn":
                    interm.normals.add(new Vector4(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]),
                            0
                    ));
                    break;
                case "f":
                    for(int i = 0; i < tokens.length - 3; i++)
                    {
                        interm.indices.add(parseIndex(tokens[1    ], interm));
                        interm.indices.add(parseIndex(tokens[2 + i], interm));
                        interm.indices.add(parseIndex(tokens[3 + i], interm));
                    }
                    break;
            }
        }
        sc.close();
        return interm.toModel();
    }

    private Index parseIndex(String token, OBJModel out)
    {
        String[] values = token.split("/");

        Index result = new Index();
        result.ivert = Integer.parseInt(values[0]) - 1;

        if(values.length > 1)
        {
            if(!values[1].isEmpty())
            {
                out.hasTexCoords = true;
                result.itexCoord = Integer.parseInt(values[1]) - 1;
            }

            if(values.length > 2)
            {
                out.hasNormals = true;
                result.inormal = Integer.parseInt(values[2]) - 1;
            }
        }

        return result;
    }

    private static class OBJModel{

        private List<Vector4> positions = new ArrayList<>();
        private List<Vector2> texCoords = new ArrayList<>();
        private List<Vector4> normals = new ArrayList<>();
        private List<Index> indices = new ArrayList<>();

        private boolean hasTexCoords = false;
        private boolean hasNormals = false;

        Model toModel(){
            Model result = new Model();
            Model normalModel = new Model();
            Map<Index, Integer> resultIndexMap = new HashMap<>();
            Map<Integer, Integer> normalIndexMap = new HashMap<>();
            Map<Integer, Integer> indexMap = new HashMap<>();

            for (Index currentIndex : indices) {
                Vector4 currentPosition = positions.get(currentIndex.ivert);
                Vector2 currentTexCoord = Vector2.ZERO();
                Vector4 currentNormal = Vector4.NULL();

                if (hasTexCoords)
                    currentTexCoord = texCoords.get(currentIndex.itexCoord);

                if (hasNormals)
                    currentNormal = normals.get(currentIndex.inormal);

                Integer modelVertexIndex = resultIndexMap.get(currentIndex);

                if (modelVertexIndex == null) {
                    modelVertexIndex = result.getPositions().size();
                    resultIndexMap.put(currentIndex, modelVertexIndex);

                    result.getPositions().add(currentPosition);
                    result.getTexCoords().add(currentTexCoord);
                    if (hasNormals)
                        result.getNormals().add(currentNormal);
                }

                Integer normalModelIndex = normalIndexMap.get(currentIndex.ivert);

                if (normalModelIndex == null) {
                    normalModelIndex = normalModel.getPositions().size();
                    normalIndexMap.put(currentIndex.ivert, normalModelIndex);

                    normalModel.getPositions().add(currentPosition);
                    normalModel.getTexCoords().add(currentTexCoord);
                    normalModel.getNormals().add(currentNormal);
                    normalModel.getTangents().add(Vector4.NULL());
                }

                result.getIndices().add(modelVertexIndex);
                normalModel.getIndices().add(normalModelIndex);
                indexMap.put(modelVertexIndex, normalModelIndex);
            }

            if(!hasNormals)
            {
                normalModel.calculateNormals();

                for(int i = 0; i < result.getPositions().size(); i++)
                    result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
            }

            normalModel.calculateTangents();

            for(int i = 0; i < result.getPositions().size(); i++)
                result.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));

            return result;
        }

    }

    private static class Index
    {
        private int ivert;
        private int itexCoord;
        private int inormal;

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (!(obj instanceof Index other))
                return false;

            return ivert == other.ivert
                    && itexCoord == other.itexCoord
                    && inormal == other.inormal;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(ivert, itexCoord, inormal);
        }
    }
}
