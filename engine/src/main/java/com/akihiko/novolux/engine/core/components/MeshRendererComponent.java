package com.akihiko.novolux.engine.core.components;

import com.akihiko.novolux.ecs.Component;
import com.akihiko.novolux.engine.core.records.Mesh;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class MeshRendererComponent extends Component {

    public static final int id = Component.generateComponentId();

    private Mesh mesh;

    @Override
    public int ID() {
        return MeshRendererComponent.id;
    }
}
