package com.akihiko.novolux.demo;

import com.akihiko.novolux.ecs.ComponentSystem;
import com.akihiko.novolux.engine.Application;

public class Demo {

    public static void main(String[] args) {
        Application app = new Application("NovoLux Demo Game", 1366, 768);
        app.getGameInstance().loadScene(new SandboxScene(app.getGameView()));
        app.start();
    }

}
