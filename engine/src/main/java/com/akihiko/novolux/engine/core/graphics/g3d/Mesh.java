package com.akihiko.novolux.engine.core.graphics.g3d;

import com.akihiko.novolux.engine.core.graphics.g3d.geometry.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public class Mesh {

    private final List<Vertex> verts;
    private final List<Integer> indices;

    public Mesh(Model model) {
        this.verts = new ArrayList<>();
        for (int i = 0; i < model.getPositions().size(); i++) {
            this.verts.add(new Vertex(model.getPositions().get(i), model.getTexCoords().get(i), model.getNormals().get(i)));
        }
        this.indices = model.getIndices();
    }

    public List<Vertex> getVerts() {
        return verts;
    }

    public List<Integer> getIndices() {
        return indices;
    }
}
