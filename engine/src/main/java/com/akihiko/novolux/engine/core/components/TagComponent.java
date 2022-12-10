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

    public TagComponent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public TagComponent setTag(String tag) {
        this.tag = tag;
        return this;
    }
}
