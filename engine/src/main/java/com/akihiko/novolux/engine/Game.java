package com.akihiko.novolux.engine;

/**
 *
 * @author AK1HIKO
 * @project NovoLux
 * @created 15/11/22
 */
public class Game implements Runnable{

    private static Game instance;

    public static Game getInstance() {
        return instance;
    }

    public static boolean initialize(String title, int width){
        return initialize(title, width, 16d/9);
    }

    public static boolean initialize(String title, int width, double ratio){
        if(Game.instance != null)
            return false;

        Game.instance = new Game(title, width, (int) (width/ratio));
        return true;
    }


    private Window gameWindow;
    private Game(String title, int width, int height) {
        gameWindow = new Window(title, width, height);
        gameWindow.display();
    }

    private Thread mainThread;
    private boolean isRunning = false;

    public synchronized void start(){
        this.isRunning = true;
        this.mainThread = new Thread(this, "GameLoop");
        this.mainThread.start();
    }

    public synchronized void stop(){
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
        while(isRunning){
            this.gameWindow.gameView.render();
        }
    }
}