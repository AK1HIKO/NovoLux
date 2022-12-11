package com.akihiko.novolux.demo.monkeyrotator;

import com.akihiko.novolux.ecs.Component;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 10/12/22
 */
public class MonkeyRotatorComponent extends Component {

    public static final int id = Component.generateComponentId();

    @Override
    public int ID() {
        return MonkeyRotatorComponent.id;
    }

    float degPerSecond;

    public MonkeyRotatorComponent(float degPerSecond) {
        this.degPerSecond = degPerSecond;
    }
}
