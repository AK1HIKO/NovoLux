package com.akihiko.novolux.engine.core.components;

import com.akihiko.novolux.ecs.Component;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 17/11/22
 */
public class TagComponent extends Component {

    public static final int id = Component.generateComponentId();
    @Override
    public int ID() {
        return TagComponent.id;
    }

    private String tag;

}
