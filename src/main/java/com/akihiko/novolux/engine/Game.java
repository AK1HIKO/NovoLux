package com.akihiko.novolux.engine;

import com.akihiko.novolux.engine.core.Window;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Game {

    public Game(String name) {
        Window window = new Window(name, 500, 500);
        window.display();
    }
}
