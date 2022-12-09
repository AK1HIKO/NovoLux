package com.akihiko.novolux.engine;

import com.akihiko.novolux.engine.utils.Logger;
import com.akihiko.novolux.engine.utils.NovoLuxRuntimeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author AK1HIKO
 * @project NovoLux
 * @created 27/11/22
 */
public class Application extends JFrame {

    private static Application instance;

    public static Application getInstance() {
        return Application.instance;
    }
    private final Game gameInstance;
    private final GameView gameView;
    public Application(String title, int width, int height) {
        super();
        if(Application.instance != null)
            throw new NovoLuxRuntimeException("Unable to create Application. An Application instance already exists!");

        this.setTitle(title);
        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Logger.dumpCore();
            }
        });

        this.gameView = new GameView(width, height);

        this.getContentPane().add(gameView);

        // So that our window size resizes according to the gameView
        this.pack();

        // After calculating the size of the window, center it:
        this.setLocationRelativeTo(null);
        this.gameInstance = new Game();

        Application.instance = this;
        this.gameView.init();
    }

    public void start() {
        this.setVisible(true);
        this.gameInstance.start();
    }

    public Game getGameInstance() {
        return gameInstance;
    }

    public GameView getGameView() {
        return gameView;
    }
}
