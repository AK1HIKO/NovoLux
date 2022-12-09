package com.akihiko.novolux.engine.core.graphics.utils;

import com.akihiko.novolux.engine.core.graphics.g3d.Mesh;
import com.akihiko.novolux.engine.core.graphics.g3d.Model;

import java.nio.file.Path;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 26/11/22
 */
public interface ModelLoader {

    Model loadModel(Path meshFile);

}
