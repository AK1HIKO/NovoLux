package com.akihiko.novolux.engine.core.records;

import com.akihiko.novolux.engine.core.math.vector.Vector3;

import java.util.List;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public record Mesh(List<Vector3> verts, List<int[]> tris) { }
