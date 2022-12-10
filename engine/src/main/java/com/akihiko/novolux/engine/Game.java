package com.akihiko.novolux.engine;

import com.akihiko.novolux.engine.core.scene.Scene;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Game implements Runnable{

    private Scene currentScene;
    public void loadScene(Scene newScene){
        // Synchronize somehow.
        this.currentScene = newScene;
        this.currentScene.create();
    }

    private Thread mainThread;
    private boolean isRunning = false;

    public synchronized void start(){
        this.isRunning = true;
        this.mainThread = new Thread(this, "GameLoop");
        this.mainThread.start();
    }

    public synchronized void stop() {
        try {
            this.mainThread.join();
            // If stopped successfully:
            this.isRunning = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long previousTime = System.nanoTime();
        while(isRunning){
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - previousTime) / 1e9f;
            previousTime = currentTime;

            this.currentScene.update(deltaTime);
//            this.gameWindow.getGameView().render();
        }
    }
}