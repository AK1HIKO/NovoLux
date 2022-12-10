package com.akihiko.novolux.demo;

import com.akihiko.novolux.engine.Application;
import com.akihiko.novolux.engine.utils.Logger;

public class Demo {

    public static void main(String[] args) {
        Application app = new Application("NovoLux Demo Game", 1366, 768);
        Application.getGameInstance().loadScene(new SandboxScene());
        Logger.info("Test Info Message from Demo.class");
        app.start();
        Logger.error("Test ERROR Message from Demo.class");
    }

}
