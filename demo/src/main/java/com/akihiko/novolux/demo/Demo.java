package com.akihiko.novolux.demo;

import com.akihiko.novolux.engine.Game;

public class Demo {

    public static void main(String[] args) {
        if(Game.initialize("NovoLux Demo Game", 900)){
            Game.getInstance().start();
        }else{
            // Something went wrong, exit the application.
            System.exit(0);
        }
    }

}
